package no.hvl.dat153.slo.namequiz;

import android.content.Context;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PersonsActivityTest {

    private int initialSize;

    @Rule
    public ActivityTestRule<PersonsActivity> personsActivityRule = new ActivityTestRule<>(PersonsActivity.class);

    @Before
    public void init() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        personsActivityRule.getActivity().personsCollection.add(ctx.getString(R.string.no_image_path_name), "Garfield");
        initialSize = personsActivityRule.getActivity().personsCollection.getSize();
    }

    @Test
    public void isCorrectPersonsCollectionSizeAfterDeletingPerson() {
        ViewInteraction vi = onView(withId(R.id.names_recycler_view));
        vi.perform(RecyclerViewActions.actionOnItemAtPosition(0, RecyclerViewAction.clickDeleteButton()));

        assertEquals(personsActivityRule.getActivity().personsCollection.getSize(), initialSize - 1);
    }


    public static class RecyclerViewAction {

        public static ViewAction clickDeleteButton() {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a delete button within a given view.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(R.string.delete_button_id);
                    v.performClick();
                }
            };
        }

    }
}
