package no.hvl.dat153.slo.namequiz;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class QuizActivityTest {

    private String correctGuessToBeTyped;
    private int initialScore;
    private int scoreToCheckAgainst;

    @Rule
    public ActivityTestRule<QuizActivity> quizActivityRule = new ActivityTestRule<>(QuizActivity.class);

    @Before
    public void init() {
        // Start the quiz
        onView(withId(R.id.quiz_toggle_quiz_button))
                .perform(click());

        correctGuessToBeTyped = quizActivityRule.getActivity().currentPerson.getName();
        initialScore = quizActivityRule.getActivity().currentScore;
    }

    @Test
    public void isCorrectScoreAfterCorrectGuess() {
        // Set the correct score to check against
        scoreToCheckAgainst = initialScore + 1;

        // Perform a guess, which we know is correct
        onView(withId(R.id.quiz_guess_input))
                .perform(typeText(correctGuessToBeTyped), closeSoftKeyboard());
        onView(withId(R.id.quiz_guess_button))
                .perform(click());

        // Check if score is correct after correct guess
        onView(withId(R.id.quiz_current_score_counter))
                .check(matches(withText(Integer.toString(scoreToCheckAgainst))));
    }

    @Test
    public void isCorrectScoreAfterWrongGuess() {
        // Set the correct score to check against
        scoreToCheckAgainst = initialScore;

        // Perform a guess, which we know is incorrect
        onView(withId(R.id.quiz_guess_input))
                .perform(typeText(correctGuessToBeTyped + "wrong"), closeSoftKeyboard());
        onView(withId(R.id.quiz_guess_button))
                .perform(click());

        // Check if score is correct (i.e. hasn't changed) after incorrect guess
        onView(withId(R.id.quiz_current_score_counter))
                .check(matches(withText(Integer.toString(scoreToCheckAgainst))));
    }
}