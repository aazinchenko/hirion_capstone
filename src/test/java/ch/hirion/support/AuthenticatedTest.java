package ch.hirion.support;
import com.microsoft.playwright.Browser;
import java.nio.file.Paths;
/** Тесты личного кабинета: стартуют уже залогиненными (сессия из RegistrationTest). */
public abstract class AuthenticatedTest extends BaseTest {
  @Override
  protected Browser.NewContextOptions contextOptions() {
    return super.contextOptions().setStorageStatePath(Paths.get(".auth/user.json"));
  }
}
