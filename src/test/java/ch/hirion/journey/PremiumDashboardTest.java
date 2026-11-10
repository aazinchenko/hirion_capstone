package ch.hirion.journey;

import ch.hirion.support.PremiumAccountTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
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
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Group PREM: /dashboard and / of the owner-granted Premium account (session from PremiumSignInTest.prem15),
 * openspec/changes/use-existing-premium-account/specs/premium-trial.
 *
 * <p>The account is permanent, so every check is relative to its current state (counts before / after) and the job
 * saved by PREM-5 is unsaved again (prem5_unsave). Read-only checks run first (priorities 103-107), the AI dialogs next
 * (PremiumAiTest, 108-110), then save (111), save rate (112), unsave (113), and the irreversible PREM-6/7 (114-115)
 * only with -Dqa.feedActions=true. Each card is an article with the job title as heading. When the feed has no card
 * left for an action, the test is skipped, not failed. Never clicked: "View job" (opens the employer's site), the AI
 * buttons (only PremiumAiTest), "Save preferences", anything in Settings.
 */
@Epic("User journey")
@Feature("premium-trial")
public class PremiumDashboardTest extends PremiumAccountTest {

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
  /** PREM-5: a server function call that changes data; the id after /_serverFn/ changes with every deploy. */
  private static final Predicate<Response> SERVER_FN_POST =
        r -> "POST".equals(r.request().method()) && r.url().contains("/_serverFn/");
  /** PREM-6: open the Applied tab up to this many times, 15 s each (its list is cached 30 s, D21). */
  private static final int APPLIED_ATTEMPTS = 3;
  /** PREM-6/7 cannot be undone on the site: only on a manual run (human decision 2026-11-10). */
  private static final boolean FEED_ACTIONS = Boolean.getBoolean("qa.feedActions");
  /** Set by PREM-5 for PREM-9 and the unsave step: saved jobs before the save, and the title of the saved job. */
  private static int savedBefore = -1;
  private static String savedTitle;
  private static final LocatorAssertions.IsVisibleOptions FEED_LOADED =
        new LocatorAssertions.IsVisibleOptions().setTimeout(60_000);
  private static final LocatorAssertions.IsVisibleOptions SLOW =
        new LocatorAssertions.IsVisibleOptions().setTimeout(30_000);
  private static final LocatorAssertions.HasCountOptions SETTLED =
        new LocatorAssertions.HasCountOptions().setTimeout(15_000);

  // PREM-3

  @Story("PREM-3 The Premium Job Feed shows job cards instead of the teaser")
  @Test(priority = 103, dependsOnGroups = "premium", description = "PREM-3 Job cards after the activation")
  public void prem3_jobCardsInsteadOfTeaser() {
    openDashboard();
    assertThat(cards().first()).isVisible(FEED_LOADED); // no activation delay: the account is Premium already
    assertThat(main().getByRole(AriaRole.HEADING, new Locator.GetByRoleOptions()
          .setName(Pattern.compile("^We found")))).hasCount(0);
    assertThat(main().getByRole(AriaRole.LINK, exactName("Unlock with Basic"))).hasCount(0);
  }

  // PREM-4

  @Story("PREM-4 Every job card offers all Premium actions")
  @Test(priority = 104, dependsOnGroups = "premium", description = "PREM-4 Card actions")
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
  @Test(priority = 105, dependsOnGroups = "premium", description = "PREM-8 Analytics without the gate")
  public void prem8_analyticsWithoutGate() {
    openAnalytics();
    for (String text : ANALYTICS_TEXTS) {
      assertThat(main().getByText(text, new Locator.GetByTextOptions().setExact(true)).first()).isVisible();
    }
    assertThat(main().getByRole(AriaRole.HEADING, exactName("Unlock Hirion Premium"))).hasCount(0);
  }

  @Story("PREM-8 Analytics is unlocked for the Premium trial")
  @Test(priority = 106, dependsOnGroups = "premium",
        description = "PREM-8 Analytics tab has no Pro badge")
  public void prem8_analyticsTabHasNoProBadge() {
    openDashboard();
    // The badge is shown while the subscription is still loading, so wait until the name settles (D20 closed).
    assertThat(role(AriaRole.BUTTON, "Analytics")).isVisible(SLOW);
  }

  // PREM-10

  @Story("PREM-10 The home page hides the pricing for a Premium user")
  @Test(priority = 107, dependsOnGroups = "premium", description = "PREM-10 Home page of a Premium user")
  public void prem10_homeWithoutPricing() {
    open("/");
    Locator dashboardLink = page.getByRole(AriaRole.BANNER).getByRole(AriaRole.LINK, exactName("Dashboard"));
    assertThat(dashboardLink).hasAttribute("href", "/dashboard"); // also proves the page knows the session
    assertThat(page.locator("#pricing")).hasCount(0); // locator-exception: section anchor #pricing
  }

  // PREM-5 (the actions below change the feed and run last)

  @Story("PREM-5 Save moves a job to Saved")
  @Test(priority = 111, dependsOnGroups = "premium", description = "PREM-5 Save a job")
  public void prem5_saveMovesJobToSaved() {
    int n = openFeedWithCards();
    savedTitle = jobCards().first().getByRole(AriaRole.HEADING).first().innerText().trim();
    savedBefore = savedCount(); // opens the Saved tab
    int sameTitleBefore = savedWithTitle(savedTitle).count(); // 0 unless a job with the same title is already saved
    tab("Job Feed").click();
    assertThat(cards()).hasCount(n, SETTLED);
    Locator first = jobCards().first();
    assertThat(first.getByRole(AriaRole.HEADING).first()).hasText(savedTitle); // still the same first card
    // The site shows the saved job at once and stores it by a POST /_serverFn/<id> afterwards (site code). Wait for
    // that answer: in CI 2026-10-30 the next test reloaded too early and the server had 0 saved jobs (PREM-9).
    Response saved = page.waitForResponse(SERVER_FN_POST,
          () -> first.getByRole(AriaRole.BUTTON, exactName("Save")).click());
    Assert.assertTrue(saved.ok(), "saving the job answered " + saved.status() + " " + saved.statusText());
    assertThat(cards()).hasCount(n - 1, SETTLED);
    tab("Saved").click();
    assertThat(jobCards()).hasCount(savedBefore + 1, SETTLED);
    assertThat(savedWithTitle(savedTitle)).hasCount(sameTitleBefore + 1, SETTLED);
    // After a reload the list comes from the server, not from the page's cache.
    openDashboard();
    Assert.assertEquals(savedCount(), savedBefore + 1, "saved jobs after a reload");
  }

  // PREM-9

  @Story("PREM-9 Save rate counts saved jobs")
  @Test(priority = 112, dependsOnMethods = "prem5_saveMovesJobToSaved", description = "PREM-9 Save rate after one save")
  public void prem9_saveRateAfterOneSave() {
    openDashboard();
    int saved = savedCount();
    Assert.assertTrue(saved >= 1, "no saved job in the Saved tab after PREM-5");
    openAnalytics();
    // The figures render as 0 until the saved jobs and the feed have loaded (site code: rate = saved > 0 && total > 0
    // ? round(saved / (saved + feed) * 100) : 0). With at least one saved job the loaded rate is > 0, so wait for that
    // first; reading too early gave "0 jobs in feed, 0%" in a CI run on 2026-10-25.
    try {
      page.waitForFunction("(label) => { " + KPI_FN + " const t = kpi(label); return t !== null && /[1-9]/.test(t); }",
            "Save rate", new Page.WaitForFunctionOptions().setTimeout(30_000));
    } catch (RuntimeException notLoaded) {
      Assert.fail("Save rate stayed 0 for 30 s with " + saved + " saved jobs (Jobs in feed: "
            + kpiNumber("Jobs in feed") + ")");
    }
    int feed = kpiNumber("Jobs in feed");
    int expected = (int) Math.round(100.0 * saved / (saved + feed));
    Assert.assertEquals(kpiNumber("Save rate"), expected,
          "Save rate with " + saved + " saved jobs and " + feed + " jobs in feed (round(100 x S / (S + F)))");
  }

  // PREM-5, second scenario: the permanent account gets its state back

  @Story("PREM-5 Save moves a job to Saved")
  @Test(priority = 113, dependsOnMethods = "prem5_saveMovesJobToSaved", description = "PREM-5 Unsave a job")
  public void prem5_unsaveRestoresSaved() {
    openDashboard();
    int before = savedCount(); // leaves the Saved tab open
    Locator mine = savedWithTitle(savedTitle);
    int sameTitle = mine.count();
    Assert.assertTrue(sameTitle >= 1, "the job saved by PREM-5 is not in the Saved tab");
    Response unsaved = page.waitForResponse(SERVER_FN_POST,
          () -> mine.first().getByRole(AriaRole.BUTTON, exactName("Saved")).click());
    Assert.assertTrue(unsaved.ok(), "unsaving the job answered " + unsaved.status() + " " + unsaved.statusText());
    assertThat(jobCards()).hasCount(before - 1, SETTLED);
    assertThat(savedWithTitle(savedTitle)).hasCount(sameTitle - 1, SETTLED);
    openDashboard();
    Assert.assertEquals(savedCount(), savedBefore,
          "the Saved tab does not have its count from before PREM-5 again");
  }

  // PREM-6

  @Story("PREM-6 Already applied moves a job to Applied")
  @Test(priority = 114, dependsOnGroups = "premium", description = "PREM-6 Mark a job as applied")
  public void prem6_alreadyAppliedMovesJobToApplied() {
    requireFeedActions();
    int n = openFeedWithCards();
    // The permanent account already has applied jobs (CI 2026-11-10 20:47 failed on "exactly one line"), so count the
    // "Applied <date>" lines, and those of today, before and after. The day before is accepted for a run at midnight.
    LocalDate today = LocalDate.now(ZURICH);
    Locator appliedOn = main().getByText(APPLIED_ON);
    Locator appliedToday = main().getByText(Pattern.compile("^Applied (" + Pattern.quote(today.format(APPLIED_DAY))
          + "|" + Pattern.quote(today.minusDays(1).format(APPLIED_DAY)) + ")$"));
    openAppliedTab();
    int allBefore = appliedOn.count();
    int todayBefore = appliedToday.count();
    tab("Job Feed").click();
    assertThat(cards()).hasCount(n, SETTLED);
    hideFirstCard("Already applied");
    assertThat(cards()).hasCount(n - 1, SETTLED);
    // The card leaves the feed at once, the applied record is saved later, and the Applied tab caches its own list
    // for 30 s without a refresh after the action (site code, candidate D21). Reopening /dashboard drops that cache,
    // so every try starts with a reload (CI 2026-10-30 failed on the first click).
    boolean shown = false;
    for (int attempt = 1; attempt <= APPLIED_ATTEMPTS && !shown; attempt++) {
      openDashboard();
      openAppliedTab();
      try {
        assertThat(appliedOn).hasCount(allBefore + 1, SETTLED);
        assertThat(appliedToday).hasCount(todayBefore + 1, SETTLED);
        shown = true;
      } catch (AssertionFailedError notYet) {
        // the tab read the list before the server stored the applied job: reload and look again
      }
    }
    Assert.assertTrue(shown, "the Applied tab did not get one more \"Applied " + today.format(APPLIED_DAY)
          + "\" line (before: " + allBefore + " lines, " + todayBefore + " of today; now " + appliedOn.count()
          + " / " + appliedToday.count() + ") after " + APPLIED_ATTEMPTS + " tries");
  }

  /** Opens the Applied tab and waits until its own list has loaded (skeleton cards until then, DASH-3). */
  private void openAppliedTab() {
    tab("Applied").click();
    Locator empty = main().getByText("No applications tracked yet", new Locator.GetByTextOptions().setExact(true));
    assertThat(main().getByText(APPLIED_ON).first().or(empty)).isVisible(SLOW);
  }

  // PREM-7

  @Story("PREM-7 Not relevant removes a job from the feed")
  @Test(priority = 115, dependsOnGroups = "premium", description = "PREM-7 Dismiss a job")
  public void prem7_notRelevantRemovesJob() {
    requireFeedActions();
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

  /** PREM-6/7 change the permanent account for good: only on a manual run with -Dqa.feedActions=true. */
  private static void requireFeedActions() {
    if (!FEED_ACTIONS) {
      throw new SkipException("\"Already applied\" / \"Not relevant\" run only with -Dqa.feedActions=true"
            + " (cannot be undone on the permanent Premium account)");
    }
  }

  /**
   * From the Job Feed of a freshly opened dashboard: opens the Saved tab and returns the number of saved jobs. The
   * Saved list and the feed load together, and the tab shows "No saved jobs yet" while nothing has loaded, so the feed
   * is given time to show its cards first (an empty feed is allowed).
   */
  private int savedCount() {
    try {
      assertThat(cards().first()).isVisible(FEED_LOADED);
    } catch (AssertionFailedError emptyFeed) {
      // the account may have no unsaved job left in its feed; the Saved list has loaded by now anyway
    }
    tab("Saved").click();
    Locator empty = main().getByText("No saved jobs yet", new Locator.GetByTextOptions().setExact(true));
    assertThat(jobCards().first().or(empty)).isVisible(SLOW);
    return jobCards().count();
  }

  /** Job cards in the open tab whose heading is exactly {@code title}. */
  private Locator savedWithTitle(String title) {
    return jobCards().filter(new Locator.FilterOptions().setHas(
          page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(title).setExact(true))));
  }

  /** Each job card is an article with the job title as heading and a "View job" button (site code 2026-11-10). */
  private Locator jobCards() {
    return main().getByRole(AriaRole.ARTICLE).filter(new Locator.FilterOptions()
          .setHas(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("View job").setExact(true))));
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
