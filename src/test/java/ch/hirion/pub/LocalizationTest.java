package ch.hirion.pub;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.opentest4j.AssertionFailedError;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

/** Group I18N: language switcher, openspec/changes/add-public-coverage/specs/localization. */
public class LocalizationTest extends PublicPageTest {

  private static final String H1_EN = "Your AI job agent for Switzerland.";
  private static final String H1_DE = "Dein KI-Job-Agent für die Schweiz.";
  private static final String H1_FR = "Votre agent IA d'emploi pour la Suisse.";
  private static final String H1_IT = "Il tuo agente IA per il lavoro in Svizzera.";
  private static final String H1_CONTACT_DE = "Sprich mit dem Team.";

  /** Opens the language menu from "EN" and picks the given language; waits until the button shows it. */
  private void switchTo(String code) {
    role(AriaRole.BUTTON, "EN").click();
    role(AriaRole.MENUITEM, code).click();
    assertThat(role(AriaRole.BUTTON, code)).isVisible();
  }

  /** A link in the header; "Loslegen" also appears in a page section, so the scope matters. */
  private Locator headerLink(String name) {
    return page.getByRole(AriaRole.BANNER)
          .getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(name).setExact(true));
  }

  // I18N-1

  @Test(description = "I18N-1 Default language is English")
  public void i18n1_defaultIsEnglish() {
    open("/");
    assertThat(role(AriaRole.BUTTON, "EN")).isVisible();
    assertThat(h1(H1_EN)).isVisible();
  }

  @Test(description = "I18N-1 Language menu lists four languages")
  public void i18n1_menuListsFourLanguages() {
    open("/");
    role(AriaRole.BUTTON, "EN").click();
    assertThat(page.getByRole(AriaRole.MENU)).isVisible();
    for (String code : new String[] {"EN", "FR", "DE", "IT"}) {
      assertThat(role(AriaRole.MENUITEM, code)).isVisible();
    }
  }

  // I18N-2

  @Test(description = "I18N-2 Switch to German")
  public void i18n2_switchToGerman() {
    open("/");
    switchTo("DE");
    assertThat(h1(H1_DE)).isVisible();
    assertThat(headerLink("Loslegen")).hasAttribute("href", "/signup");
    assertEquals(page.evaluate("() => localStorage.getItem('hirion.lang')"), "de");
  }

  // I18N-3

  @Test(description = "I18N-3 German survives a reload")
  public void i18n3_germanSurvivesReload() {
    open("/");
    switchTo("DE");
    reload();
    assertThat(role(AriaRole.BUTTON, "DE")).isVisible();
    assertThat(h1(H1_DE)).isVisible();
  }

  @Test(description = "I18N-3 German applies on another page")
  public void i18n3_germanOnAnotherPage() {
    open("/");
    switchTo("DE");
    open("/contact");
    assertThat(role(AriaRole.BUTTON, "DE")).isVisible();
    assertThat(h1(H1_CONTACT_DE)).isVisible();
  }

  // I18N-4 (known defect D2)

  @Test(description = "I18N-4 Default document language")
  public void i18n4_defaultHtmlLang() {
    open("/");
    assertThat(page.locator("html")).hasAttribute("lang", "en"); // locator-exception: <html> lang attribute
  }

  @Test(expectedExceptions = AssertionFailedError.class,
        description = "I18N-4 Document language after switching to German -- KNOWN BUG D2: <html lang> stays en")
  public void i18n4_htmlLangAfterGerman() {
    open("/");
    switchTo("DE");
    assertThat(page.locator("html")).hasAttribute("lang", "de", QUICK_ATTR); // locator-exception: <html> lang attribute
  }

  // I18N-5

  @Test(description = "I18N-5 Switch to French")
  public void i18n5_switchToFrench() {
    open("/");
    switchTo("FR");
    assertThat(h1(H1_FR)).isVisible();
  }

  @Test(description = "I18N-5 Switch to Italian")
  public void i18n5_switchToItalian() {
    open("/");
    switchTo("IT");
    assertThat(h1(H1_IT)).isVisible();
  }
}
