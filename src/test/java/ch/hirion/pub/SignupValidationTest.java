package ch.hirion.pub;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Group REG, step 1 only: client-side checks on /signup, specs/access-and-quality REG-1, REG-2.
 * Nothing is submitted and no account is created, so these tests belong to the public suite.
 */
public class SignupValidationTest extends PublicPageTest {

  // REG-1

  @Test(description = "REG-1 Empty step 1 is blocked")
  public void reg1_emptyStepOneBlocked() {
    open("/signup");
    role(AriaRole.BUTTON, "Continue").click(); // exact: not "Continue with Google" / "Continue with Apple"
    assertThat(page.getByText("Please enter your first and last name.",
          new Page.GetByTextOptions().setExact(true))).isVisible();
    assertThat(page.getByText("Step 1 of 4", new Page.GetByTextOptions().setExact(true))).isVisible();
    assertThat(page).hasURL(Pattern.compile("/signup([?#].*)?$"));
  }

  // REG-2

  @Test(description = "REG-2 Show password")
  public void reg2_showPassword() {
    open("/signup");
    Locator password = field("Password"); // exact: the "Show password" button also matches "Password"
    password.fill("Secret-123!"); // made-up value, never submitted
    assertThat(password).hasAttribute("type", "password");
    role(AriaRole.BUTTON, "Show password").click();
    assertThat(password).hasAttribute("type", "text");
  }
}
