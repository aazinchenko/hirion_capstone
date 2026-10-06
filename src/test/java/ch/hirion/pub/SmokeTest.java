package ch.hirion.pub;

import ch.hirion.support.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Group PUB: home page smoke, openspec/changes/add-home-smoke/specs/home-page/spec.md. Guest only. */
@Epic("Public site")
@Feature("home-page")
public class SmokeTest extends BaseTest {

  private Locator faq;
  private Locator pricing;

  @BeforeMethod(alwaysRun = true) // runs after BaseTest.openContext (superclass first)
  public void openHome() {
    // "load" never fires within 10 s on the live site; assertions auto-wait for their elements anyway.
    // Page loads get their own 30 s limit (the live site is slow in bursts); element waits stay at 10 s.
    page.setDefaultNavigationTimeout(30_000);
    page.navigate("/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    // The page is server-rendered (TanStack Start): buttons are visible before React hydrates and
    // clicks before that are lost. TanStack deletes window.$_TSR once hydrated and the stream ended.
    page.waitForFunction("() => !('$_TSR' in window)", null,
          new Page.WaitForFunctionOptions().setTimeout(30_000));
    faq = page.locator("#faq"); // locator-exception: section anchor #faq
    pricing = page.locator("#pricing"); // locator-exception: section anchor #pricing
  }

  private Locator inSection(Locator section, AriaRole role, String name) {
    return section.getByRole(role, new Locator.GetByRoleOptions().setName(name).setExact(true));
  }

  private Locator text(Locator section, String exact) {
    return section.getByText(exact, new Locator.GetByTextOptions().setExact(true));
  }

  // PUB-1

  @Story("PUB-1 Hero call to action opens signup")
  @Test(description = "PUB-1 Guest clicks the hero CTA")
  public void pub1_heroCtaOpensSignup() {
    role(AriaRole.LINK, "Get my personalized jobs").click();
    assertThat(page).hasURL(Pattern.compile("/signup([?#].*)?$"));
  }

  // PUB-2

  @Story("PUB-2 FAQ items expand as an accordion")
  @Test(description = "PUB-2 FAQ item starts collapsed")
  public void pub2_faqStartsCollapsed() {
    Locator question = inSection(faq, AriaRole.BUTTON, "Is Hirion free to use?");
    question.scrollIntoViewIfNeeded();
    assertThat(question).hasAttribute("aria-expanded", "false");
  }

  @Story("PUB-2 FAQ items expand as an accordion")
  @Test(description = "PUB-2 Clicking a FAQ item expands it")
  public void pub2_faqClickExpands() {
    Locator question = inSection(faq, AriaRole.BUTTON, "Is Hirion free to use?");
    question.click();
    assertThat(question).hasAttribute("aria-expanded", "true");
  }

  @Story("PUB-2 FAQ items expand as an accordion")
  @Test(description = "PUB-2 Expanded FAQ item shows its answer")
  public void pub2_faqShowsAnswer() {
    inSection(faq, AriaRole.BUTTON, "Is Hirion free to use?").click();
    assertThat(faq.getByText(Pattern.compile("^Yes, start free with weekly job alerts\\.")))
          .isVisible();
  }

  // PUB-3

  @Story("PUB-3 Pricing shows prices per billing period")
  @Test(description = "PUB-3 Pricing is visible to a guest")
  public void pub3_pricingVisibleToGuest() {
    assertThat(pricing).isVisible();
    assertThat(inSection(pricing, AriaRole.BUTTON, "Monthly")).isVisible();
    assertThat(inSection(pricing, AriaRole.BUTTON, "Quarterly")).isVisible();
    assertThat(inSection(pricing, AriaRole.BUTTON, "Yearly")).isVisible();
  }

  @Story("PUB-3 Pricing shows prices per billing period")
  @Test(description = "PUB-3 Yearly is the default period")
  public void pub3_yearlyIsDefault() {
    pricing.scrollIntoViewIfNeeded();
    assertThat(text(pricing, "CHF 10")).isVisible();
    assertThat(text(pricing, "CHF 120 billed yearly")).isVisible();
  }

  @Story("PUB-3 Pricing shows prices per billing period")
  @Test(description = "PUB-3 Monthly price")
  public void pub3_monthlyPrice() {
    inSection(pricing, AriaRole.BUTTON, "Monthly").click();
    assertThat(text(pricing, "CHF 15")).isVisible();
    assertThat(text(pricing, "CHF 15 billed monthly")).isVisible();
    assertThat(text(pricing, "CHF 120 billed yearly")).isHidden();
  }

  @Story("PUB-3 Pricing shows prices per billing period")
  @Test(description = "PUB-3 Quarterly price")
  public void pub3_quarterlyPrice() {
    inSection(pricing, AriaRole.BUTTON, "Quarterly").click();
    assertThat(text(pricing, "CHF 13")).isVisible();
    assertThat(text(pricing, "CHF 39 billed quarterly")).isVisible();
  }

  @Story("PUB-3 Pricing shows prices per billing period")
  @Test(description = "PUB-3 Switching back to Yearly")
  public void pub3_backToYearly() {
    inSection(pricing, AriaRole.BUTTON, "Monthly").click();
    inSection(pricing, AriaRole.BUTTON, "Yearly").click();
    assertThat(text(pricing, "CHF 10")).isVisible();
    assertThat(text(pricing, "CHF 120 billed yearly")).isVisible();
  }

  // PUB-4

  @Story("PUB-4 Pricing period toggle exposes its state")
  @Test(description = "PUB-4 Selected period is aria-pressed")
  public void pub4_selectedPeriodIsAriaPressed() {
    inSection(pricing, AriaRole.BUTTON, "Monthly").click();
    assertThat(inSection(pricing, AriaRole.BUTTON, "Monthly")).hasAttribute("aria-pressed", "true");
    assertThat(inSection(pricing, AriaRole.BUTTON, "Yearly")).hasAttribute("aria-pressed", "false");
  }

  // PUB-5: stop at the /signup URL, never continue the signup

  @Story("PUB-5 Plan cards link to signup")
  @Test(description = "PUB-5 Free plan CTA")
  public void pub5_freePlanCta() {
    inSection(pricing, AriaRole.LINK, "Start free").click();
    assertThat(page).hasURL(Pattern.compile("/signup\\?(.*&)?plan=free(&.*)?$"));
  }

  @Story("PUB-5 Plan cards link to signup")
  @Test(description = "PUB-5 Paid plan CTA")
  public void pub5_paidPlanCta() {
    inSection(pricing, AriaRole.LINK, "Start 7-day free trial").click();
    assertThat(page).hasURL(Pattern.compile("/signup\\?(.*&)?plan=trial(&.*)?$"));
  }
}
