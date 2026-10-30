package ch.hirion.journey;

import ch.hirion.support.AuthenticatedTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.opentest4j.AssertionFailedError;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Group PREM: /dashboard and / of the journey user after the Premium trial started (PremiumUpgradeTest.prem1),
 * openspec/changes/add-premium-trial-journey/specs/premium-trial.
 *
 * <p>Read-only checks run first (priorities 103-107), the AI dialogs next (PremiumAiTest, 108-110), the actions that
 * change the feed last (111-114). The cards have no test id, so actions are checked by counting one "Tailor my CV"
 * button per card. When the feed has no card left for an action, the test is skipped, not failed (design.md).
 * Never clicked: "View job" (opens the employer's site), the AI buttons (only PremiumAiTest), "Save preferences".
 */
@Epic("User journey")
@Feature("premium-trial")
public class PremiumDashboardTest extends AuthenticatedTest {

  private static final Pattern DASHBOARD_URL = Pattern.compile("/dashboard([?#].*)?$");
  /** Card actions besides "Tailor my CV", which is the card counter itself. */
  private static final List<String> CARD_ACTIONS =
        List.of("View job", "Save", "Write Cover Letter", "Help me stand out", "Hide");
  private static final List<String> ANALYTICS_TEXTS = List.of("Your performance", "Swiss market intelligence",
        "Positioning & opportunity", "Total active jobs", "Avg match score", "Save rate", "Jobs in feed");
  private static final Pattern ANALYTICS_TAB = Pattern.compile("^Analytics( Pro)?$", Pattern.CASE_INSENSITIVE);
  private static final Pattern APPLIED_ON = Pattern.compile("^Applied ([A-Z][a-z]{2} \\d{1,2}, \\d{4})$");
  private static final DateTimeFormatter APPLIED_DAY = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
  private static final ZoneId ZURICH = ZoneId.of("Europe/Zurich");
  /** PREM-6: open the Applied tab up to this many times, 15 s each (its list is cached 30 s, D21). */
  private static final int APPLIED_ATTEMPTS = 3;
  /** PREM-3: reopen /dashboard up to this many times, 30 s each (reported delay up to 5 min, bug #49). */
  private static final int CARD_ATTEMPTS = 12;
  private static final LocatorAssertions.IsVisibleOptions CARD_WAIT =
        new LocatorAssertions.IsVisibleOptions().setTimeout(30_000);
  private static final LocatorAssertions.IsVisibleOptions FEED_LOADED =
        new LocatorAssertions.IsVisibleOptions().setTimeout(60_000);
  private static final LocatorAssertions.IsVisibleOptions SLOW =
        new LocatorAssertions.IsVisibleOptions().setTimeout(30_000);
  private static final LocatorAssertions.HasCountOptions SETTLED =
        new LocatorAssertions.HasCountOptions().setTimeout(15_000);

  // PREM-3

  @Story("PREM-3 The Premium Job Feed shows job cards instead of the teaser")
  @Test(priority = 103, dependsOnGroups = {"registered", "premium"}, description = "PREM-3 Job cards after the activation")
  public void prem3_jobCardsInsteadOfTeaser() {
    boolean shown = false;
    for (int attempt = 1; attempt <= CARD_ATTEMPTS && !shown; attempt++) {
      openDashboard();
      try {
        assertThat(cards().first()).isVisible(CARD_WAIT);
        shown = true;
      } catch (AssertionFailedError notYet) {
        // the feed of a fresh trial can lag behind the activation: reopen the page and wait again
      }
    }
    Assert.assertTrue(shown, "no job card (button \"Tailor my CV\") within " + CARD_ATTEMPTS * 30 + " s");
    assertThat(main().getByRole(AriaRole.HEADING, new Locator.GetByRoleOptions()
          .setName(Pattern.compile("^We found")))).hasCount(0);
    assertThat(main().getByRole(AriaRole.LINK, exactName("Unlock with Basic"))).hasCount(0);
  }

  // PREM-4

  @Story("PREM-4 Every job card offers all Premium actions")
  @Test(priority = 104, dependsOnGroups = {"registered", "premium"}, description = "PREM-4 Card actions")
  public void prem4_cardActions() {
    int n = openFeedWithCards();
    for (String action : CARD_ACTIONS) {
      assertThat(main().getByRole(AriaRole.BUTTON, exactName(action))).hasCount(n);
    }
    // the score badge is "95" + "match" in two spans, capitals only by CSS (docs/intent.md)
    assertThat(main().getByText("match", new Locator.GetByTextOptions().setExact(true))).hasCount(n);
  }

  // PREM-8

  @Story("PREM-8 Analytics is unlocked for the Premium trial")
  @Test(priority = 105, dependsOnGroups = {"registered", "premium"}, description = "PREM-8 Analytics without the gate")
  public void prem8_analyticsWithoutGate() {
    openAnalytics();
    for (String text : ANALYTICS_TEXTS) {
      assertThat(main().getByText(text, new Locator.GetByTextOptions().setExact(true)).first()).isVisible();
    }
    assertThat(main().getByRole(AriaRole.HEADING, exactName("Unlock Hirion Premium"))).hasCount(0);
  }

  @Story("PREM-8 Analytics is unlocked for the Premium trial")
  @Test(priority = 106, dependsOnGroups = {"registered", "premium"},
        description = "PREM-8 Analytics tab has no Pro badge")
  public void prem8_analyticsTabHasNoProBadge() {
    openDashboard();
    // The badge is shown while the subscription is still loading, so wait until the name settles (D20 closed).
    assertThat(role(AriaRole.BUTTON, "Analytics")).isVisible(SLOW);
  }

  // PREM-10

  @Story("PREM-10 The home page hides the pricing for a Premium user")
  @Test(priority = 107, dependsOnGroups = {"registered", "premium"}, description = "PREM-10 Home page of a Premium user")
  public void prem10_homeWithoutPricing() {
    open("/");
    Locator dashboardLink = page.getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, exactName("Dashboard"));
    assertThat(dashboardLink).hasAttribute("href", "/dashboard"); // also proves the page knows the session
    assertThat(page.locator("#pricing")).hasCount(0); // locator-exception: section anchor #pricing
  }

  // PREM-5 (the actions below change the feed and run last)

  @Story("PREM-5 Save moves a job to Saved")
  @Test(priority = 111, dependsOnGroups = {"registered", "premium"}, description = "PREM-5 Save a job")
  public void prem5_saveMovesJobToSaved() {
    int n = openFeedWithCards();
    main().getByRole(AriaRole.BUTTON, exactName("Save")).first().click();
    assertThat(cards()).hasCount(n - 1, SETTLED);
    tab("Saved").click();
    assertThat(main().getByRole(AriaRole.BUTTON, exactName("View job"))).hasCount(1, SETTLED);
    assertThat(main().getByText("No saved jobs yet")).hasCount(0);
  }

  // PREM-9

  @Story("PREM-9 Save rate counts saved jobs")
  @Test(priority = 112, dependsOnMethods = "prem5_saveMovesJobToSaved", description = "PREM-9 Save rate after one save")
  public void prem9_saveRateAfterOneSave() {
    openAnalytics();
    // The figures render as 0 until the saved jobs and the feed have loaded (site code: rate = saved > 0 && total > 0
    // ? round(saved / (saved + feed) * 100) : 0). With one saved job the loaded rate is > 0, so wait for that first;
    // reading too early gave "0 jobs in feed, 0%" in a CI run on 2026-10-25.
    try {
      page.waitForFunction("(label) => { " + KPI_FN + " const t = kpi(label); return t !== null && /[1-9]/.test(t); }",
            "Save rate", new Page.WaitForFunctionOptions().setTimeout(30_000));
    } catch (RuntimeException notLoaded) {
      Assert.fail("Save rate stayed 0 for 30 s after one saved job (Jobs in feed: " + kpiNumber("Jobs in feed") + ")");
    }
    int feed = kpiNumber("Jobs in feed");
    int expected = (int) Math.round(100.0 / (1 + feed));
    Assert.assertEquals(kpiNumber("Save rate"), expected,
          "Save rate with 1 saved job and " + feed + " jobs in feed (round(100 / (1 + F)))");
  }

  // PREM-6

  @Story("PREM-6 Already applied moves a job to Applied")
  @Test(priority = 113, dependsOnGroups = {"registered", "premium"}, description = "PREM-6 Mark a job as applied")
  public void prem6_alreadyAppliedMovesJobToApplied() {
    int n = openFeedWithCards();
    hideFirstCard("Already applied");
    assertThat(cards()).hasCount(n - 1, SETTLED);
    // The card leaves the feed at once, the applied record is saved later, and the Applied tab caches its own list
    // for 30 s without a refresh after the action (site code, candidate D21): an early click shows the empty tab.
    // Reopening /dashboard drops that cache, so retry with a reload (CI 2026-10-30 failed on the first click).
    Locator appliedOn = main().getByText(APPLIED_ON);
    boolean shown = false;
    for (int attempt = 1; attempt <= APPLIED_ATTEMPTS && !shown; attempt++) {
      if (attempt > 1) openDashboard();
      tab("Applied").click();
      try {
        assertThat(appliedOn).hasCount(1, SETTLED);
        shown = true;
      } catch (AssertionFailedError notYet) {
        // the tab read the list before the server stored the applied job: reload and look again
      }
    }
    Assert.assertTrue(shown, "no \"Applied <date>\" line in the Applied tab after " + APPLIED_ATTEMPTS + " tries");
    Matcher m = APPLIED_ON.matcher(appliedOn.innerText().trim());
    Assert.assertTrue(m.matches(), "unexpected text: " + appliedOn.innerText());
    LocalDate today = LocalDate.now(ZURICH); // the day before is accepted when the run crossed midnight
    Assert.assertTrue(List.of(today.format(APPLIED_DAY), today.minusDays(1).format(APPLIED_DAY)).contains(m.group(1)),
          "applied date " + m.group(1) + " is not today (" + today.format(APPLIED_DAY) + ")");
  }

  // PREM-7

  @Story("PREM-7 Not relevant removes a job from the feed")
  @Test(priority = 114, dependsOnGroups = {"registered", "premium"}, description = "PREM-7 Dismiss a job")
  public void prem7_notRelevantRemovesJob() {
    int n = openFeedWithCards();
    hideFirstCard("Not relevant");
    assertThat(cards()).hasCount(n - 1, SETTLED);
  }

  // ---------------------------------------------------------------------------------------------
  // Helpers. None of them clicks "View job" or an AI button.

  private void openDashboard() {
    open("/dashboard");
    assertThat(page).hasURL(DASHBOARD_URL);
  }

  /** Opens the Job Feed and returns the number of cards; skips the test when no card is left (design.md). */
  private int openFeedWithCards() {
    openDashboard();
    try {
      assertThat(cards().first()).isVisible(FEED_LOADED);
    } catch (AssertionFailedError e) {
      throw new SkipException("no job card left in the Premium feed for this action");
    }
    return cards().count();
  }

  private void openAnalytics() {
    openDashboard();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ANALYTICS_TAB)).first().click();
    assertThat(main().getByText("Your performance", new Locator.GetByTextOptions().setExact(true))).isVisible(SLOW);
  }

  private void hideFirstCard(String choice) {
    main().getByRole(AriaRole.BUTTON, exactName("Hide")).first().click();
    page.getByRole(AriaRole.MENUITEM, new Page.GetByRoleOptions().setName(choice).setExact(true)).click();
  }

  /**
   * Read in the browser: the number of the analytics figure labelled exactly {@code label}, e.g. "Jobs in feed 3"
   * -> 3, "Save rate 33%" -> 33. The figure has no own role or label, so the label's nearest block with a digit is
   * read.
   */
  private int kpiNumber(String label) {
    Object text = page.evaluate("(label) => { " + KPI_FN + " return kpi(label); }", label);
    Assert.assertNotNull(text, "no figure next to \"" + label + "\"");
    Matcher m = Pattern.compile("([\\d,]+)").matcher(text.toString());
    Assert.assertTrue(m.find(), "no number next to \"" + label + "\": " + text);
    return Integer.parseInt(m.group(1).replace(",", ""));
  }

  /**
   * Browser-side helper for kpiNumber and PREM-9: text of the nearest block around the leaf labelled exactly
   * {@code label} that holds a digit, e.g. "33%" for "Save rate"; null when the label is not on the page.
   */
  private static final String KPI_FN = "function kpi(label) {"
        + " const main = document.querySelector('main'); if (!main) return null;"
        + " const el = [...main.querySelectorAll('*')].find(e => e.children.length === 0 && e.textContent.trim() === label);"
        + " for (let b = el && el.parentElement; b && b !== main; b = b.parentElement) {"
        + "   const rest = b.innerText.replace(label, ''); if (/\\d/.test(rest)) return rest; }"
        + " return null; }";

  /** One "Tailor my CV" button per job card. */
  private Locator cards() {
    return main().getByRole(AriaRole.BUTTON, exactName("Tailor my CV"));
  }

  private Locator tab(String name) {
    return role(AriaRole.BUTTON, name);
  }

  private Locator main() {
    return page.getByRole(AriaRole.MAIN);
  }

  private static Locator.GetByRoleOptions exactName(String name) {
    return new Locator.GetByRoleOptions().setName(name).setExact(true);
  }
}
