package ch.hirion.pub;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.opentest4j.AssertionFailedError;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Year;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Group FOOT: footer and legal pages, openspec/changes/add-public-coverage/specs/footer-and-legal. */
public class FooterLegalTest extends PublicPageTest {

  private static final Pattern VALID_MAILTO =
        Pattern.compile("^mailto:[^@\\s\\[\\]]+@[^@\\s\\[\\]]+\\.[a-z]{2,}$");

  private Locator footerLink(String name) {
    return page.getByRole(AriaRole.CONTENTINFO)
          .getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(name).setExact(true));
  }

  // FOOT-1

  @Test(description = "FOOT-1 Footer link targets")
  public void foot1_footerLinkTargets() {
    open("/");
    assertThat(footerLink("Privacy")).hasAttribute("href", "/privacy");
    assertThat(footerLink("Terms")).hasAttribute("href", "/terms");
    assertThat(footerLink("Imprint")).hasAttribute("href", "/imprint");
    assertThat(footerLink("Contact")).hasAttribute("href", "/contact");
  }

  @Test(description = "FOOT-1 Footer link opens the privacy policy")
  public void foot1_footerOpensPrivacy() {
    open("/");
    footerLink("Privacy").click();
    assertThat(page).hasURL(Pattern.compile("/privacy([?#].*)?$"));
    assertThat(h1("Privacy Policy")).isVisible();
  }

  @Test(description = "FOOT-1 Internal footer links respond")
  public void foot1_internalLinksRespond() {
    SoftAssert soft = new SoftAssert();
    for (String path : List.of("/", "/contact", "/privacy", "/terms", "/imprint")) {
      soft.assertEquals(page.request().get(BASE_URL + path).status(), 200, "status of " + path);
    }
    soft.assertAll(); // reports every broken link at once, not only the first
  }

  // FOOT-2

  @Test(description = "FOOT-2 Privacy page")
  public void foot2_privacyPage() {
    open("/privacy");
    assertThat(h1("Privacy Policy")).isVisible();
    assertThat(page).hasTitle("Privacy Policy, Hirion");
  }

  @Test(description = "FOOT-2 Terms page")
  public void foot2_termsPage() {
    open("/terms");
    assertThat(h1("Terms of Service")).isVisible();
    assertThat(page).hasTitle("Terms of Service, Hirion");
  }

  @Test(description = "FOOT-2 Imprint page")
  public void foot2_imprintPage() {
    open("/imprint");
    assertThat(h1("Imprint")).isVisible();
    assertThat(page).hasTitle("Imprint, Hirion");
  }

  // FOOT-3 (known defect D1)

  @Test(expectedExceptions = AssertionFailedError.class,
        description = "FOOT-3 Imprint has no unfilled templates -- KNOWN BUG D1: /imprint shows [FILL: ...] templates")
  public void foot3_imprintHasNoFillTemplates() {
    open("/imprint");
    h1("Imprint").waitFor(); // precondition: a TimeoutError here is a real failure, not the known bug
    assertThat(page.getByText("[FILL:"))
          .hasCount(0, new LocatorAssertions.HasCountOptions().setTimeout(3000));
  }

  @Test(expectedExceptions = AssertionFailedError.class,
        description = "FOOT-3 Imprint email is a valid mailto link -- KNOWN BUG D1: mailto:[FILL: ...]")
  public void foot3_imprintEmailIsValidMailto() {
    open("/imprint");
    h1("Imprint").waitFor(); // precondition: a TimeoutError here is a real failure, not the known bug
    @SuppressWarnings("unchecked")
    List<String> hrefs = (List<String>) page.getByRole(AriaRole.MAIN).evaluate(
          "main => [...main.querySelectorAll('a[href^=\"mailto:\"]')].map(a => a.getAttribute('href'))");
    if (hrefs.isEmpty() || !hrefs.stream().allMatch(h -> VALID_MAILTO.matcher(h).matches())) {
      throw new AssertionFailedError("every mailto link in main must be valid, found: " + hrefs);
    }
  }

  // FOOT-4

  @DataProvider
  public Object[][] anchors() {
    return new Object[][] {{"How it works", "how"}, {"Pricing", "pricing"},
          {"Sample matches", "examples"}, {"Manifesto", "manifesto"}, {"FAQ", "faq"}};
  }

  @Test(dataProvider = "anchors", description = "FOOT-4 Footer anchor scrolls to its section")
  public void foot4_anchorScrolls(String name, String id) {
    open("/");
    footerLink(name).click();
    assertThat(page).hasURL(Pattern.compile("#" + id + "$"));
    assertThat(page.locator("#" + id)).isInViewport(); // locator-exception: section anchor #id
  }

  // FOOT-5

  @Test(description = "FOOT-5 LinkedIn link attributes")
  public void foot5_linkedInIsSafe() {
    open("/");
    Locator li = footerLink("Hirion on LinkedIn");
    assertThat(li).hasAttribute("href", "https://www.linkedin.com/company/hirionch");
    assertThat(li).hasAttribute("target", "_blank");
    assertThat(li).hasAttribute("rel", Pattern.compile("noopener"));
    assertThat(li).hasAttribute("rel", Pattern.compile("noreferrer"));
  }

  // FOOT-6

  @Test(description = "FOOT-6 Privacy shows its update date")
  public void foot6_privacyLastUpdated() {
    open("/privacy");
    assertThat(page.getByText(Pattern.compile("^Last updated:"))).isVisible();
  }

  @Test(description = "FOOT-6 Terms shows its update date")
  public void foot6_termsLastUpdated() {
    open("/terms");
    assertThat(page.getByText(Pattern.compile("^Last updated:"))).isVisible();
  }

  // FOOT-7

  @Test(description = "FOOT-7 Copyright year")
  public void foot7_copyrightYear() {
    open("/");
    assertThat(page.getByRole(AriaRole.CONTENTINFO)).containsText("© " + Year.now().getValue());
  }
}