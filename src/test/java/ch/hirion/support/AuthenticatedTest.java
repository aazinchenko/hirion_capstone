package ch.hirion.support;
import com.microsoft.playwright.Browser;
import java.nio.file.Paths;
/** Tests of the signed-in area: they start already signed in (session saved by RegistrationTest). */
public abstract class AuthenticatedTest extends BaseTest {
  @Override
  protected Browser.NewContextOptions contextOptions() {
    return super.contextOptions().setStorageStatePath(Paths.get(".auth/user.json"));
  }
}
