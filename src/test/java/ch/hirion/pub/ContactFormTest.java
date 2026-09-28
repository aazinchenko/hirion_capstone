package ch.hirion.pub;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Group CONT: contact form, openspec/changes/add-public-coverage/specs/contact-form.
 * NEVER a real submit: every non-GET request is answered locally before it leaves the browser.
 */
public class ContactFormTest extends PublicPageTest {

  private static final String EMAIL = "qa-contact@example.com";
  private final List<String> nonGet = new CopyOnWriteArrayList<>();

  @BeforeMethod(alwaysRun = true) // runs after BaseTest.openContext (superclass first)
  public void mockNetworkAndOpenContact() {
    nonGet.clear();
    page.route("**/*", route -> {
      Request request = route.request();
      if ("GET".equals(request.method())) {
        route.resume();
        return;
      }
      nonGet.add(request.method() + " " + request.url() + " " + request.postData());
      route.fulfill(new Route.FulfillOptions().setStatus(200).setContentType("application/json").setBody("{}"));
    });
    open("/contact");
  }

  private Locator send() {
    return role(AriaRole.BUTTON, "Send message");
  }

  /** Waits 2 s for a non-GET request and fails if one shows up or was already recorded. */
  private void assertNoNonGetRequest(int expectedRecorded) {
    try {
      page.waitForRequest(r -> !"GET".equals(r.method()),
            new Page.WaitForRequestOptions().setTimeout(2000), () -> { });
      fail("unexpected non-GET request, recorded: " + nonGet);
    } catch (TimeoutError expected) {
      // no further request within 2 s
    }
    assertEquals(nonGet.size(), expectedRecorded, "non-GET requests: " + nonGet);
  }

  private void fillValidExceptEmail() {
    field("Name").fill("QA Contact");
    field("Subject").fill("QA test, not delivered");
    field("Message").fill("Automated test message. The network is mocked; nothing is sent.");
  }

  // CONT-1

  @Test(description = "CONT-1 Contact form is rendered")
  public void cont1_formRendered() {
    assertThat(h1("Talk to the team.")).isVisible();
    for (String label : new String[] {"Name", "Email", "Subject", "Message"}) {
      assertThat(field(label)).hasAttribute("required", Pattern.compile(".*"));
    }
    assertThat(send()).isVisible();
  }

  // CONT-2

  @Test(description = "CONT-2 Empty form is blocked")
  public void cont2_emptyFormBlocked() {
    nonGet.clear();
    send().click();
    assertTrue((Boolean) field("Name").evaluate("el => el.validity.valueMissing"),
          "Name should report validity.valueMissing");
    assertNoNonGetRequest(0);
  }

  @Test(description = "CONT-2 Invalid email is blocked")
  public void cont2_invalidEmailBlocked() {
    fillValidExceptEmail();
    field("Email").fill("not-an-email");
    nonGet.clear();
    send().click();
    assertTrue((Boolean) field("Email").evaluate("el => el.validity.typeMismatch"),
          "Email should report validity.typeMismatch");
    assertNoNonGetRequest(0);
  }

  // CONT-3

  @Test(description = "CONT-3 Honeypot attributes")
  public void cont3_honeypotAttributes() {
    Locator honeypot = page.locator("input[name='website']"); // locator-exception: contact-form honeypot
    assertThat(honeypot).hasAttribute("tabindex", "-1");
    assertThat(honeypot).hasAttribute("autocomplete", "off");
  }

  // CONT-4

  @Test(description = "CONT-4 Valid submit with a mocked network")
  public void cont4_validSubmitMocked() {
    fillValidExceptEmail();
    field("Email").fill(EMAIL);
    nonGet.clear();
    Request request = page.waitForRequest(r -> !"GET".equals(r.method()), () -> send().click());
    String body = request.postData();
    assertTrue(body != null && body.contains(EMAIL), "request body should contain " + EMAIL + ": " + body);
    assertNoNonGetRequest(1);
  }
}
