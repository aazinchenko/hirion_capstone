package ch.hirion.pub;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
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
 * Only requests to the form endpoint are counted: Stripe.js posts its own beacon at random moments.
 */
@Epic("Public site")
@Feature("contact-form")
public class ContactFormTest extends PublicPageTest {

  private static final String EMAIL = "qa-contact@example.com";
  private static final String FORM_API = "/api/public/contact";
  private static final String CONFIRMATION = "Thanks! Your message is ready to send.";
  private final List<String> formRequests = new CopyOnWriteArrayList<>();

  @BeforeMethod(alwaysRun = true) // runs after BaseTest.openContext (superclass first)
  public void mockNetworkAndOpenContact() {
    formRequests.clear();
    page.route("**/*", route -> {
      Request request = route.request();
      if ("GET".equals(request.method())) {
        route.resume();
        return;
      }
      if (isFormRequest(request)) {
        formRequests.add(request.method() + " " + request.url() + " " + request.postData());
      }
      route.fulfill(new Route.FulfillOptions().setStatus(200).setContentType("application/json")
            .setBody("{\"ok\":true}"));
    });
    open("/contact");
  }

  private static boolean isFormRequest(Request request) {
    return !"GET".equals(request.method()) && request.url().contains(FORM_API);
  }

  private Locator send() {
    return role(AriaRole.BUTTON, "Send message");
  }

  /** Waits 2 s for a request to the form endpoint and fails if one shows up; then checks the count. */
  private void assertNoMoreFormRequests(int expectedRecorded) {
    try {
      page.waitForRequest(ContactFormTest::isFormRequest,
            new Page.WaitForRequestOptions().setTimeout(2000), () -> { });
      fail("unexpected request to " + FORM_API + ", recorded: " + formRequests);
    } catch (TimeoutError expected) {
      // no further form request within 2 s
    }
    assertEquals(formRequests.size(), expectedRecorded, "requests to " + FORM_API + ": " + formRequests);
  }

  private void fillValidExceptEmail() {
    field("Name").fill("QA Contact");
    field("Subject").fill("QA test, not delivered");
    field("Message").fill("Automated test message. The network is mocked; nothing is sent.");
  }

  // CONT-1

  @Story("CONT-1 Contact form fields")
  @Test(description = "CONT-1 Contact form is rendered")
  public void cont1_formRendered() {
    assertThat(h1("Talk to the team.")).isVisible();
    for (String label : new String[] {"Name", "Email", "Subject", "Message"}) {
      assertThat(field(label)).hasAttribute("required", Pattern.compile(".*"));
    }
    assertThat(send()).isVisible();
  }

  // CONT-2

  @Story("CONT-2 Invalid input is not sent")
  @Test(description = "CONT-2 Empty form is blocked")
  public void cont2_emptyFormBlocked() {
    send().click();
    assertTrue((Boolean) field("Name").evaluate("el => el.validity.valueMissing"),
          "Name should report validity.valueMissing");
    assertThat(page).hasURL(Pattern.compile("/contact([?#].*)?$"));
    assertNoMoreFormRequests(0);
  }

  @Story("CONT-2 Invalid input is not sent")
  @Test(description = "CONT-2 Invalid email is blocked")
  public void cont2_invalidEmailBlocked() {
    fillValidExceptEmail();
    field("Email").fill("not-an-email");
    send().click();
    assertTrue((Boolean) field("Email").evaluate("el => el.validity.typeMismatch"),
          "Email should report validity.typeMismatch");
    assertNoMoreFormRequests(0);
  }

  // CONT-3

  @Story("CONT-3 Honeypot is hidden from people")
  @Test(description = "CONT-3 Honeypot attributes")
  public void cont3_honeypotAttributes() {
    Locator honeypot = page.locator("input[name='website']"); // locator-exception: contact-form honeypot
    assertThat(honeypot).hasAttribute("tabindex", "-1");
    assertThat(honeypot).hasAttribute("autocomplete", "off");
    assertTrue((Boolean) honeypot.evaluate("el => el.closest('[aria-hidden=\"true\"]') !== null"),
          "honeypot should be inside an element with aria-hidden=\"true\"");
    assertThat(honeypot).not().isInViewport();
  }

  // CONT-4

  @Story("CONT-4 Valid input is sent once")
  @Test(description = "CONT-4 Valid submit with a mocked network")
  public void cont4_validSubmitMocked() {
    fillValidExceptEmail();
    field("Email").fill(EMAIL);
    Request request = page.waitForRequest(ContactFormTest::isFormRequest, () -> send().click());
    assertEquals(request.method(), "POST");
    String body = request.postData();
    assertTrue(body != null && body.contains(EMAIL), "request body should contain " + EMAIL + ": " + body);
    assertThat(page.getByText(CONFIRMATION, new Page.GetByTextOptions().setExact(true))).isVisible();
    assertNoMoreFormRequests(1);
  }
}
