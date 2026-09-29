package ch.hirion.pub;

import ch.hirion.support.BaseTest;

/**
 * Base of the public-page tests of add-public-coverage (guest context). The shared helpers open(),
 * reload(), h1(), field() and QUICK_ATTR moved to support/BaseTest in change add-user-journey, so the
 * signed-in journey tests (AuthenticatedTest) can use them too.
 */
public abstract class PublicPageTest extends BaseTest {
}
