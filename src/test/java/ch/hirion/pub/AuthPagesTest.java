package ch.hirion.pub;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * Group AUTH: guest redirects, login, forgot-password and 404 pages,
 * openspec/changes/add-public-coverage/specs/access-and-quality. Nothing is ever submitted.
 */
@Epic("Public site")
@Feature("access-and-quality")
public class AuthPagesTest extends PublicPageTest {

  private static final Pattern LOGIN_URL = Pattern.compile("/login([?#].*)?$");

  // AUTH-1

  @Story("AUTH-1 Guests are redirected to sign in")
  @Test(description = "AUTH-1 Guest opens the dashboard")
  public void auth1_guestDashboardRedirects() {
    open("/dashboard");
    assertThat(page).hasURL(LOGIN_URL);
    assertThat(h1("Welcome back")).isVisible();
  }

  @Story("AUTH-1 Guests are redirected to sign in")
  @Test(description = "AUTH-1 Guest opens settings")
  public void auth1_guestSettingsRedirects() {
    open("/settings");
    assertThat(page).hasURL(LOGIN_URL);
    assertThat(h1("Welcome back")).isVisible();
  }

  @Story("AUTH-1 Guests are redirected to sign in")
  @Test(description = "AUTH-1 Guest opens the other private pages")
  public void auth1_guestOtherPrivatePagesRedirect() {
    for (String path : new String[] {"/onboarding", "/employer/", "/admin"}) {
      open(path);
      assertThat(page).hasURL(LOGIN_URL);
      assertThat(h1("Welcome back")).isVisible();
    }
  }

  // AUTH-2

  @Story("AUTH-2 Login page")
  @Test(description = "AUTH-2 Login page is rendered")
  public void auth2_loginPageRendered() {
    open("/login");
    assertThat(h1("Welcome back")).isVisible();
    assertThat(field("Email")).isVisible();
    assertThat(field("Password")).isVisible(); // exact: "Show password" also matches "Password"
    assertThat(role(AriaRole.BUTTON, "Sign in")).isVisible();
  }

  @Story("AUTH-2 Login page")
  @Test(description = "AUTH-2 Forgot password link")
  public void auth2_forgotPasswordLink() {
    open("/login");
    role(AriaRole.LINK, "Forgot password?").click();
    assertThat(page).hasURL(Pattern.compile("/forgot-password([?#].*)?$"));
  }

  @Story("AUTH-2 Login page")
  @Test(description = "AUTH-2 Create account link")
  public void auth2_createAccountLink() {
    open("/login");
    assertThat(role(AriaRole.LINK, "Create one")).hasAttribute("href", "/signup");
  }

  // AUTH-3

  @Story("AUTH-3 Login fields support autofill")
  @Test(description = "AUTH-3 Login autocomplete attributes")
  public void auth3_loginAutocomplete() {
    open("/login");
    field("Email").waitFor(); // preconditions: a missing field fails here, not as a wrong attribute
    field("Password").waitFor();
    assertThat(field("Email")).hasAttribute("autocomplete", "email");
    assertThat(field("Password")).hasAttribute("autocomplete", "current-password");
  }

  // AUTH-4

  @Story("AUTH-4 Forgot-password page")
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

  @Story("AUTH-5 Unknown pages show a 404 page")
  @Test(description = "AUTH-5 Unknown path")
  public void auth5_unknownPathIs404() {
    Response response = open("/this-page-does-not-exist-qa");
    assertEquals(response.status(), 404);
    assertThat(h1("404")).isVisible();
    assertThat(page.getByText("Page not found", new Page.GetByTextOptions().setExact(true)))
          .isVisible();
    assertThat(role(AriaRole.LINK, "Go home")).hasAttribute("href", "/");
  }

  // AUTH-7: the buttons are only checked for visibility, never clicked (AGENTS.md: no OAuth)

  private void assertSocialSignInButtons() {
    assertThat(role(AriaRole.BUTTON, "Continue with Google")).isVisible();
    assertThat(role(AriaRole.BUTTON, "Continue with Apple")).isVisible();
  }

  @Story("AUTH-7 Login and signup offer Google and Apple sign-in")
  @Test(description = "AUTH-7 Social sign-in buttons on the login page")
  public void auth7_socialButtonsOnLogin() {
    open("/login");
    assertSocialSignInButtons();
    assertThat(page.getByText("OR", new Page.GetByTextOptions().setExact(true))).isVisible();
  }

  @Story("AUTH-7 Login and signup offer Google and Apple sign-in")
  @Test(description = "AUTH-7 Social sign-in buttons on signup step 1")
  public void auth7_socialButtonsOnSignup() {
    open("/signup");
    assertThat(page.getByText("Step 1 of 4", new Page.GetByTextOptions().setExact(true))).isVisible();
    assertSocialSignInButtons();
  }

  // AUTH-8

  @Story("AUTH-8 Reset-password page without a valid link")
  @Test(description = "AUTH-8 Reset-password page opened directly")
  public void auth8_resetPasswordWithoutLink() {
    open("/reset-password");
    assertThat(h1("Set a new password")).isVisible();
    assertThat(page.getByText("This reset link is invalid or has expired. Request a new one.",
          new Page.GetByTextOptions().setExact(true))).isVisible();
    assertThat(role(AriaRole.LINK, "Send reset link")).hasAttribute("href", "/forgot-password");
    assertThat(field("New password")).hasCount(0);
  }
}
