package ch.hirion.pub;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import org.opentest4j.AssertionFailedError;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * Group AUTH: guest redirects, login, forgot-password and 404 pages,
 * openspec/changes/add-public-coverage/specs/access-and-quality. Nothing is ever submitted.
 */
public class AuthPagesTest extends PublicPageTest {

  private static final Pattern LOGIN_URL = Pattern.compile("/login([?#].*)?$");

  // AUTH-1

  @Test(description = "AUTH-1 Guest opens the dashboard")
  public void auth1_guestDashboardRedirects() {
    open("/dashboard");
    assertThat(page).hasURL(LOGIN_URL);
    assertThat(h1("Welcome back")).isVisible();
  }

  @Test(description = "AUTH-1 Guest opens settings")
  public void auth1_guestSettingsRedirects() {
    open("/settings");
    assertThat(page).hasURL(LOGIN_URL);
    assertThat(h1("Welcome back")).isVisible();
  }

  // AUTH-2

  @Test(description = "AUTH-2 Login page is rendered")
  public void auth2_loginPageRendered() {
    open("/login");
    assertThat(h1("Welcome back")).isVisible();
    assertThat(field("Email")).isVisible();
    assertThat(field("Password")).isVisible(); // exact: "Show password" also matches "Password"
    assertThat(role(AriaRole.BUTTON, "Sign in")).isVisible();
  }

  @Test(description = "AUTH-2 Forgot password link")
  public void auth2_forgotPasswordLink() {
    open("/login");
    role(AriaRole.LINK, "Forgot password?").click();
    assertThat(page).hasURL(Pattern.compile("/forgot-password([?#].*)?$"));
  }

  @Test(description = "AUTH-2 Create account link")
  public void auth2_createAccountLink() {
    open("/login");
    assertThat(role(AriaRole.LINK, "Create one")).hasAttribute("href", "/signup");
  }

  // AUTH-3 (known defect D5)

  @Test(expectedExceptions = AssertionFailedError.class,
        description = "AUTH-3 Login autocomplete attributes -- KNOWN BUG D5: /login inputs have no autocomplete")
  public void auth3_loginAutocomplete() {
    open("/login");
    field("Email").waitFor(); // preconditions: a missing field is a real failure, not the known bug
    field("Password").waitFor();
    assertThat(field("Email")).hasAttribute("autocomplete", "email", QUICK_ATTR);
    assertThat(field("Password")).hasAttribute("autocomplete", "current-password", QUICK_ATTR);
  }

  // AUTH-4

  @Test(description = "AUTH-4 Forgot-password page is rendered")
  public void auth4_forgotPasswordPage() {
    open("/forgot-password");
    assertThat(h1("Reset your password")).isVisible();
    assertThat(field("Email")).isVisible();
    assertThat(role(AriaRole.BUTTON, "Send reset link")).isVisible();
    assertThat(page.getByRole(AriaRole.MAIN).getByRole(AriaRole.LINK,
          new Locator.GetByRoleOptions().setName("Sign in").setExact(true))).hasAttribute("href", "/login");
  }

  // AUTH-5

  @Test(description = "AUTH-5 Unknown path")
  public void auth5_unknownPathIs404() {
    Response response = open("/this-page-does-not-exist-qa");
    assertEquals(response.status(), 404);
    assertThat(h1("404")).isVisible();
    assertThat(page.getByText("Page not found", new Page.GetByTextOptions().setExact(true)))
          .isVisible();
    assertThat(role(AriaRole.LINK, "Go home")).hasAttribute("href", "/");
  }
}
