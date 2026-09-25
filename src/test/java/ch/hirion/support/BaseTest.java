package ch.hirion.support;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BaseTest {

  public static final String BASE_URL = System.getProperty("baseUrl", "https://hirion.ch");
  protected static final Path FIXTURES = Paths.get("src/test/resources/fixtures");
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

  /**
   * Поиск по роли и ТОЧНОМУ имени: "Continue" не совпадёт с "Continue with Google".
   */
  protected Locator role(AriaRole role, String name) {
    return page.getByRole(role, new Page.GetByRoleOptions().setName(name).setExact(true));
  }
}
