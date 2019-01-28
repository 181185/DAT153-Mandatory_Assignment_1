package no.hvl.dat153.slo.namequiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Activity class representing the main activity (the "home screen").
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * onClick-handler for the "Names"-button.
     *
     * @param view The view that fired the onClick-event
     */
    public void onNamesButtonClick(View view) {
        Intent intent = new Intent(this, PersonsActivity.class);
        startActivity(intent);
    }

    /**
     * onClick-handler for the "Quiz"-button.
     *
     * @param view The view that fired the onClick-event
     */
    public void onQuizButtonClick(View view) {
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

    /**
     * onClick-handler for the "Exit"-button.
     *
     * @param view The view that fired the onClick-event
     */
    public void onExitButtonClick(View view) {
        finishAndRemoveTask();
    }
}
