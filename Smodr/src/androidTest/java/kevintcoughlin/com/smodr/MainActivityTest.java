package kevintcoughlin.com.smodr;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.views.activities.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

	@Rule
	public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
			MainActivity.class);

	@Test
	public void layout_viewsExist() {
		onView(withId(R.id.list)).check(matches(isDisplayed()));
	}
}
