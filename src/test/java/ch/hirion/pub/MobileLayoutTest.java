package ch.hirion.pub;

import com.microsoft.playwright.Browser;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/** Group QA (phone, 375 x 812): specs/access-and-quality QA-1. QA-2 removed (D3 not a defect, 2026-10-05). */
public class MobileLayoutTest extends PublicPageTest {

  @Override
  protected Browser.NewContextOptions contextOptions() {
    return super.contextOptions().setViewportSize(375, 812).setIsMobile(true).setHasTouch(true);
  }

  // QA-1

  @Test(description = "QA-1 No horizontal scroll at 375 px")
  public void qa1_noHorizontalScroll() {
    open("/");
    int scrollWidth = ((Number) page.evaluate("() => document.documentElement.scrollWidth")).intValue();
    assertTrue(scrollWidth <= 375, "scrollWidth " + scrollWidth + " > 375");
  }
}
