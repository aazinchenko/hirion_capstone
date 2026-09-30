package ch.hirion.journey;

import ch.hirion.support.BaseTest;
import ch.hirion.support.TestUser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.opentest4j.AssertionFailedError;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Group REG + plan AUTH-03: signup wizard steps 1-4 and signing in with the registered account,
 * openspec/changes/add-user-journey/specs/signup (REG-1 / REG-2 live in the public suite).
 *
 * <p>Only reg9_freeSignupLandsOnDashboard creates an account. Every other wizard test is a guest that stops
 * before the last step's submit button. reg4 and auth6 run after reg9 and use the registered test user.
 * Emails and passwords are never printed.
 */
public class RegistrationTest extends BaseTest {

  private static final Pattern DASHBOARD_URL = Pattern.compile("/dashboard([?#].*)?$");
  private static final Pattern LOGIN_URL = Pattern.compile("/login([?#].*)?$");
  private static final Pattern SIGNUP_URL = Pattern.compile("/signup([?#].*)?$");
  /** The selected plan card has the class token "border-primary"; others have "hover:border-primary/40". */
  private static final Pattern SELECTED_CARD = Pattern.compile("(^|\\s)border-primary(\\s|$)");
  /** Step changes wait for the email check (step 1) and a React re-render. */
  private static final LocatorAssertions.IsVisibleOptions STEP =
        new LocatorAssertions.IsVisibleOptions().setTimeout(15_000);
  private static final String CV = "qa-cv.pdf";
  private static final String ROLE = "QA / Test Engineer";
  private static final String GUEST_PASSWORD = "Guest-Pass-2026!"; // made up, never creates an account

  // ---------------------------------------------------------------------------------------------
  // REG-3

  @Test(priority = 1, description = "REG-3 Short password is blocked")
  public void reg3_shortPasswordBlocked() {
    open("/signup");
    fillStepOne("Qa", "Journey", guestEmail(), "short7!"); // 7 characters
    continueButton().click();
    assertThat(exactText("Enter an email and a password of at least 8 characters.")).isVisible();
    assertThat(stepLabel(1)).isVisible();
  }

  // REG-4 (after reg9: needs the registered test user)

  @Test(priority = 11, dependsOnMethods = "reg9_freeSignupLandsOnDashboard",
        description = "REG-4 Taken email is blocked")
  public void reg4_takenEmailBlocked() {
    TestUser user = registeredUser();
    open("/signup");
    fillStepOne("Qa", "Journey", user.email, GUEST_PASSWORD);
    continueButton().click();
    assertThat(exactText("An account with this email already exists. Sign in instead.")).isVisible(STEP);
    assertThat(stepLabel(1)).isVisible();
  }

  // REG-5

  @Test(priority = 2, description = "REG-5 Step 2 is rendered")
  public void reg5_stepTwoRendered() {
    walkToStep(2, "/signup");
    assertThat(role(AriaRole.HEADING, "Upload your CV")).isVisible();
    assertThat(exactText("PDF, DOCX, TXT up to 10MB")).isVisible();
  }

  @Test(priority = 3, description = "REG-5 Step 2 without a file is blocked")
  public void reg5_stepTwoWithoutFileBlocked() {
    walkToStep(2, "/signup");
    continueButton().click();
    assertThat(exactText("Please upload your CV to continue. It is what powers your job matches.")).isVisible();
    assertThat(stepLabel(2)).isVisible();
  }

  // REG-6

  @Test(priority = 4, description = "REG-6 Step 3 is rendered")
  public void reg6_stepThreeRendered() {
    walkToStep(3, "/signup");
    assertThat(role(AriaRole.HEADING, "What roles are you looking for?")).isVisible();
  }

  @Test(priority = 5, description = "REG-6 Step 3 without a role is blocked")
  public void reg6_stepThreeWithoutRoleBlocked() {
    walkToStep(3, "/signup");
    continueButton().click();
    assertThat(exactText("Please add at least one target role to continue.")).isVisible();
    assertThat(stepLabel(3)).isVisible();
  }

  // REG-7

  @Test(priority = 6, description = "REG-7 Step 4 is rendered with Premium Trial selected")
  public void reg7_stepFourPremiumTrialSelected() {
    walkToStep(4, "/signup"); // no plan parameter: the site preselects Premium Trial
    assertThat(role(AriaRole.HEADING, "Choose your plan")).isVisible();
    assertThat(exactText("7 days free")).isVisible();
    assertThat(planCard("Free")).isVisible();
    assertThat(role(AriaRole.BUTTON, "Back")).isVisible();
    assertThat(role(AriaRole.BUTTON, "See my matches")).isVisible(); // visible only, never clicked here
    assertThat(planCard("Premium Trial")).hasAttribute("class", SELECTED_CARD);
    assertThat(planCard("Free")).not().hasAttribute("class", SELECTED_CARD);
  }

  @Test(priority = 7, description = "REG-7 Free can be selected")
  public void reg7_freeCanBeSelected() {
    walkToStep(4, "/signup");
    planCard("Free").click();
    assertThat(planCard("Free")).hasAttribute("class", SELECTED_CARD);
    assertThat(planCard("Premium Trial")).not().hasAttribute("class", SELECTED_CARD);
  }

  @Test(priority = 7, description = "REG-7 plan=free preselects Free")
  public void reg7_planFreePreselectsFree() {
    walkToStep(4, "/signup?plan=free");
    assertThat(planCard("Free")).hasAttribute("class", SELECTED_CARD);
    assertThat(planCard("Premium Trial")).not().hasAttribute("class", SELECTED_CARD);
  }

  @Test(priority = 7, description = "REG-7 plan=trial preselects Premium Trial")
  public void reg7_planTrialPreselectsTrial() {
    walkToStep(4, "/signup?plan=trial");
    assertThat(planCard("Premium Trial")).hasAttribute("class", SELECTED_CARD);
    assertThat(planCard("Free")).not().hasAttribute("class", SELECTED_CARD);
  }

  @Test(priority = 7, description = "REG-7 Back keeps the wizard data")
  public void reg7_backKeepsWizardData() {
    walkToStep(4, "/signup");
    planCard("Free").click();
    assertThat(planCard("Free")).hasAttribute("class", SELECTED_CARD);
    role(AriaRole.BUTTON, "Back").click();
    assertThat(stepLabel(3)).isVisible(STEP);
    // The chip of the chosen role sits inside <main>; the option list is portaled outside it.
    assertThat(page.getByRole(AriaRole.MAIN)
          .getByText(ROLE, new Locator.GetByTextOptions().setExact(true))).isVisible();
    continueButton().click();
    assertThat(stepLabel(4)).isVisible(STEP);
    assertThat(planCard("Free")).hasAttribute("class", SELECTED_CARD);
    assertThat(planCard("Premium Trial")).not().hasAttribute("class", SELECTED_CARD);
  }

  // REG-8

  @Test(priority = 8, description = "REG-8 Steps 1-3 send no account data")
  public void reg8_stepsOneToThreeSendNothing() {
    List<String> sent = Collections.synchronizedList(new ArrayList<>());
    boolean[] armed = {false};
    page.onRequest(request -> {
      if (armed[0] && isAccountRequest(request)) {
        sent.add(request.method() + " " + request.url()); // URL only, never the body
      }
    });
    open("/signup");
    fillStepOne("Qa", "Journey", guestEmail(), GUEST_PASSWORD);
    continueButton().click(); // the email-availability check is allowed here
    assertThat(stepLabel(2)).isVisible(STEP);
    armed[0] = true; // from step 2 on nothing may reach the backend before the last step's submit
    uploadCv();
    continueButton().click();
    assertThat(stepLabel(3)).isVisible(STEP);
    addRole();
    continueButton().click();
    assertThat(stepLabel(4)).isVisible(STEP);
    Assert.assertEquals(sent, List.of(), "account requests sent before the plan step was submitted");
  }

  // REG-9 -- the ONLY test that creates an account

  @Test(priority = 10, groups = "registered", description = "REG-9 Free signup lands on the dashboard")
  public void reg9_freeSignupLandsOnDashboard() {
    TestUser user = TestUser.generate(); // SkipException without -Dqa.mailbox: nothing is created
    walkToStep(4, "/signup?plan=free", user.firstName, "Journey", user.email, user.currentPassword);

    Locator free = planCard("Free");
    Locator trial = planCard("Premium Trial");
    free.click();
    assertThat(free).hasAttribute("class", SELECTED_CARD);
    // GUARD: with Premium Trial selected the site starts the Stripe trial at once. Stop before the submit.
    Assert.assertTrue(isSelected(free), "Free card is not selected -- account NOT created");
    Assert.assertFalse(isSelected(trial), "Premium Trial card is selected -- account NOT created");

    user.accountCreated = false;
    user.submitted = true; // an account may exist from the click on; cleanup must then never drop it silently
    user.save(); // before the click, so JourneyCleanup finds the user even if the run dies right after it
    role(AriaRole.BUTTON, "See my matches").click();

    assertThat(page).hasURL(DASHBOARD_URL, new PageAssertions.HasURLOptions().setTimeout(60_000));
    user.accountCreated = true;
    user.save();
    context.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get(".auth/user.json")));
    assertThat(h1("Welcome back, " + user.firstName)).isVisible(STEP);
  }

  // REG-10 (known defect D6)

  @Test(priority = 9, expectedExceptions = AssertionFailedError.class,
        expectedExceptionsMessageRegExp = "(?s).*tabindex.*",
        description = "REG-10 Show password is in the tab order -- KNOWN BUG D6: the button has tabindex=-1")
  public void reg10_showPasswordInTabOrder() {
    open("/signup");
    Locator show = role(AriaRole.BUTTON, "Show password");
    show.waitFor();
    // Precondition through TestNG: a missing button is a real failure, not the known bug.
    Assert.assertEquals(show.count(), 1, "exactly one Show password button on step 1");
    assertThat(show).not().hasAttribute("tabindex", "-1", QUICK_ATTR);
  }

  // AUTH-6 (plan AUTH-03, after reg9, exactly one wrong attempt)

  @Test(priority = 12, dependsOnMethods = "reg9_freeSignupLandsOnDashboard",
        description = "AUTH-6 Registered test user with a wrong password")
  public void auth6_wrongPasswordStaysOnLogin() {
    TestUser user = registeredUser();
    String wrong = "Wrong-Pass-2026!";
    Assert.assertFalse(wrong.equals(user.currentPassword) || wrong.equals(user.pendingPassword),
          "the wrong password must differ from the real ones");
    open("/login");
    field("Email").fill(user.email);
    field("Password").fill(wrong); // exact: the "Show password" button also matches "Password"
    role(AriaRole.BUTTON, "Sign in").click();
    // The error is a toast in the "Notifications" region; its text is proposal Open question 4.
    Locator toasts = page.getByRole(AriaRole.REGION,
          new Page.GetByRoleOptions().setName(Pattern.compile("^Notifications")));
    assertThat(toasts.getByRole(AriaRole.LISTITEM)).isVisible(STEP);
    assertThat(page).hasURL(LOGIN_URL);
    assertThat(h1("Welcome back")).isVisible();
  }

  // ---------------------------------------------------------------------------------------------
  // Wizard helpers. None of them clicks the submit button of step 4.

  /** Opens {@code path} as a guest and walks to {@code step} (2-4) with made-up step 1 values. */
  private void walkToStep(int step, String path) {
    walkToStep(step, path, "Qa", "Journey", guestEmail(), GUEST_PASSWORD);
  }

  private void walkToStep(int step, String path, String first, String last, String email, String password) {
    open(path);
    assertThat(page).hasURL(SIGNUP_URL);
    fillStepOne(first, last, email, password);
    continueButton().click();
    assertThat(stepLabel(2)).isVisible(STEP);
    if (step == 2) return;
    uploadCv();
    continueButton().click();
    assertThat(stepLabel(3)).isVisible(STEP);
    if (step == 3) return;
    addRole();
    continueButton().click();
    assertThat(stepLabel(4)).isVisible(STEP);
  }

  private void fillStepOne(String first, String last, String email, String password) {
    field("First name").fill(first);
    field("Last name").fill(last);
    field("Email").fill(email);
    field("Password").fill(password); // exact: not the "Show password" button
  }

  /** Step 2: the file input sits inside the drop-zone label, so the label text finds it. */
  private void uploadCv() {
    Locator dropZone = page.getByLabel("Drop your CV here, or click to upload");
    FileChooser chooser = page.waitForFileChooser(dropZone::click);
    chooser.setFiles(FIXTURES.resolve(CV));
    assertThat(exactText(CV)).isVisible(); // the drop zone shows the chosen file name
  }

  /**
   * Step 3: the roles picker is a combobox with the visible text "Add" and a list of options; one role is enough.
   * The combobox has no accessible name (candidate defect D13), so it is matched by role + exact text.
   */
  private void addRole() {
    page.getByRole(AriaRole.COMBOBOX)
          .filter(new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*Add\\s*$"))).click();
    role(AriaRole.OPTION, ROLE).click();
    // Escape does not close the option list (it stays open, portaled outside <main>; heal-raw.log
    // 2026-09-29); the chosen role shows as a chip inside <main>.
    page.keyboard().press("Escape");
    assertThat(page.getByRole(AriaRole.MAIN)
          .getByText(ROLE, new Locator.GetByTextOptions().setExact(true))).isVisible();
  }

  /** "Continue" exactly: not "Continue with Google" / "Continue with Apple". */
  private Locator continueButton() {
    return role(AriaRole.BUTTON, "Continue");
  }

  private Locator stepLabel(int n) {
    return exactText("Step " + n + " of 4");
  }

  private Locator exactText(String text) {
    return page.getByText(text, new Page.GetByTextOptions().setExact(true));
  }

  /**
   * Plan cards are plain buttons without aria-pressed / aria-checked (candidate defect D12). The Free card
   * is the button holding the exact text "Free"; a substring match would also hit "7 days free".
   */
  private Locator planCard(String label) {
    return page.getByRole(AriaRole.BUTTON).filter(new Locator.FilterOptions().setHas(exactText(label)));
  }

  private static boolean isSelected(Locator card) {
    String cls = card.getAttribute("class");
    return cls != null && Arrays.asList(cls.trim().split("\\s+")).contains("border-primary");
  }

  /** Non-GET to hirion.ch or the auth backend, or any server-function call. Stripe and others are ignored. */
  private static boolean isAccountRequest(Request request) {
    String url = request.url();
    String host = URI.create(url).getHost();
    if (host == null) return false;
    boolean ours = host.equals("hirion.ch") || host.endsWith(".hirion.ch") || host.endsWith(".supabase.co");
    return url.contains("/_serverFn/") || (ours && !"GET".equals(request.method()));
  }

  private static String guestEmail() {
    return "journey-guest+hirion-qa-" + System.currentTimeMillis() + "@example.com";
  }

  private static TestUser registeredUser() {
    TestUser user = TestUser.load().orElse(null);
    Assert.assertNotNull(user, "no registered test user in " + TestUser.STATE);
    Assert.assertTrue(user.accountCreated, "the test user was not created");
    return user;
  }
}
