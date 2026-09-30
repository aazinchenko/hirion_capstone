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
  if (/-Dqa\.mailbox/.test(command)) {
    console.log(JSON.stringify({
      hookSpecificOutput: {
        hookEventName: "PreToolUse",
        permissionDecision: "ask",
        permissionDecisionReason:
          "Live journey run (-Dqa.mailbox) creates a real hirion.ch account: it needs an explicit human \"да\".",
      },
    }));
  }
});
