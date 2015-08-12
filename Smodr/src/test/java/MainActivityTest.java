import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdView;
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

/**
 * Tests for {@link MainActivity}.
 *
 * @author kevincoughlin
 */
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
	public void validateViews() throws Exception {
		final FrameLayout container = (FrameLayout) activity.findViewById(R.id.channels_container);
		final Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
		final AdView adView = (AdView) activity.findViewById(R.id.ad);

		assertNotNull("Container could not be found", container);
		assertNotNull("Toolbar could not be found", toolbar);
		assertNotNull("AdView could not be found", adView);
	}
}