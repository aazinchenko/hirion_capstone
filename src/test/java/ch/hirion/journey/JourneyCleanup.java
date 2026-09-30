package ch.hirion.journey;

import ch.hirion.support.BaseTest;
import ch.hirion.support.TestUser;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Deletes the disposable journey user (change add-user-journey, tasks 1.1 / 1.2, design.md "Cleanup").
 * HUMAN-OWNED: denied to the agent in .claude/settings.json, so the guard below cannot be weakened.
 *
 * <p>Two entry points share deleteUser(): before the suite it removes a user left by an interrupted run
 * (Ctrl+C skips @AfterSuite), after the suite it removes this run's user, even when tests failed.
 */
public class JourneyCleanup {

  /** DEL-01: the only accounts this class may ever delete. Same pattern as TestUser.QA_EMAIL. */
  static final Pattern GUARD = Pattern.compile("\\+hirion-qa-\\d+@");

  @BeforeSuite(alwaysRun = true)
  public void deleteLeftoverUser() {
    TestUser.load().ifPresent(user -> {
      log("leftover state from an earlier run: " + user.email + " (accountCreated=" + user.accountCreated + ")");
      deleteUser(user);
    });
  }

  @AfterSuite(alwaysRun = true)
  public void deleteRunUser() {
    TestUser.load().ifPresent(user -> {
      log("deleting this run's user: " + user.email + " (accountCreated=" + user.accountCreated + ")");
      deleteUser(user);
    });
  }

  void deleteUser(TestUser user) {
    // 1. Guard first: nothing is opened or touched for an email outside the QA pattern.
    if (user.email == null || !GUARD.matcher(user.email).find()) {
      throw new IllegalStateException("Refusing to delete a non-test account: " + user.email);
    }
    try (Playwright pw = Playwright.create()) {
      Browser browser = pw.chromium().launch();
      // 2. Sign in with the current password, then with the pending one (SET-5 may have changed it).
      Page page = signIn(browser, user.email, user.currentPassword);
      if (page == null && user.pendingPassword != null) {
        page = signIn(browser, user.email, user.pendingPassword);
      }
      if (page == null) {
        if (!user.accountCreated && !user.submitted) {
          log("sign-in failed and the account was never submitted: removing the state file only");
          TestUser.deleteState();
          return;
        }
        throw new IllegalStateException("Could not sign in to delete " + user.email
              + " -- delete it by hand; state kept in " + TestUser.STATE);
      }
      // 3. Settings > Security > "Delete account" > confirm "Delete" (texts: proposal Open question 2).
      open(page, "/settings?section=security");
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete account").setExact(true))
            .click();
      Locator dialog = page.getByRole(AriaRole.DIALOG).or(page.getByRole(AriaRole.ALERTDIALOG));
      dialog.waitFor();
      log("dialog: " + dialog.innerText().replace('\n', ' '));
      dialog.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Delete").setExact(true))
            .click();
      page.waitForURL(url -> "/".equals(URI.create(url).getPath()),
            new Page.WaitForURLOptions().setTimeout(15_000));
      log("redirected to " + page.url() + "; page text after delete: " + firstLine(page));
      // 4. The deleted account must not sign in any more (plan DEL-03).
      Page again = signIn(browser, user.email, user.currentPassword);
      if (again != null) {
        throw new IllegalStateException("Account still signs in after deletion: " + user.email);
      }
      log("deleted: " + user.email + " no longer signs in");
    }
    // 5. Nothing of this user stays on disk (plan DEL-04).
    TestUser.deleteState();
    deleteQuietly(".auth/user.json");
  }

  /** Signs in on a fresh context; returns the page on /dashboard, or null when sign-in fails. */
  private Page signIn(Browser browser, String email, String password) {
    if (password == null) {
      return null;
    }
    BrowserContext context = browser.newContext(new Browser.NewContextOptions().setBaseURL(BaseTest.BASE_URL));
    context.setDefaultTimeout(15_000);
    Page page = context.newPage();
    open(page, "/login");
    page.getByLabel("Email", new Page.GetByLabelOptions().setExact(true)).fill(email);
    page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).fill(password);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in").setExact(true)).click();
    try {
      page.waitForURL(url -> URI.create(url).getPath().startsWith("/dashboard"),
            new Page.WaitForURLOptions().setTimeout(15_000));
      return page;
    } catch (TimeoutError notSignedIn) {
      log("sign-in did not reach /dashboard; page says: " + firstLine(page));
      context.close();
      return null;
    }
  }

  private static void open(Page page, String path) {
    page.navigate(path, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
          .setTimeout(30_000));
    page.waitForFunction("() => !('$_TSR' in window)", null,
          new Page.WaitForFunctionOptions().setTimeout(30_000));
  }

  private static String firstLine(Page page) {
    String text = page.locator("html").innerText(); // locator-exception: <html>, whole page text for the log
    return text.length() > 300 ? text.substring(0, 300).replace('\n', ' ') + "..." : text.replace('\n', ' ');
  }

  private static void deleteQuietly(String path) {
    try {
      Files.deleteIfExists(Paths.get(path));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void log(String message) {
    System.out.println("[JourneyCleanup] " + message);
  }
}
