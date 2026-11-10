package ch.hirion.support;

import com.microsoft.playwright.Browser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests of the owner-granted Premium account (PREM, change use-existing-premium-account): they start signed in with
 * the session that PremiumSignInTest.prem15 saved. Credentials come only from the environment, never from -D
 * properties (those would show in the command line and the Maven log), and are never printed.
 */
public abstract class PremiumAccountTest extends BaseTest {

  public static final String EMAIL_ENV = "QA_PREMIUM_EMAIL";
  public static final String PASSWORD_ENV = "QA_PREMIUM_PASSWORD";
  /** Session of the Premium account; .auth/ is git-ignored and the file is deleted after the suite. */
  public static final Path SESSION = Paths.get(".auth/premium.json");

  /** Without a saved session (before prem15, or when it failed) the context starts signed out. */
  @Override
  protected Browser.NewContextOptions contextOptions() {
    Browser.NewContextOptions options = super.contextOptions();
    return Files.exists(SESSION) ? options.setStorageStatePath(SESSION) : options;
  }
}
