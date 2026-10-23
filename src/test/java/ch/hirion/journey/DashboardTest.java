package ch.hirion.journey;

import ch.hirion.support.AuthenticatedTest;
import ch.hirion.support.TestUser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Group DASH: /dashboard of the registered test user, openspec/changes/add-user-journey/specs/dashboard.
 *
 * <p>Starts signed in from .auth/user.json (written by reg9) and is skipped when group "registered" did not
 * pass. A Free user's Job Feed is a locked teaser (DASH-2, change update-plans-and-pricing): there are no job
 * cards, so Save / Hide / View job (former DASH-4, 5, 6, 10, 11) need the paid plan Basic and are not tested.
 * Never clicked: "Unlock with Basic", "See all plans", "Upgrade to Premium". "Save preferences" is clicked only in
 * dash12. Emails and passwords are never printed.
 */
@Epic("User journey")
@Feature("dashboard")
public class DashboardTest extends AuthenticatedTest {

  private static final Pattern DASHBOARD_URL = Pattern.compile("/dashboard([?#].*)?$");
  private static final List<String> TABS =
        List.of("Job Feed", "Saved", "Applied", "Archive", "Analytics", "Preferences");
  /** The teaser heading; group 1 is the number of locked jobs. */
  private static final Pattern TEASER_HEADING = Pattern.compile("^We found (\\d+) jobs? for you$");
  /** The teaser shows at most this many locked rows, whatever N the heading says. */
  private static final int TEASER_ROWS = 3;
  /** Each locked row ends with its match score, e.g. <span>88%</span>. */
  private static final Pattern MATCH_PERCENT = Pattern.compile("^\\d+%$");
  /**
   * Teaser links and their targets; neither is clicked: "Unlock with Basic" starts the Basic checkout at once
   * (buy=basic, change sync-specs-with-site-scan).
   */
  private static final Map<String, String> TEASER_LINKS = Map.of(
        "Unlock with Basic", "/settings?section=plan&buy=basic",
        "See all plans", "/settings?section=plan");
  /** Job actions of a paid plan: a Free user's Job Feed has none of them. */
  private static final List<String> JOB_ACTIONS = List.of("View job", "Save", "Hide");
  private static final List<String> PREFERENCE_FIELDS = List.of("Roles", "Work mode", "Locations", "Industries",
        "Company type", "Employment eligibility", "Seniority", "Languages");
  private static final String GATE_TEXT = "See your match analytics, Swiss salary benchmarks, and personalized"
        + " opportunities to land roles faster.";
  /** The first feed of a new user is built from the CV analysis and can take a while. */
  private static final LocatorAssertions.IsVisibleOptions FEED_LOADED =
        new LocatorAssertions.IsVisibleOptions().setTimeout(30_000);
  private static final LocatorAssertions.IsVisibleOptions SLOW =
        new LocatorAssertions.IsVisibleOptions().setTimeout(15_000);
  /** The Pro gate renders only after the analytics data has loaded (> 5 s in the first live run). */
  private static final LocatorAssertions.IsVisibleOptions ANALYTICS_LOADED =
        new LocatorAssertions.IsVisibleOptions().setTimeout(30_000);

  // ---------------------------------------------------------------------------------------------
  // DASH-1

  @Story("DASH-1 Dashboard heading and tab buttons")
  @Test(priority = 1, dependsOnGroups = "registered", description = "DASH-1 Dashboard is rendered")
  public void dash1_dashboardRendered() {
    TestUser user = registeredUser();
    openDashboard();
    assertThat(h1("Welcome back, " + user.firstName)).isVisible(SLOW);
    for (String name : TABS) {
      assertThat(tab(name)).isVisible();
    }
  }

  // DASH-2

  @Story("DASH-2 Free Job Feed is a locked teaser")
  @Test(priority = 2, dependsOnGroups = "registered", description = "DASH-2 Locked teaser for a Free user")
  public void dash2_freeJobFeedIsLockedTeaser() {
    openDashboard();
    Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(TEASER_HEADING));
    assertThat(heading).isVisible(FEED_LOADED);
    Matcher found = TEASER_HEADING.matcher(heading.innerText().trim());
    Assert.assertTrue(found.matches(), "teaser heading: " + heading.innerText());
    int jobs = Integer.parseInt(found.group(1));

    // The rows are aria-hidden, so they are found by text: one match score per row. The teaser previews at most
    // three jobs (2 jobs -> 2 rows, 10 jobs -> 3 rows; change fix-teaser-row-count).
    Locator scores = main().getByText(MATCH_PERCENT);
    assertThat(scores).hasCount(Math.min(jobs, TEASER_ROWS));
    // Read in the browser: the row sits inside an aria-hidden block and its title block is blurred.
    Object lockedRow = scores.first().evaluate("score => !!score.closest('[aria-hidden=\"true\"]')"
          + " && getComputedStyle(score.previousElementSibling).filter.includes('blur')");
    Assert.assertEquals(lockedRow, Boolean.TRUE, "the first teaser row is not hidden and blurred");

    assertThat(main().getByText(Pattern.compile("^Unlock them with Basic"))).isVisible();
    for (Map.Entry<String, String> link : TEASER_LINKS.entrySet()) {
      assertThat(main().getByRole(AriaRole.LINK, exactName(link.getKey())))
            .hasAttribute("href", link.getValue());
    }
    for (String name : JOB_ACTIONS) {
      assertThat(main().getByRole(AriaRole.BUTTON, exactName(name))).hasCount(0);
    }
  }

  // DASH-3

  @Story("DASH-3 Empty lists of a new user")
  @Test(priority = 3, dependsOnGroups = "registered", description = "DASH-3 Empty Saved, Applied and Archive")
  public void dash3_emptyListsOfNewUser() {
    openDashboard();
    tab("Saved").click();
    assertThat(exactText("No saved jobs yet")).isVisible();
    tab("Applied").click();
    assertThat(exactText("No applications tracked yet")).isVisible();
    tab("Archive").click();
    assertThat(exactText("No archived jobs yet")).isVisible();
  }

  // DASH-7 (Free user: the paid-plan gate; "Upgrade to Premium" is checked, never clicked)

  @Story("DASH-7 Analytics is Pro-only for a Free user")
  @Test(priority = 10, dependsOnGroups = "registered", description = "DASH-7 Free user sees the Pro gate on Analytics")
  public void dash7_analyticsProGate() {
    openDashboard();
    tab("Analytics").click();
    assertThat(role(AriaRole.HEADING, "Unlock Hirion Premium")).isVisible(ANALYTICS_LOADED);
    assertThat(exactText(GATE_TEXT)).isVisible();
    Locator upgrade = role(AriaRole.LINK, "Upgrade to Premium");
    assertThat(upgrade).isVisible();
    assertThat(upgrade).hasAttribute("href", "/settings?section=plan");
  }

  // DASH-9

  @Story("DASH-9 Preferences form")
  @Test(priority = 11, dependsOnGroups = "registered", description = "DASH-9 Preferences is rendered")
  public void dash9_preferencesRendered() {
    openDashboard();
    tab("Preferences").click();
    // Each field is a section with an h3 heading and an unnamed picker (D13), so the heading names it.
    for (String name : PREFERENCE_FIELDS) {
      assertThat(role(AriaRole.HEADING, name)).isVisible();
    }
    assertThat(role(AriaRole.BUTTON, "Save preferences")).isVisible(); // presence only, clicked in dash12
  }

  // DASH-12 (the only test that clicks "Save preferences")

  @Story("DASH-12 Preferences are saved")
  @Test(priority = 12, dependsOnGroups = "registered", description = "DASH-12 Work mode survives a reload")
  public void dash12_workModeSurvivesReload() {
    openDashboard();
    tab("Preferences").click();
    assertThat(role(AriaRole.BUTTON, "Save preferences")).isVisible();
    // Precondition: if "Remote" were already chosen, clicking the option could remove it again.
    Assert.assertEquals(page.getByRole(AriaRole.MAIN).getByText("Remote", exactOptions()).count(), 0,
          "\"Remote\" is already selected before the test");

    workModePicker().click();
    role(AriaRole.OPTION, "Remote").click();
    page.keyboard().press("Escape"); // close the picker, the chip stays
    assertThat(page.getByRole(AriaRole.MAIN).getByText("Remote", exactOptions())).isVisible();

    role(AriaRole.BUTTON, "Save preferences").click();
    assertThat(notifications().getByText("Preferences saved", exactOptions())).isVisible(SLOW);

    reload();
    tab("Preferences").click();
    assertThat(page.getByRole(AriaRole.MAIN).getByText("Remote", exactOptions())).isVisible(SLOW);
  }

  // ---------------------------------------------------------------------------------------------
  // Helpers. None of them clicks a teaser link, "Upgrade to Premium" or "Save preferences".

  private void openDashboard() {
    open("/dashboard");
    assertThat(page).hasURL(DASHBOARD_URL);
  }

  /**
   * Dashboard tabs are role=button, never role=tab (docs/intent.md). Without Pro the Analytics button also
   * holds the badge "Pro", so its accessible name is "Analytics Pro" (site code, reviewed 2026-09-30).
   */
  private Locator tab(String name) {
    if ("Analytics".equals(name)) {
      return page.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName(Pattern.compile("^Analytics( Pro)?$")));
    }
    return role(AriaRole.BUTTON, name);
  }

  /**
   * The Work mode picker: a combobox with the visible text "Add work mode" and, like D13 on signup step 3,
   * probably no accessible name, so it is matched by role + exact visible text.
   */
  private Locator workModePicker() {
    return page.getByRole(AriaRole.COMBOBOX)
          .filter(new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*Add work mode\\s*$")));
  }

  private Locator notifications() {
    return page.getByRole(AriaRole.REGION,
          new Page.GetByRoleOptions().setName(Pattern.compile("^Notifications")));
  }

  private Locator main() {
    return page.getByRole(AriaRole.MAIN);
  }

  private static Locator.GetByRoleOptions exactName(String name) {
    return new Locator.GetByRoleOptions().setName(name).setExact(true);
  }

  private Locator exactText(String text) {
    return page.getByText(text, new Page.GetByTextOptions().setExact(true));
  }

  private static Locator.GetByTextOptions exactOptions() {
    return new Locator.GetByTextOptions().setExact(true);
  }

  private static TestUser registeredUser() {
    TestUser user = TestUser.load().orElse(null);
    Assert.assertNotNull(user, "no registered test user in " + TestUser.STATE);
    Assert.assertTrue(user.accountCreated, "the test user was not created");
    return user;
  }
}
