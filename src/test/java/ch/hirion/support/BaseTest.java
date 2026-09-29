package ch.hirion.support;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BaseTest {

  public static final String BASE_URL = System.getProperty("baseUrl", "https://hirion.ch");
  protected static final Path FIXTURES = Paths.get("src/test/resources/fixtures");
  /** Short timeout for KNOWN BUG assertions, so the expected failure does not cost 10 s. */
  protected static final LocatorAssertions.HasAttributeOptions QUICK_ATTR =
        new LocatorAssertions.HasAttributeOptions().setTimeout(3000);
  protected Playwright playwright;
  protected Browser browser;
  protected BrowserContext context;
  protected Page page;

  @BeforeClass(alwaysRun = true)
  public void launchBrowser() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
          .setHeadless(!"false".equals(System.getProperty("headless"))));
  }

  /**
   * Подклассы переопределяют: размер экрана, сохранённая сессия.
   */
  protected Browser.NewContextOptions contextOptions() {
    return new Browser.NewContextOptions().setBaseURL(BASE_URL);
  }

  @BeforeMethod(alwaysRun = true)
  public void openContext() {
    context = browser.newContext(contextOptions());
    context.setDefaultTimeout(10_000);
    context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
    page = context.newPage();
  }

  @AfterMethod(alwaysRun = true)
  public void closeContext(ITestResult result) {
    Tracing.StopOptions stop = new Tracing.StopOptions();
    if (!result.isSuccess()) { // трасса только для упавших тестов
      stop.setPath(Paths.get("target/traces", getClass().getSimpleName() + "-"
            + result.getMethod().getMethodName() + ".zip"));
    }
    context.tracing().stop(stop);
    context.close();
  }

  @AfterClass(alwaysRun = true)
  public void closeBrowser() {
    playwright.close();
  }

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

  /**
   * The site is server-rendered (TanStack Start): buttons are visible before React hydrates and clicks
   * before that are lost. TanStack deletes window.$_TSR once hydrated and the stream ended.
   */
  private void waitForHydration() {
    page.waitForFunction("() => !('$_TSR' in window)", null,
          new Page.WaitForFunctionOptions().setTimeout(30_000));
  }

  /**
   * Поиск по роли и ТОЧНОМУ имени: "Continue" не совпадёт с "Continue with Google".
   */
  protected Locator role(AriaRole role, String name) {
    return page.getByRole(role, new Page.GetByRoleOptions().setName(name).setExact(true));
  }

  protected Locator h1(String name) {
    return page.getByRole(AriaRole.HEADING,
          new Page.GetByRoleOptions().setName(name).setExact(true).setLevel(1));
  }

  /** Exact label: "Password" must not also match the "Show password" button. */
  protected Locator field(String label) {
    return page.getByLabel(label, new Page.GetByLabelOptions().setExact(true));
  }
}
