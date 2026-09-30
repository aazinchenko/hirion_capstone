"""Actual token use and list-price cost per OpenSpec change, from the Claude Code session transcripts.

Usage:  python scripts/usage-by-change.py [~/.claude/projects]

Every assistant message in the transcripts of both working folders (the project folder and its parent) is
assigned to a change by its timestamp: a change owns the time after the previous change's archive commit up
to its own archive commit; change 1 starts at its propose commit. Before that is "setup", after the last
archive "after change 4". A message id is counted once. Cost = list price x tokens (an estimate: the work ran
on a subscription, so there is no invoice).
"""
import collections
import datetime as dt
import glob
import json
import os
import subprocess
import sys

# USD per 1M tokens (claude-api reference, cached 2026-09-25). Cache write: 5 min = 1.25x input, 1 h = 2x input.
PRICES = {
    "claude-opus-5-5": {"in": 4.00, "out": 20.00, "read": 0.20},
    "claude-sonnet-5": {"in": 2.00, "out": 10.00, "read": 0.20},
}
# (label, commit that opens the window or None, commit that closes it)
CHANGES = [
    ("1 add-home-smoke", "897ed3a", "49d0c5a"),
    ("2 add-public-coverage", None, "faa59df"),
    ("3 add-user-journey", None, "5e844a6"),
    ("4 update-signup-steps", None, "d1b34a0"),
]
FOLDERS = {"d--demo-hirion-capstone-hirion-capstone": "project folder (author agent)",
           "d--demo-hirion-capstone": "parent folder (reviewer)"}


def commit_time(sha):
    iso = subprocess.check_output(["git", "log", "-1", "--format=%cI", sha], text=True).strip()
    return dt.datetime.fromisoformat(iso).astimezone(dt.timezone.utc)


def windows():
    bounds, start = [], commit_time(CHANGES[0][1])
    for label, _, end_sha in CHANGES:
        end = commit_time(end_sha)
        bounds.append((label, start, end))
        start = end
    return bounds


def change_of(ts, bounds):
    if ts <= bounds[0][1]:
        return "0 setup (before change 1)"
    for label, start, end in bounds:
        if start < ts <= end:
            return label
    return "5 after change 4 (steps 27-28 docs)"


def cost(model, u):
    p = PRICES.get(model)
    if p is None:
        return 0.0
    cc = u.get("cache_creation") or {}
    w5 = cc.get("ephemeral_5m_input_tokens", 0)
    w1h = cc.get("ephemeral_1h_input_tokens", 0)
    if not cc:  # no split recorded: count the write as 1 h (the subscription default)
        w1h = u.get("cache_creation_input_tokens", 0)
    return (u.get("input_tokens", 0) * p["in"] + u.get("output_tokens", 0) * p["out"]
            + u.get("cache_read_input_tokens", 0) * p["read"]
            + w5 * p["in"] * 1.25 + w1h * p["in"] * 2) / 1e6


def main():
    root = os.path.expanduser(sys.argv[1] if len(sys.argv) > 1 else "~/.claude/projects")
    bounds = windows()
    messages = {}  # message id -> (timestamp, folder, is_subagent, model, usage); last line wins
    for path in glob.glob(os.path.join(root, "*", "**", "*.jsonl"), recursive=True):
        folder = os.path.relpath(path, root).split(os.sep)[0].lower()
        if folder not in FOLDERS:
            continue
        sub = "subagents" in path
        with open(path, encoding="utf-8", errors="ignore") as fh:
            for line in fh:
                try:
                    j = json.loads(line)
                except ValueError:
                    continue
                m = j.get("message")
                if j.get("type") != "assistant" or not isinstance(m, dict) or not m.get("usage"):
                    continue
                ts = dt.datetime.fromisoformat(j["timestamp"].replace("Z", "+00:00"))
                messages[m.get("id") or id(m)] = (ts, folder, sub, m.get("model"), m["usage"])

    table = collections.defaultdict(lambda: [0, 0, 0.0])  # (change, who) -> msgs, output, usd
    for ts, folder, sub, model, u in messages.values():
        who = FOLDERS[folder] + (", subagent" if sub else "")
        for key in ((change_of(ts, bounds), who), (change_of(ts, bounds), "TOTAL")):
            row = table[key]
            row[0] += 1
            row[1] += u.get("output_tokens", 0)
            row[2] += cost(model, u)

    print(f"generated {dt.datetime.now(dt.timezone.utc):%Y-%m-%d %H:%M} UTC, {len(messages)} assistant messages")
    print(f"{'change':38} {'who':44} {'msgs':>5} {'output tok':>11} {'USD':>9}")
    for (change, who), (n, out, usd) in sorted(table.items(), key=lambda kv: (kv[0][0], kv[0][1] == "TOTAL", kv[0][1])):
        print(f"{change:38} {who:44} {n:5} {out:11,} {usd:9.2f}")
    grand = [sum(v[i] for (c, w), v in table.items() if w == "TOTAL") for i in range(3)]
    print(f"{'ALL':38} {'TOTAL':44} {grand[0]:5} {grand[1]:11,} {grand[2]:9.2f}")


if __name__ == "__main__":
    main()
