package kevintcoughlin.com.smodr;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.views.activities.DetailActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailActivityTest {
	@Rule
	public ActivityTestRule<DetailActivity> mActivityRule = new ActivityTestRule<>(
			DetailActivity.class);

	@Test
	public void layout_viewsExist() {
		onView(withId(R.id.main_content)).check(matches(isDisplayed()));
		onView(withId(R.id.appbar)).check(matches(isDisplayed()));
		onView(withId(R.id.collapsing_toolbar)).check(matches(isDisplayed()));
		onView(withId(R.id.backdrop)).check(matches(isDisplayed()));
		onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
		onView(withId(R.id.list)).check(matches(isDisplayed()));
	}
}
