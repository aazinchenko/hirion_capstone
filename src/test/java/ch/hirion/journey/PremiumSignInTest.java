package ch.hirion.journey;

import ch.hirion.support.BaseTest;
import ch.hirion.support.PremiumAccountTest;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Group PREM gate: sign-in to the owner-granted Premium account (PREM-15) and its plan state (PREM-16),
 * openspec/changes/use-existing-premium-account/specs/premium-trial.
 *
 * <p>Both tests are in group "premium"; every other Premium test depends on it. Nothing on Plan & Billing is clicked.
 * The sign-in runs in its own context WITHOUT tracing, so the typed password never lands in a trace file. The email
 * and password are never printed (AGENTS.md).
 */
@Epic("User journey")
@Feature("premium-trial")
public class PremiumSignInTest extends PremiumAccountTest {

  /** Reserved for disposable users that JourneyCleanup deletes: never the permanent Premium account. */
  private static final Pattern DISPOSABLE = Pattern.compile("\\+hirion-qa-\\d+@");
  private static final LocatorAssertions.IsVisibleOptions SLOW =
        new LocatorAssertions.IsVisibleOptions().setTimeout(30_000);

  // PREM-15

  @Story("PREM-15 The suite signs in to the existing Premium account")
  @Test(priority = 101, groups = "premium", description = "PREM-15 Sign-in to the Premium account")
  public void prem15_signInToPremiumAccount() throws IOException {
    String email = env(EMAIL_ENV);
    String password = env(PASSWORD_ENV);
    Assert.assertFalse(DISPOSABLE.matcher(email).find(),
          EMAIL_ENV + " is a disposable +hirion-qa-N address; the Premium suite needs the permanent Premium account");
    Files.deleteIfExists(SESSION); // never reuse a session of an earlier run

    BrowserContext login = browser.newContext(new com.microsoft.playwright.Browser.NewContextOptions()
          .setBaseURL(BaseTest.BASE_URL));
    try {
      login.setDefaultTimeout(15_000);
      Page p = login.newPage();
      p.navigate("/login", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
            .setTimeout(30_000));
      p.waitForFunction("() => !('$_TSR' in window)", null, new Page.WaitForFunctionOptions().setTimeout(30_000));
      p.getByLabel("Email", new Page.GetByLabelOptions().setExact(true)).fill(email);
      p.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).fill(password);
      p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in").setExact(true)).click();
      try {
        p.waitForURL(url -> URI.create(url).getPath().startsWith("/dashboard"),
              new Page.WaitForURLOptions().setTimeout(30_000));
      } catch (TimeoutError notSignedIn) {
        Assert.fail("the Premium account did not reach /dashboard after \"Sign in\" (still on " + URI.create(p.url())
              .getPath() + "): check " + EMAIL_ENV + " / " + PASSWORD_ENV + " and that the account still exists");
      }
      login.storageState(new BrowserContext.StorageStateOptions().setPath(SESSION));
    } finally {
      login.close();
    }
  }

  // PREM-16

  @Story("PREM-16 Plan & Billing shows the Premium plan")
  @Test(priority = 102, groups = "premium", dependsOnMethods = "prem15_signInToPremiumAccount",
        description = "PREM-16 Premium plan state")
  public void prem16_premiumPlanState() {
    open("/settings?section=plan");
    assertThat(exactText("You're on the Premium plan.")).isVisible(SLOW);
    Locator trial = page.getByRole(AriaRole.MAIN).getByRole(AriaRole.BUTTON,
          new Locator.GetByRoleOptions().setName("Start 7-day free trial").setExact(true));
    assertThat(trial).hasCount(0);
  }

  /** The session file holds the real account's cookies: it does not outlive the suite. */
  @AfterSuite(alwaysRun = true)
  public void deleteSession() throws IOException {
    Files.deleteIfExists(SESSION);
  }

  // ---------------------------------------------------------------------------------------------

  private static String env(String name) {
    String value = System.getenv(name);
    Assert.assertTrue(value != null && !value.isBlank(),
          "environment variable " + name + " is not set (the Premium account's credentials; see AGENTS.md)");
    return value.trim();
  }

  private Locator exactText(String text) {
    return page.getByText(text, new Page.GetByTextOptions().setExact(true));
  }
}
