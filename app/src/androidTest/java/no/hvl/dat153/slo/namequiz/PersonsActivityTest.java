package no.hvl.dat153.slo.namequiz;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PersonsActivityTest {

    private int initialSize;

    @Rule
    public ActivityTestRule<PersonsActivity> personsActivityRule = new ActivityTestRule<>(PersonsActivity.class);

    @Test
    public void isCorrectPersonsCollectionSizeAfterDeletingPerson() {
        initialSize = personsActivityRule.getActivity().personsCollection.getSize();

        onView(withText(R.string.delete_button_text)).perform(click());

        assertEquals(personsActivityRule.getActivity().personsCollection.getSize(), initialSize - 1);
    }
}
