package ch.hirion.journey;

import ch.hirion.support.AuthenticatedTest;
import ch.hirion.support.TestUser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.opentest4j.AssertionFailedError;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Group DASH: /dashboard of the registered test user, openspec/changes/add-user-journey/specs/dashboard.
 *
 * <p>Starts signed in from .auth/user.json (written by reg9) and is skipped when group "registered" did not
 * pass. The order is set by priority: the empty lists (DASH-3) come before Save (DASH-4), and DASH-4, DASH-5
 * and DASH-6 each act on a different card. Never clicked: "Tailor my CV", "Write Cover Letter",
 * "Help me stand out", "Upgrade to Pro". "View job" is clicked only in dash11, "Save preferences" only in
 * dash12. Emails and passwords are never printed.
 */
public class DashboardTest extends AuthenticatedTest {

  private static final Pattern DASHBOARD_URL = Pattern.compile("/dashboard([?#].*)?$");
  private static final Pattern MATCH_SCORE = Pattern.compile("^\\d+ MATCH$");
  /** D8: a "?" next to a letter, a space or another "?" stands in for a lost character. */
  private static final Pattern REPLACEMENT_CHAR =
        Pattern.compile("[A-Za-z\\u00C0-\\u017F\\s?]\\?|\\?[A-Za-z\\u00C0-\\u017F\\s?]");
  private static final List<String> TABS =
        List.of("Job Feed", "Saved", "Applied", "Archive", "Analytics", "Preferences");
  private static final List<String> CARD_BUTTONS =
        List.of("View job", "Save", "Tailor my CV", "Write Cover Letter", "Help me stand out", "Hide");
  private static final List<String> PREFERENCE_FIELDS = List.of("Roles", "Work mode", "Locations", "Industries",
        "Company type", "Employment eligibility", "Seniority", "Languages");
  private static final String GATE_TEXT = "See your match analytics, Swiss salary benchmarks, and personalized"
        + " opportunities to land roles faster.";
  /** The first feed of a new user is built from the CV analysis and can take a while. */
  private static final double FEED_TIMEOUT = 30_000;
  private static final LocatorAssertions.IsVisibleOptions SLOW =
        new LocatorAssertions.IsVisibleOptions().setTimeout(15_000);
  /** Short timeout for the KNOWN BUG D8 assertion, like QUICK_ATTR. */
  private static final LocatorAssertions.HasCountOptions QUICK_COUNT =
        new LocatorAssertions.HasCountOptions().setTimeout(3000);

  /** Titles already saved / hidden by an earlier test: DASH-4, DASH-5 and DASH-6 each take a different card. */
  private final Set<String> usedTitles = new HashSet<>();

  // ---------------------------------------------------------------------------------------------
  // DASH-1

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

  @Test(priority = 2, dependsOnGroups = "registered", description = "DASH-2 First job card")
  public void dash2_firstJobCard() {
    openDashboard();
    Locator card = feedCards(1).first();
    assertThat(card.getByText(MATCH_SCORE)).isVisible();
    for (String name : CARD_BUTTONS) {
      assertThat(cardButton(card, name)).isVisible(); // presence only: the AI buttons are never clicked
    }
    assertThat(cardButton(card, "Hide")).hasAttribute("aria-haspopup", "menu");
  }

  // DASH-3 (before DASH-4: the lists of a fresh user are empty)

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

  // DASH-4

  @Test(priority = 4, dependsOnGroups = "registered", description = "DASH-4 Save the first job")
  public void dash4_saveMovesJob() {
    openDashboard();
    String title = pickCard();
    cardButton(cardTitled(title), "Save").click();
    // Checked before the tab click: while the card is still there its button is also named "Saved".
    assertThat(cardTitled(title)).hasCount(0);
    tab("Saved").click();
    assertThat(exactText(title).first()).isVisible();
    tab("Job Feed").click();
    assertThat(cardTitled(title)).hasCount(0);
  }

  // DASH-5

  @Test(priority = 5, dependsOnGroups = "registered", description = "DASH-5 Hide menu items")
  public void dash5_hideMenuItems() {
    openDashboard();
    String title = pickCard(); // only opens the menu, so the card stays in the feed and is not marked used
    usedTitles.remove(title);
    cardButton(cardTitled(title), "Hide").click();
    assertThat(page.getByRole(AriaRole.MENU)).isVisible();
    assertThat(role(AriaRole.MENUITEM, "Already applied")).isVisible();
    assertThat(role(AriaRole.MENUITEM, "Not relevant")).isVisible();
    page.keyboard().press("Escape"); // close without choosing
  }

  @Test(priority = 6, dependsOnGroups = "registered", description = "DASH-5 Mark a job as already applied")
  public void dash5_alreadyAppliedMovesJob() {
    openDashboard();
    String title = pickCard();
    cardButton(cardTitled(title), "Hide").click();
    role(AriaRole.MENUITEM, "Already applied").click();
    assertThat(cardTitled(title)).hasCount(0);
    tab("Applied").click();
    assertThat(exactText(title).first()).isVisible();
  }

  // DASH-6

  @Test(priority = 7, dependsOnGroups = "registered", description = "DASH-6 Mark a job as not relevant")
  public void dash6_notRelevantRemovesJob() {
    openDashboard();
    String title = pickCard();
    cardButton(cardTitled(title), "Hide").click();
    role(AriaRole.MENUITEM, "Not relevant").click();
    assertThat(cardTitled(title)).hasCount(0);
  }

  // DASH-7 (Free user: the Pro gate; "Upgrade to Pro" is checked, never clicked)

  @Test(priority = 8, dependsOnGroups = "registered", description = "DASH-7 Free user sees the Pro gate on Analytics")
  public void dash7_analyticsProGate() {
    openDashboard();
    tab("Analytics").click();
    assertThat(role(AriaRole.HEADING, "Unlock Hirion Pro")).isVisible();
    assertThat(exactText(GATE_TEXT)).isVisible();
    Locator upgrade = role(AriaRole.LINK, "Upgrade to Pro");
    assertThat(upgrade).isVisible();
    assertThat(upgrade).hasAttribute("href", "/settings?section=plan");
  }

  // DASH-9

  @Test(priority = 9, dependsOnGroups = "registered", description = "DASH-9 Preferences is rendered")
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

  @Test(priority = 10, dependsOnGroups = "registered", description = "DASH-12 Work mode survives a reload")
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

  // DASH-11 (the only test that clicks "View job")

  @Test(priority = 11, dependsOnGroups = "registered", description = "DASH-11 View job opens an external tab")
  public void dash11_viewJobOpensExternalTab() {
    openDashboard();
    Locator viewJob = cardButton(feedCards(1).first(), "View job");
    // "View job" is a button calling window.open(url, "_blank"), not a link.
    Page vacancy = context.waitForPage(viewJob::click);
    vacancy.waitForURL(url -> !url.startsWith("about:"), new Page.WaitForURLOptions()
          .setTimeout(30_000).setWaitUntil(WaitUntilState.COMMIT));
    String host = URI.create(vacancy.url()).getHost();
    vacancy.close();
    Assert.assertNotNull(host, "the new tab has no URL host");
    Assert.assertFalse(host.equals("hirion.ch") || host.endsWith(".hirion.ch"),
          "View job opened hirion.ch (" + host + ") instead of the external job ad");
    assertThat(page).hasURL(DASHBOARD_URL);
  }

  // DASH-10 (known defect D8)

  /*
   * Depends on the feed content: the "?" titles come from specific job ads. If today's feed has none, this
   * test fails ("expected exception was not thrown"), which does NOT mean D8 is fixed -- check the feed
   * (design.md "Risks"). The regex can also flag a real question mark in a title. Report both to a human.
   */
  @Test(priority = 12, dependsOnGroups = "registered", expectedExceptions = AssertionFailedError.class,
        expectedExceptionsMessageRegExp = "(?s).*count.*",
        description = "DASH-10 No replacement characters in job titles -- KNOWN BUG D8: titles show \"?\" "
              + "instead of characters")
  public void dash10_noReplacementCharsInTitles() {
    openDashboard();
    Locator cards = feedCards(1); // precondition through TestNG: at least one card in the feed
    Locator broken = cards.getByRole(AriaRole.HEADING)
          .filter(new Locator.FilterOptions().setHasText(REPLACEMENT_CHAR));
    assertThat(broken).hasCount(0, QUICK_COUNT);
  }

  // ---------------------------------------------------------------------------------------------
  // Helpers. None of them clicks an AI button, "Upgrade to Pro", "View job" or "Save preferences".

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
   * A job card is the article holding a "View job" button (no CSS classes). The card role is ASSUMED
   * (design.md "Locators": decided in RED from the live DOM); a wrong role is healed with heal-loop.sh.
   */
  private Locator cards() {
    return page.getByRole(AriaRole.ARTICLE)
          .filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("View job").setExact(true))));
  }

  private Locator cardTitled(String title) {
    return cards().filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.HEADING,
          new Page.GetByRoleOptions().setName(title).setExact(true))));
  }

  private static Locator cardButton(Locator card, String name) {
    return card.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(name).setExact(true));
  }

  private static String titleOf(Locator card) {
    return card.getByRole(AriaRole.HEADING).first().innerText().trim().replaceAll("\\s+", " ");
  }

  /** Waits for the feed; fewer than {@code min} cards is a precondition failure (TestNG), not a known bug. */
  private Locator feedCards(int min) {
    Locator cards = cards();
    try {
      cards.first().waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE).setTimeout(FEED_TIMEOUT));
    } catch (PlaywrightException e) {
      Assert.fail("no job card in the Job Feed after " + (int) (FEED_TIMEOUT / 1000) + " s");
    }
    Assert.assertTrue(cards.count() >= min, "the Job Feed has fewer than " + min + " job cards");
    return cards;
  }

  /**
   * The first feed card not used by an earlier test whose title is unique in the feed, so "the title is no
   * longer in the Job Feed" can only mean this job left it. Marks the title as used.
   */
  private String pickCard() {
    Locator cards = feedCards(1);
    int count = cards.count();
    for (int i = 0; i < count; i++) {
      String title = titleOf(cards.nth(i));
      if (!title.isEmpty() && !usedTitles.contains(title) && cardTitled(title).count() == 1) {
        usedTitles.add(title);
        return title;
      }
    }
    Assert.fail("no unused job card with a unique title in the Job Feed (" + count + " cards)");
    return null; // unreachable
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
