package ch.hirion.journey;

import ch.hirion.support.AuthenticatedTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Group PREM: the journey user starts the 7-day Premium trial in Settings > Plan & Billing,
 * openspec/changes/add-premium-trial-journey/specs/premium-trial (variant A, human decision 2026-10-24).
 *
 * <p>Runs after every Free test (priorities 101+) with the session saved by reg9. prem1 is the gate: all other
 * Premium tests depend on group "premium". Never clicked: "Manage subscription", "Subscribe to Basic", "Have a promo
 * code?". If a Stripe checkout appears instead of the trial, prem1 presses Escape and fails; nothing is typed.
 */
@Epic("User journey")
@Feature("premium-trial")
public class PremiumUpgradeTest extends AuthenticatedTest {

  static final String TRIAL_BUTTON = "Start 7-day free trial";
  private static final String TRIAL_STARTED = "Your 7-day Premium trial is active";
  /** One paragraph in the live page: "Free trial ends on 31/10/2026. No card on file, nothing will be charged." */
  private static final Pattern TRIAL_ENDS = Pattern.compile(
        "^Free trial ends on (\\d{2}/\\d{2}/\\d{4})\\. No card on file, nothing will be charged\\.$");
  private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final ZoneId ZURICH = ZoneId.of("Europe/Zurich");
  private static final LocatorAssertions.IsVisibleOptions SLOW =
        new LocatorAssertions.IsVisibleOptions().setTimeout(30_000);
  private static final LocatorAssertions.IsVisibleOptions TOAST =
        new LocatorAssertions.IsVisibleOptions().setTimeout(15_000);

  // PREM-1

  @Story("PREM-1 A Free user starts the Premium trial in Plan & Billing")
  @Test(priority = 101, groups = "premium", dependsOnGroups = "registered",
        description = "PREM-1 Trial starts without checkout")
  public void prem1_trialStartsWithoutCheckout() {
    open("/settings?section=plan");
    assertThat(exactText("You're currently on the Free plan.")).isVisible(SLOW); // precondition: still Free
    Locator trial = main().getByRole(AriaRole.BUTTON, exactName(TRIAL_BUTTON));
    assertThat(trial).hasCount(1);
    trial.click();
    try {
      assertThat(notifications().getByText(TRIAL_STARTED)).isVisible(TOAST);
    } finally {
      if (stripeCheckoutShown()) {
        page.keyboard().press("Escape"); // nothing is typed into the checkout
        Assert.fail("Stripe checkout opened instead of the trial");
      }
    }
  }

  // PREM-2

  @Story("PREM-2 Plan & Billing shows the active trial")
  @Test(priority = 102, dependsOnGroups = {"registered", "premium"}, description = "PREM-2 Trial plan state")
  public void prem2_trialPlanState() {
    open("/settings?section=plan");
    assertThat(exactText("You're on the Premium plan.")).isVisible(SLOW);
    Locator ends = main().getByText(TRIAL_ENDS);
    assertThat(ends).isVisible();
    Matcher m = TRIAL_ENDS.matcher(ends.innerText().trim());
    Assert.assertTrue(m.matches(), "unexpected trial end text: " + ends.innerText());
    LocalDate today = LocalDate.now(ZURICH);
    // activation day + 7; +8 is accepted when the run crossed midnight between prem1 and prem2
    List<String> expected = List.of(today.plusDays(7).format(DAY), today.plusDays(8).format(DAY));
    Assert.assertTrue(expected.contains(m.group(1)), "trial end " + m.group(1) + " is not today + 7: " + expected);
    assertThat(main().getByRole(AriaRole.BUTTON, exactName("Free plan"))).isDisabled();
    assertThat(main().getByRole(AriaRole.BUTTON, exactName("Manage subscription"))).isVisible(); // presence only
    assertThat(main().getByRole(AriaRole.BUTTON, exactName(TRIAL_BUTTON))).hasCount(0);
    assertThat(main().getByRole(AriaRole.BUTTON, exactName("Cancel subscription"))).hasCount(0);
  }

  // ---------------------------------------------------------------------------------------------
  // Helpers

  /** Read in the browser: the checkout of the site is an embedded Stripe iframe (CheckoutModal). */
  private boolean stripeCheckoutShown() {
    return (Boolean) page.evaluate("() => [...document.querySelectorAll('iframe')].some(f => /stripe/i.test(f.src))");
  }

  private Locator main() {
    return page.getByRole(AriaRole.MAIN);
  }

  private Locator notifications() {
    return page.getByRole(AriaRole.REGION,
          new Page.GetByRoleOptions().setName(Pattern.compile("^Notifications")));
  }

  private Locator exactText(String text) {
    return page.getByText(text, new Page.GetByTextOptions().setExact(true));
  }

  private static Locator.GetByRoleOptions exactName(String name) {
    return new Locator.GetByRoleOptions().setName(name).setExact(true);
  }
}
