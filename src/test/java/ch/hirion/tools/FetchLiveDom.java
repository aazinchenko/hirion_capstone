package ch.hirion.tools;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import java.nio.file.*;
/** usage: FetchLiveDom <url> [css-hint] [storageState.json] */
public class FetchLiveDom {
  public static void main(String[] args) throws Exception {
    String url = args.length > 0 ? args[0] : "https://hirion.ch";
    String hint = args.length > 1 ? args[1] : "main";
    String storage = args.length > 2 ? args[2] : null;
    Files.createDirectories(Paths.get(".agent-log"));
    try (Playwright pw = Playwright.create()) {
      Browser browser = pw.chromium().launch();
      Browser.NewContextOptions options = new Browser.NewContextOptions();
      if (storage != null) options.setStorageStatePath(Paths.get(storage));
      Page page = browser.newContext(options).newPage();
      page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
      String html = (String) page.evaluate(
            "sel => (document.querySelector(sel) || document.body).outerHTML.slice(0, 6000)", hint);
      Files.writeString(Paths.get(".agent-log/dom-snippet.html"), html);
      System.out.printf("Saved %d chars from %s -> .agent-log/dom-snippet.html%n",
            html.length(), url);
    }
  }
}
