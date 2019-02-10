package no.hvl.dat153.slo.namequiz;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.OutputStream;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.intent.IntentCallback;
import androidx.test.runner.intent.IntentMonitorRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddPersonActivityTest {

    private int initialSize;
    private final String dummyName = "Garfield";

    @Rule
    public ActivityTestRule<AddPersonActivity> addPersonActivityRule = new ActivityTestRule<>(AddPersonActivity.class);

    @Test
    public void isCorrectPersonsCollectionSizeAfterAddingPerson() {
        Intents.init();
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(
                new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        // Since we're adding MediaStore.EXTRA_OUTPUT specifying we want the picture stored at a
        // specific Uri/file-path and we're using FileProvider in the implementation of AddPersonActivity,
        // we have to implement an intentCallback when stubbing the camera.
        IntentCallback intentCallback = new IntentCallback() {
            @Override
            public void onIntentSent(Intent intent) {
                if (intent.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE)) {
                    try {
                        Uri imageUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                        Context context = ApplicationProvider.getApplicationContext();

                        Bitmap icon = BitmapFactory.decodeResource(
                                context.getResources(),
                                R.drawable.cat1);
                        OutputStream out = context.getContentResolver().openOutputStream(imageUri);
                        icon.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        IntentMonitorRegistry.getInstance().addIntentCallback(intentCallback);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
        onView(withId(R.id.add_person_image)).perform(click());
        onView(withText(R.string.add_person_dialog_camera_option)).perform(click());

        IntentMonitorRegistry.getInstance().removeIntentCallback(intentCallback);

        onView(withId(R.id.add_person_name))
                .perform(typeText(dummyName), closeSoftKeyboard());

        initialSize = addPersonActivityRule.getActivity().personsCollection.getSize();

        onView(withId(R.id.add_person_button)).perform(click());

        assertEquals(addPersonActivityRule.getActivity().personsCollection.getSize(), initialSize + 1);
    }
}
