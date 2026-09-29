package ch.hirion.pub;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Group QA (desktop): console errors and SEO basics, specs/access-and-quality QA-3, QA-4. */
public class QualityTest extends PublicPageTest {

  // QA-3

  @Test(description = "QA-3 No console errors on public pages")
  public void qa3_noConsoleErrors() {
    List<String> errors = new CopyOnWriteArrayList<>();
    String[] current = {""};
    page.onConsoleMessage(msg -> {
      if ("error".equals(msg.type())) errors.add(current[0] + " console: " + msg.text());
    });
    page.onPageError(err -> errors.add(current[0] + " pageerror: " + err));
    for (String path : new String[] {"/", "/contact", "/login", "/privacy", "/terms", "/imprint"}) {
      current[0] = path;
      open(path);
    }
    assertTrue(errors.isEmpty(), "console errors:\n" + String.join("\n", errors));
  }

  // QA-4

  @Test(description = "QA-4 Home page metadata")
  public void qa4_homeMetadata() {
    open("/");
    assertThat(page).hasTitle("Hirion, Swiss jobs matched to your CV, sent by email");
    assertEquals(page.evaluate("() => document.querySelector('meta[name=\"description\"]')?.content"),
          "Stop scrolling job boards. Hirion scans every Swiss source and delivers only the roles that match your profile.");
    assertEquals(page.evaluate("() => document.querySelector('link[rel=\"canonical\"]')?.href"),
          "https://hirion.ch/");
    assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setLevel(1))).hasCount(1);
  }

  @Test(description = "QA-4 robots.txt and sitemap.xml")
  public void qa4_robotsAndSitemap() {
    APIResponse robots = page.request().get(BASE_URL + "/robots.txt");
    assertEquals(robots.status(), 200, "/robots.txt");
    List<String> lines = robots.text().lines().map(String::trim).toList();
    assertTrue(lines.contains("Disallow: /dashboard"), "robots.txt should hide /dashboard:\n" + robots.text());
    assertTrue(lines.contains("Disallow: /settings"), "robots.txt should hide /settings:\n" + robots.text());
    assertEquals(page.request().get(BASE_URL + "/sitemap.xml").status(), 200, "/sitemap.xml");
  }
}
