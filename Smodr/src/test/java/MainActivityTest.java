import android.widget.Toolbar;
import com.kevintcoughlin.smodr.BuildConfig;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.views.activities.MainActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@Config(constants = BuildConfig.class, sdk = 21)
@RunWith(RobolectricGradleTestRunner.class)
public final class MainActivityTest {

	private MainActivity activity;

	@Before
	public void setup() throws Exception {
		activity = Robolectric.setupActivity(MainActivity.class);
		assertNotNull("MainActivity is not instantiated", activity);
	}

	@Test
	public void validateTextViewContent() throws Exception {
		final Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
		assertNotNull("Toolbar could not be found", toolbar);
	}

}