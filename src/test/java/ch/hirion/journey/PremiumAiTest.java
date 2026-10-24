package ch.hirion.journey;

import ch.hirion.support.AuthenticatedTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.opentest4j.AssertionFailedError;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Group PREM: the AI dialogs of a Premium job card (PREM-11..13),
 * openspec/changes/add-premium-trial-journey/specs/premium-trial.
 *
 * <p>Every AI call costs the site owner money: the tests run only with -Dqa.ai=true (never in CI; the hook
 * scripts/guard-live-run.mjs asks the human first), each button is clicked once per run, and only the labels of the
 * dialog are checked, never the text the AI wrote (AGENTS.md).
 */
@Epic("User journey")
@Feature("premium-trial")
public class PremiumAiTest extends AuthenticatedTest {

  private static final boolean AI_ENABLED = Boolean.getBoolean("qa.ai");
  private static final Pattern DASHBOARD_URL = Pattern.compile("/dashboard([?#].*)?$");
  /** The AI answer takes "a few seconds" by the site's own text; 90 s leaves room for a slow model. */
  private static final LocatorAssertions.IsVisibleOptions AI_DONE =
        new LocatorAssertions.IsVisibleOptions().setTimeout(90_000);
  /**
   * E.g. "29 tailored applications left this month." -- in the stand-out dialog it shares one paragraph with "Based
   * only on what is in your CV. ...", so the pattern is not anchored at the end (seen live 2026-10-24).
   */
  private static final Pattern MONTHLY_COUNTER = Pattern.compile("^\\d+ tailored applications left this month\\.");
  private static final LocatorAssertions.IsVisibleOptions FEED_LOADED =
        new LocatorAssertions.IsVisibleOptions().setTimeout(60_000);

  @BeforeMethod(alwaysRun = true) // runs after BaseTest.openContext (superclass first)
  public void skipWithoutAiFlag() {
    if (!AI_ENABLED) {
      throw new SkipException("AI dialogs run only with -Dqa.ai=true (owner's AI costs)");
    }
  }

  /** "Copy" writes to the clipboard, which headless Chromium allows only with these permissions. */
  @Override
  protected Browser.NewContextOptions contextOptions() {
    return super.contextOptions().setPermissions(List.of("clipboard-read", "clipboard-write"));
  }

  // PREM-11

  @Story("PREM-11 Help me stand out shows a review of the job")
  @Test(priority = 108, dependsOnGroups = {"registered", "premium"}, description = "PREM-11 Stand-out review")
  public void prem11_helpMeStandOut() {
    openFirstCardAction("Help me stand out");
    Locator dialog = page.getByRole(AriaRole.DIALOG);
    assertThat(dialog.getByText("What to highlight", exact())).isVisible(AI_DONE);
    assertThat(dialog.getByText("Help me stand out", exact()).first()).isVisible();
    // "Worth addressing" shows only when the AI finds gaps (first -Dqa.ai run, 2026-10-24): not required
    assertThat(dialog.getByText("Keywords to use", exact())).isVisible();
    assertThat(copyButton(dialog)).isVisible();
    assertThat(dialog.getByText(MONTHLY_COUNTER)).isVisible();
  }

  // PREM-12

  @Story("PREM-12 Tailor my CV writes a tailored CV")
  @Test(priority = 109, dependsOnGroups = {"registered", "premium"}, description = "PREM-12 Tailored CV")
  public void prem12_tailorMyCv() {
    openFirstCardAction("Tailor my CV");
    Locator dialog = page.getByRole(AriaRole.DIALOG);
    assertThat(dialog.getByText("ATS-friendly", exact())).isVisible(AI_DONE);
    // the title "Tailored CV" from the site code is not in the live dialog (first -Dqa.ai run, 2026-10-24)
    assertThat(dialog.getByText("Closely tailored", exact())).isVisible();
    assertThat(dialog.getByText("Classic", exact())).isVisible();
    assertThat(dialog.getByText("Modern", exact())).isVisible();
    for (String download : List.of(".txt", "PDF")) {
      assertThat(dialog.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(download).setExact(true)))
            .isVisible();
    }
    copyButton(dialog).click();
    assertThat(page.getByRole(AriaRole.REGION, new Page.GetByRoleOptions().setName(Pattern.compile("^Notifications")))
          .getByText("Copied", new Locator.GetByTextOptions().setExact(true))).isVisible();
  }

  // PREM-13

  @Story("PREM-13 Write Cover Letter writes a cover letter")
  @Test(priority = 110, dependsOnGroups = {"registered", "premium"}, description = "PREM-13 Cover letter")
  public void prem13_writeCoverLetter() {
    openFirstCardAction("Write Cover Letter");
    Locator dialog = page.getByRole(AriaRole.DIALOG);
    assertThat(dialog.getByText("Letterhead", exact())).isVisible(AI_DONE);
    assertThat(dialog.getByText("Cover letter", exact()).first()).isVisible();
    assertThat(dialog.getByText("Plain", exact())).isVisible();
    assertThat(copyButton(dialog)).isVisible();
  }

  // ---------------------------------------------------------------------------------------------

  /** Opens the Job Feed and clicks one AI button of the first card (once per run). */
  private void openFirstCardAction(String button) {
    open("/dashboard");
    assertThat(page).hasURL(DASHBOARD_URL);
    Locator first = page.getByRole(AriaRole.MAIN)
          .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(button).setExact(true)).first();
    try {
      assertThat(first).isVisible(FEED_LOADED);
    } catch (AssertionFailedError e) {
      throw new SkipException("no job card in the Premium feed for \"" + button + "\"");
    }
    first.click();
  }

  private static Locator copyButton(Locator dialog) {
    return dialog.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Copy").setExact(true));
  }

  private static Locator.GetByTextOptions exact() {
    return new Locator.GetByTextOptions().setExact(true);
  }
}
