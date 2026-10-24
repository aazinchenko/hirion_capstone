// PreToolUse hook (Bash / PowerShell): a command that passes -Dqa.mailbox runs the live journey, which
// creates and deletes a REAL account on hirion.ch. Allow rules such as Bash(mvn -q test:*) would let it run
// silently, so this hook turns it into a human prompt ("ask"). See AGENTS.md and docs/autonomy-log.md #7.
let input = "";
process.stdin.on("data", (chunk) => (input += chunk));
process.stdin.on("end", () => {
  let command = "";
  try {
    command = JSON.parse(input)?.tool_input?.command ?? "";
  } catch {
    return; // not a tool call we understand: no opinion
  }
  // -Dqa.ai=true makes the Premium tests press the AI buttons, which cost the site owner money (PREM, 2026-10-24).
  const reasons = [];
  if (/-Dqa\.mailbox/.test(command)) {
    reasons.push("Live journey run (-Dqa.mailbox) creates a real hirion.ch account");
  }
  if (/-Dqa\.ai/.test(command)) {
    reasons.push("-Dqa.ai presses the AI buttons (owner's AI costs)");
  }
  if (reasons.length > 0) {
    console.log(JSON.stringify({
      hookSpecificOutput: {
        hookEventName: "PreToolUse",
        permissionDecision: "ask",
        permissionDecisionReason: reasons.join("; ") + ": it needs an explicit human \"да\".",
      },
    }));
  }
});
