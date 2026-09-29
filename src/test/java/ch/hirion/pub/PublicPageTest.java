package ch.hirion.pub;

import ch.hirion.support.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;

/**
 * Shared page opening for the public-page tests of add-public-coverage. Lives in ch.hirion.pub because
 * support/** is not editable inside a change (.claude/settings.json); same wait as SmokeTest.openHome.
 */
public abstract class PublicPageTest extends BaseTest {

  /** Short timeout for KNOWN BUG assertions, so the expected failure does not cost 10 s. */
  protected static final LocatorAssertions.HasAttributeOptions QUICK_ATTR =
        new LocatorAssertions.HasAttributeOptions().setTimeout(3000);

  /** Navigates to a path and waits until TanStack Start has hydrated the page. */
  protected Response open(String path) {
    page.setDefaultNavigationTimeout(30_000);
    Response response = page.navigate(path,
          new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    waitForHydration();
    return response;
  }

  /** Reloads the current page and waits for hydration again, like open(). */
  protected Response reload() {
    Response response = page.reload(
          new Page.ReloadOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    waitForHydration();
    return response;
  }

  private void waitForHydration() {
    page.waitForFunction("() => !('$_TSR' in window)", null,
          new Page.WaitForFunctionOptions().setTimeout(30_000));
  }

  protected Locator h1(String name) {
    return page.getByRole(AriaRole.HEADING,
          new Page.GetByRoleOptions().setName(name).setExact(true).setLevel(1));
  }

  protected Locator field(String label) {
    return page.getByLabel(label, new Page.GetByLabelOptions().setExact(true));
  }
}
