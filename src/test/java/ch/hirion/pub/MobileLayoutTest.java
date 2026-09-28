package ch.hirion.pub;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.opentest4j.AssertionFailedError;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;

/** Group QA (phone, 375 x 812): specs/access-and-quality QA-1, QA-2. */
public class MobileLayoutTest extends PublicPageTest {

  @Override
  protected Browser.NewContextOptions contextOptions() {
    return super.contextOptions().setViewportSize(375, 812);
  }

  // QA-1

  @Test(description = "QA-1 No horizontal scroll at 375 px")
  public void qa1_noHorizontalScroll() {
    open("/");
    int scrollWidth = ((Number) page.evaluate("() => document.documentElement.scrollWidth")).intValue();
    assertTrue(scrollWidth <= 375, "scrollWidth " + scrollWidth + " > 375");
  }

  // QA-2 (known defect D3)

  @Test(expectedExceptions = AssertionFailedError.class,
        description = "QA-2 Sign in reachable at 375 px -- KNOWN BUG D3: header links and Sign in hidden, no burger menu")
  public void qa2_signInReachableOnPhone() {
    open("/");
    Locator signIn = role(AriaRole.LINK, "Sign in");
    if (!signIn.isVisible()) {
      // A header menu button: collapsed, not the language button "EN".
      Locator menuButton = page.getByRole(AriaRole.BANNER)
            .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setExpanded(false))
            .filter(new Locator.FilterOptions().setHasNotText("EN"));
      if (menuButton.count() > 0 && menuButton.first().isVisible()) {
        menuButton.first().click();
      }
    }
    assertThat(signIn).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));
  }
}
