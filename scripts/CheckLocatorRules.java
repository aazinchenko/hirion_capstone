import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;
public class CheckLocatorRules {
  record Rule(Pattern pattern, String reason, Pattern allowIf) {}
  static final List<Rule> RULES = List.of(
        new Rule(Pattern.compile("nth-child", Pattern.CASE_INSENSITIVE),
              "nth-child locator is forbidden by AGENTS.md", null),
        new Rule(Pattern.compile("waitForTimeout|Thread\\.sleep"),
              "hardcoded wait is forbidden by AGENTS.md", null),
        new Rule(Pattern.compile("\\.css-[a-z0-9]+", Pattern.CASE_INSENSITIVE),
              "generated CSS class locator is forbidden", null),
        new Rule(Pattern.compile("\\.faq-item-\\d", Pattern.CASE_INSENSITIVE),
              "guessed positional class, not a role-based locator", null),
        new Rule(Pattern.compile("\\.locator\\("),
              "raw locator without '// locator-exception:'", Pattern.compile("locator-exception:")),
        new Rule(Pattern.compile("setName\\(\"Continue\"\\)(?!\\.setExact\\(true\\))"),
              "'Continue' also matches OAuth buttons: use setExact(true) or role(...)", null));
  public static void main(String[] args) throws Exception {
    Path root = Paths.get(args.length > 0 ? args[0] : "src/test/java");
    List<Path> files;
    try (Stream<Path> walk = Files.walk(root)) {
      files = walk.filter(p -> p.toString().endsWith(".java")).toList();
    }
    boolean failed = false;
    for (Path file : files) {
      List<String> lines = Files.readAllLines(file);
      for (int i = 0; i < lines.size(); i++) {
        for (Rule rule : RULES) {
          String line = lines.get(i);
          boolean allowed = rule.allowIf() != null && rule.allowIf().matcher(line).find();
          if (rule.pattern().matcher(line).find() && !allowed) {
            System.err.printf("REJECTED: %s:%d -- %s%n", file, i + 1, rule.reason());
            failed = true;
          }
        }
      }
    }
    if (failed) System.exit(1);
    System.out.println("PASS: no forbidden patterns");
  }
}