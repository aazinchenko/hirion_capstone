package ch.hirion.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.SkipException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The one disposable user of a journey run (change add-user-journey, design.md "Test user state").
 * Stored in the git-ignored .auth/test-user.json BEFORE the account exists, so an interrupted run can
 * always be cleaned up. Public fields, no getters: Jackson writes them as they are.
 */
public class TestUser {

  /** Every generated email matches this; JourneyCleanup refuses to delete anything else. */
  public static final Pattern QA_EMAIL = Pattern.compile("\\+hirion-qa-\\d+@");
  public static final Path STATE = Paths.get(".auth/test-user.json");
  private static final ObjectMapper JSON = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private static final SecureRandom RANDOM = new SecureRandom();

  public String email;
  public String firstName = "Qa";
  public String currentPassword;
  /** Set before "Update password" (SET-5); promoted to currentPassword once it signs in. */
  public String pendingPassword;
  /** true from the moment "See my matches" is clicked: an account may exist even if the run dies next. */
  public boolean submitted;
  /** false until /dashboard is reached after "See my matches". */
  public boolean accountCreated;

  /** Builds a new user from -Dqa.mailbox; skips the journey when the mailbox is not given. */
  public static TestUser generate() {
    String mailbox = System.getProperty("qa.mailbox", "").trim();
    if (mailbox.isEmpty()) {
      throw new SkipException("-Dqa.mailbox=<gmail local part> is required for the journey; nothing was created");
    }
    TestUser user = new TestUser();
    user.email = mailbox + "+hirion-qa-" + System.currentTimeMillis() + "@gmail.com";
    user.currentPassword = randomPassword();
    if (!QA_EMAIL.matcher(user.email).find()) {
      throw new IllegalStateException("generated email does not match the QA pattern: " + user.email);
    }
    return user;
  }

  /** 16 characters with upper, lower, digit and symbol, so any password rule of the site is met. */
  static String randomPassword() {
    String upper = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    String lower = "abcdefghijkmnopqrstuvwxyz";
    String digits = "23456789";
    String symbols = "!-_";
    String all = upper + lower + digits + symbols;
    StringBuilder sb = new StringBuilder("Qa");
    sb.append(upper.charAt(RANDOM.nextInt(upper.length())));
    sb.append(lower.charAt(RANDOM.nextInt(lower.length())));
    sb.append(digits.charAt(RANDOM.nextInt(digits.length())));
    sb.append(symbols.charAt(RANDOM.nextInt(symbols.length())));
    while (sb.length() < 16) {
      sb.append(all.charAt(RANDOM.nextInt(all.length())));
    }
    return sb.toString();
  }

  public void save() {
    try {
      Files.createDirectories(STATE.getParent());
      JSON.writerWithDefaultPrettyPrinter().writeValue(STATE.toFile(), this);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Optional<TestUser> load() {
    if (!Files.exists(STATE)) {
      return Optional.empty();
    }
    try {
      return Optional.of(JSON.readValue(STATE.toFile(), TestUser.class));
    } catch (IOException e) {
      throw new UncheckedIOException("cannot read " + STATE, e);
    }
  }

  public static void deleteState() {
    try {
      Files.deleteIfExists(STATE);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
