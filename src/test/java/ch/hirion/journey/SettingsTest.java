package ch.hirion.journey;

import ch.hirion.support.AuthenticatedTest;
import ch.hirion.support.TestUser;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.WaitUntilState;
import org.opentest4j.AssertionFailedError;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Groups SET + DEL: /settings of the registered test user, openspec/changes/add-user-journey/specs/account-settings
 * and specs/account-deletion.
 *
 * <p>Starts signed in from .auth/user.json (written by reg9) and is skipped when group "registered" did not
 * pass. Sections are opened by the deep links /settings?section=security and ?section=plan (workaround for
 * D9); only the D9 tests click "Profile" / "Security". Never clicked: "Delete account" (only JourneyCleanup
 * deletes), "Upload photo", "Replace", "Re-analyse", anything in Plan & Billing. No test types a valid
 * foreign email: the site would send a confirmation link to it. SET-5 changes the password and runs last.
 * Emails and passwords are never printed.
 */
public class SettingsTest extends AuthenticatedTest {

  private static final Pattern DASHBOARD_URL = Pattern.compile("/dashboard([?#].*)?$");
  private static final List<String> SECTIONS = List.of("Profile", "Security", "Plan & Billing");
  private static final List<String> PROFILE_FIELDS =
        List.of("First name", "Last name", "Email", "Phone", "LinkedIn URL", "Headline", "Short bio");
  private static final List<String> REQUIRED_FIELDS = List.of("First name", "Last name", "Email");
  private static final List<String> PASSWORD_FIELDS =
        List.of("Current password", "New password", "Confirm new password");
  /** SET-6 values from the spec, in the order Phone, LinkedIn URL, Headline, Short bio. */
  private static final Map<String, String> PROFILE_VALUES = Map.of(
        "Phone", "79 000 00 00",
        "LinkedIn URL", "https://www.linkedin.com/in/qa-tester-example",
        "Headline", "QA Automation Engineer (test account)",
        "Short bio", "Automated test account. Created and deleted by Playwright.");
  /** Not an email address at all, so it can never reach a real mailbox (D10). */
  private static final String INVALID_EMAIL = "not-an-email";
  private static final double PROFILE_TIMEOUT = 15_000;
  private static final LocatorAssertions.IsVisibleOptions SLOW =
        new LocatorAssertions.IsVisibleOptions().setTimeout(15_000);
  /** 3 s timeouts for the KNOWN BUG assertions, like QUICK_ATTR. */
  private static final LocatorAssertions.IsHiddenOptions QUICK_HIDDEN =
        new LocatorAssertions.IsHiddenOptions().setTimeout(3000);
  private static final LocatorAssertions.IsVisibleOptions QUICK_VISIBLE =
        new LocatorAssertions.IsVisibleOptions().setTimeout(3000);
  private static final SecureRandom RANDOM = new SecureRandom();

  // ---------------------------------------------------------------------------------------------
  // SET-1

  @Test(priority = 1, dependsOnGroups = "registered", description = "SET-1 Section buttons")
  public void set1_sectionButtons() {
    open("/settings");
    for (String name : SECTIONS) {
      assertThat(role(AriaRole.BUTTON, name)).isVisible(SLOW);
    }
  }

  @Test(priority = 2, dependsOnGroups = "registered", description = "SET-1 Security deep link")
  public void set1_securityDeepLink() {
    open("/settings?section=security");
    assertThat(field("Current password")).isVisible(SLOW);
    assertThat(role(AriaRole.BUTTON, "Update password")).isVisible();
  }

  // SET-2 (Profile is the default section of /settings)

  @Test(priority = 3, dependsOnGroups = "registered", description = "SET-2 Profile is rendered")
  public void set2_profileRendered() {
    openProfile(registeredUser());
    assertThat(role(AriaRole.BUTTON, "Upload photo")).isVisible(); // presence only
    for (String label : PROFILE_FIELDS) {
      assertThat(field(label)).isVisible();
    }
    assertThat(role(AriaRole.COMBOBOX, "Country")).isVisible(); // not the "Country code" picker next to Phone
    for (String name : List.of("View", "Re-analyse", "Replace", "Save changes")) {
      assertThat(role(AriaRole.BUTTON, name)).isVisible(); // presence only: the CV is never changed
    }
    assertThat(exactText("Skills").first()).isVisible();
  }

  @Test(priority = 4, dependsOnGroups = "registered", expectedExceptions = AssertionFailedError.class,
        expectedExceptionsMessageRegExp = "(?s).*not marked required.*",
        description = "SET-2 Required profile fields -- KNOWN BUG D14: only a visual \"*\" in the label, the "
              + "inputs are not required / aria-required")
  public void set2_requiredProfileFields() {
    openProfile(registeredUser());
    List<String> notRequired = new ArrayList<>();
    for (String label : REQUIRED_FIELDS) {
      Locator input = field(label);
      input.waitFor(); // precondition: a missing field is a TimeoutError, never the known bug
      boolean required = (Boolean) input.evaluate(
            "e => e.required === true || e.getAttribute('aria-required') === 'true'");
      if (!required) {
        notRequired.add(label);
      }
    }
    if (!notRequired.isEmpty()) {
      throw new AssertionFailedError("fields not marked required: " + notRequired);
    }
  }

  @Test(priority = 5, dependsOnGroups = "registered", description = "SET-2 CV cannot be removed")
  public void set2_cvCannotBeRemoved() {
    openProfile(registeredUser());
    assertThat(role(AriaRole.BUTTON, "Replace")).isVisible(); // the CV block is rendered, so the check below means something
    assertThat(role(AriaRole.BUTTON, "Remove")).hasCount(0);
    assertThat(role(AriaRole.BUTTON, "Delete")).hasCount(0);
  }

  // SET-3 (known defect D9: opened by clicking "Profile", not by a deep link)

  @Test(priority = 6, dependsOnGroups = "registered", expectedExceptions = AssertionFailedError.class,
        expectedExceptionsMessageRegExp = "(?s).*hidden.*",
        description = "SET-3 Fresh profile has no unsaved-changes notice -- KNOWN BUG D9: \"You have unsaved "
              + "changes\" is shown right after load")
  public void set3_freshProfileNoUnsavedNotice() {
    TestUser user = registeredUser();
    open("/settings");
    role(AriaRole.BUTTON, "Profile").click();
    waitForProfile(user); // precondition: TimeoutError / TestNG, never the expected exception
    assertThat(page.getByText("You have unsaved changes").first()).isHidden(QUICK_HIDDEN);
  }

  @Test(priority = 7, dependsOnGroups = "registered", expectedExceptions = AssertionFailedError.class,
        expectedExceptionsMessageRegExp = "(?s).*Leave without saving.*",
        description = "SET-3 Switching section without edits -- KNOWN BUG D9: \"Unsaved changes -- Leave without "
              + "saving?\" opens")
  public void set3_switchSectionWithoutEdits() {
    TestUser user = registeredUser();
    open("/settings");
    role(AriaRole.BUTTON, "Profile").click();
    waitForProfile(user);
    role(AriaRole.BUTTON, "Security").click();
    // Wait until the switch has an outcome: the Security form OR the D9 dialog. Neither -> TimeoutError (red).
    // Only the dialog assertion may count as the known bug (message regex); a missing Security form after
    // a hidden dialog is a real failure. Neither "Stay on this page" nor "Leave without saving" is clicked.
    Locator security = field("Current password");
    Locator dialog = page.getByText("Leave without saving?").first();
    security.or(dialog).first().waitFor();
    assertThat(dialog).isHidden(QUICK_HIDDEN);
    assertThat(security).isVisible(QUICK_VISIBLE);
  }

  // SET-4 (known defect D10: the invalid email never leaves the browser)

  @Test(priority = 8, dependsOnGroups = "registered", expectedExceptions = AssertionFailedError.class,
        expectedExceptionsMessageRegExp = "(?s).*not-an-email.*",
        description = "SET-4 Invalid email is not sent -- KNOWN BUG D10: the invalid email is sent and saved")
  public void set4_invalidEmailNotSent() {
    TestUser user = registeredUser();
    openProfile(user);
    List<String> sent = Collections.synchronizedList(new ArrayList<>());
    // Catch-all on every host: the profile may be saved straight to a backend host (design.md "D10").
    page.route("**/*", route -> {
      String body = route.request().postData();
      if (!"GET".equals(route.request().method()) && body != null && body.contains(INVALID_EMAIL)) {
        sent.add(route.request().method() + " " + route.request().url().replaceAll("[?#].*", "")); // no body, no query
        route.abort(); // nothing is saved, even though the site tries
      } else {
        route.resume();
      }
    });
    try {
      field("Email").fill(INVALID_EMAIL);
      role(AriaRole.BUTTON, "Save changes").click();
      try {
        page.waitForCondition(() -> !sent.isEmpty(), new Page.WaitForConditionOptions().setTimeout(3000));
      } catch (PlaywrightException nothingSent) {
        // no request within 3 s: the expected (correct) behaviour
      }
      if (!sent.isEmpty()) {
        throw new AssertionFailedError("expected 0 non-GET requests with \"" + INVALID_EMAIL
              + "\" in the body, but " + sent.size() + " were sent (aborted): " + sent, 0, sent.size());
      }
    } finally {
      // Safety check through TestNG, never masked by expectedExceptions: the account email is unchanged.
      page.unrouteAll();
      page.onDialog(dialog -> { // a beforeunload prompt for the dirty form must not block the reload
        if ("beforeunload".equals(dialog.type())) dialog.accept(); else dialog.dismiss();
      });
      reload();
      waitForProfile(user);
    }
  }

  // SET-6

  @Test(priority = 9, dependsOnGroups = "registered", description = "SET-6 Profile survives a reload")
  public void set6_profileSurvivesReload() {
    TestUser user = registeredUser();
    openProfile(user);
    PROFILE_VALUES.forEach((label, value) -> field(label).fill(value)); // name and email are not touched
    role(AriaRole.BUTTON, "Save changes").click();
    assertThat(notifications().getByText("Profile updated", exactOptions())).isVisible(SLOW);

    reload();
    waitForProfile(user);
    PROFILE_VALUES.forEach((label, value) -> assertThat(field(label)).hasValue(value));
  }

  // SET-8 (nothing in Plan & Billing is clicked)

  @Test(priority = 10, dependsOnGroups = "registered", description = "SET-8 Free plan is shown")
  public void set8_freePlanShown() {
    open("/settings?section=plan");
    assertThat(exactText("You're currently on the Free plan.")).isVisible(SLOW);
    assertThat(role(AriaRole.BUTTON, "Current plan")).isVisible(); // presence only
  }

  // SET-5 (form)

  @Test(priority = 11, dependsOnGroups = "registered", description = "SET-5 Security form is rendered")
  public void set5_securityFormRendered() {
    open("/settings?section=security");
    for (String label : PASSWORD_FIELDS) {
      assertThat(field(label)).isVisible(SLOW); // exact: every "Show password" button also matches
    }
    assertThat(role(AriaRole.BUTTON, "Show password")).hasCount(3);
    assertThat(role(AriaRole.BUTTON, "Update password")).isVisible();
  }

  // DEL-1 ("Delete account" is checked, never clicked: only JourneyCleanup deletes the account)

  @Test(priority = 12, dependsOnGroups = "registered", description = "DEL-1 Delete account button is present")
  public void del1_deleteAccountButtonPresent() {
    open("/settings?section=security");
    Locator update = role(AriaRole.BUTTON, "Update password");
    Locator delete = role(AriaRole.BUTTON, "Delete account");
    assertThat(update).isVisible(SLOW);
    assertThat(delete).isVisible();
    BoundingBox updateBox = update.boundingBox();
    BoundingBox deleteBox = delete.boundingBox();
    Assert.assertTrue(deleteBox.y > updateBox.y, "\"Delete account\" is not below \"Update password\"");
  }

  // SET-5 (last: changes the password)

  @Test(priority = 13, dependsOnGroups = "registered", description = "SET-5 New password signs in")
  public void set5_newPasswordSignsIn() {
    TestUser user = registeredUser();
    String newPassword = newPassword(user);
    open("/settings?section=security");
    field("Current password").fill(user.currentPassword);
    field("New password").fill(newPassword);
    field("Confirm new password").fill(newPassword);

    user.pendingPassword = newPassword;
    user.save(); // before the click: cleanup falls back to it if the run dies right after the update
    role(AriaRole.BUTTON, "Update password").click();
    assertThat(notifications().getByText("Password updated", exactOptions())).isVisible(SLOW);

    BrowserContext guest = browser.newContext(new Browser.NewContextOptions().setBaseURL(BASE_URL));
    try {
      Page login = guest.newPage();
      login.navigate("/login", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
            .setTimeout(30_000));
      login.waitForFunction("() => !('$_TSR' in window)", null, // hydrated, as BaseTest.open() waits
            new Page.WaitForFunctionOptions().setTimeout(30_000));
      login.getByLabel("Email", new Page.GetByLabelOptions().setExact(true)).fill(user.email);
      login.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).fill(newPassword);
      login.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in").setExact(true)).click();
      assertThat(login).hasURL(DASHBOARD_URL, new PageAssertions.HasURLOptions().setTimeout(30_000));
    } finally {
      guest.close();
    }
    user.currentPassword = newPassword; // promoted only after the new password signed in
    user.pendingPassword = null;
    user.save();
  }

  // ---------------------------------------------------------------------------------------------
  // Helpers. None of them clicks "Delete account", "Upload photo", "Replace", "Re-analyse" or Plan & Billing.

  /** /settings shows Profile by default; waits until the form holds the test user's data. */
  private void openProfile(TestUser user) {
    open("/settings");
    waitForProfile(user);
  }

  /**
   * Precondition (never an AssertionFailedError, so it cannot pass as a KNOWN BUG): the Email field shows the
   * test user's email. Also the SET-4 safety check. The email itself is never printed.
   */
  private void waitForProfile(TestUser user) {
    try {
      page.waitForCondition(() -> user.email.equals(field("Email").inputValue()),
            new Page.WaitForConditionOptions().setTimeout(PROFILE_TIMEOUT));
    } catch (PlaywrightException e) {
      Assert.fail("Profile \"Email\" does not show the test user's email after "
            + (int) (PROFILE_TIMEOUT / 1000) + " s -- if the account email changed, JourneyCleanup will fail loudly");
    }
  }

  private Locator notifications() {
    return page.getByRole(AriaRole.REGION,
          new Page.GetByRoleOptions().setName(Pattern.compile("^Notifications")));
  }

  private Locator exactText(String text) {
    return page.getByText(text, new Page.GetByTextOptions().setExact(true));
  }

  private static Locator.GetByTextOptions exactOptions() {
    return new Locator.GetByTextOptions().setExact(true);
  }

  /** 16 characters with upper, lower, digit and symbol, different from the current and pending password. */
  private static String newPassword(TestUser user) {
    String upper = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    String lower = "abcdefghijkmnopqrstuvwxyz";
    String digits = "23456789";
    String all = upper + lower + digits + "!-_";
    StringBuilder sb = new StringBuilder("Qn");
    sb.append(upper.charAt(RANDOM.nextInt(upper.length())));
    sb.append(lower.charAt(RANDOM.nextInt(lower.length())));
    sb.append(digits.charAt(RANDOM.nextInt(digits.length())));
    sb.append('-');
    while (sb.length() < 16) {
      sb.append(all.charAt(RANDOM.nextInt(all.length())));
    }
    String password = sb.toString();
    Assert.assertFalse(password.equals(user.currentPassword) || password.equals(user.pendingPassword),
          "the new password must differ from the current one");
    return password;
  }

  private static TestUser registeredUser() {
    TestUser user = TestUser.load().orElse(null);
    Assert.assertNotNull(user, "no registered test user in " + TestUser.STATE);
    Assert.assertTrue(user.accountCreated, "the test user was not created");
    return user;
  }
}
