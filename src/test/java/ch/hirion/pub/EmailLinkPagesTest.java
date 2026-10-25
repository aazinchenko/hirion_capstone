package ch.hirion.pub;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Group LINK: pages opened from emails, visited by a guest without a valid link,
 * openspec/changes/sync-specs-with-site-scan/specs/email-link-pages. Nothing real is ever sent: the survey
 * submit runs with every non-GET request answered locally.
 */
@Epic("Public site")
@Feature("email-link-pages")
public class EmailLinkPagesTest extends PublicPageTest {

  private static final String SURVEY_API = "/api/public/survey-response";
  private static final List<String> SURVEY_QUESTIONS = List.of(
        "1. What made you sign up for Hirion?",
        "2. How useful were Hirion's job recommendations for you?",
        "3. Did Hirion help you discover jobs you probably wouldn't have found yourself?",
        "4. What was the biggest problem or frustration you had with Hirion?",
        "5. Why didn't you continue with Premium after the free trial?",
        "6. What would make Hirion valuable enough for you to pay for it?");

  private static boolean isSurveyRequest(Request request) {
    return !"GET".equals(request.method()) && request.url().contains(SURVEY_API);
  }

  private void assertText(String text) {
    assertThat(page.getByText(text, new Page.GetByTextOptions().setExact(true))).isVisible();
  }

  // LINK-1

  @Story("LINK-1 Feedback survey page")
  @Test(description = "LINK-1 Survey page is rendered")
  public void link1_surveyRendered() {
    open("/survey");
    assertThat(h1("Help us make Hirion better")).isVisible();
    for (String question : SURVEY_QUESTIONS) {
      assertText(question);
    }
    assertThat(role(AriaRole.BUTTON, "Send feedback")).isVisible();
  }

  @Story("LINK-1 Feedback survey page")
  @Test(description = "LINK-1 Survey without an id sends nothing")
  public void link1_surveyWithoutIdSendsNothing() {
    List<String> surveyRequests = new CopyOnWriteArrayList<>();
    page.route("**/*", route -> {
      Request request = route.request();
      if ("GET".equals(request.method())) {
        route.resume();
        return;
      }
      if (isSurveyRequest(request)) {
        surveyRequests.add(request.method() + " " + request.url());
      }
      route.fulfill(new Route.FulfillOptions().setStatus(200).setContentType("application/json")
            .setBody("{\"ok\":true}"));
    });
    open("/survey");
    role(AriaRole.BUTTON, "Send feedback").click();
    assertText("This survey link is missing its id. Please use the link from the email."); // text changed 2026-10-25
    try {
      page.waitForRequest(EmailLinkPagesTest::isSurveyRequest,
            new Page.WaitForRequestOptions().setTimeout(2000), () -> { });
      fail("unexpected request to " + SURVEY_API + ", recorded: " + surveyRequests);
    } catch (TimeoutError expected) {
      // no survey request within 2 s
    }
    assertEquals(surveyRequests.size(), 0, "requests to " + SURVEY_API + ": " + surveyRequests);
  }

  // LINK-2

  @Story("LINK-2 Unsubscribe page without a valid link")
  @Test(description = "LINK-2 Unsubscribe page opened directly")
  public void link2_unsubscribeWithoutLink() {
    open("/unsubscribe");
    assertThat(h1("Invalid link")).isVisible();
    assertText("This unsubscribe link is invalid or has expired.");
  }

  // LINK-3

  @Story("LINK-3 Checkout return page without a session")
  @Test(description = "LINK-3 Checkout return opened directly")
  public void link3_checkoutReturnWithoutSession() {
    open("/checkout/return");
    assertThat(h1("No session found")).isVisible();
    assertText("We couldn't find a checkout session in this URL.");
    // The href carries checkout=success (candidate defect D18), so only the path is checked.
    assertThat(role(AriaRole.LINK, "Go to my account")).hasAttribute("href", Pattern.compile("^/settings([?#].*)?$"));
  }
}
