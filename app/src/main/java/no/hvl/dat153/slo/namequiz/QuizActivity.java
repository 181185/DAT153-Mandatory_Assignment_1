package no.hvl.dat153.slo.namequiz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class QuizActivity extends BaseActivity {
    private int currentScore;
    private int bestScore;

    private TextView bestScoreCounter;
    private TextView currentScoreCounter;
    private TextView currentScoreInfo;
    private TextView guessInfo;
    private ImageView currPersonImage;
    private ViewSwitcher quizViewSwitcher;
    private EditText guessInput;
    private Button submitGuessButton;
    private Button nextPersonButton;

    private File file;
    private Person currentPerson;
    private PersonsCollection personsCollection;
    private LocalStorageHelper localStorageHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        String best_score_identifier = getString(R.string.quiz_preferences_best_score);
        bestScore = sharedPreferences.getInt(best_score_identifier, 0);

        bestScoreCounter = findViewById(R.id.quiz_best_score_counter);
        bestScoreCounter.setText(Integer.toString(bestScore));

        currPersonImage = findViewById(R.id.quiz_image);
        file = new File(getFilesDir(), getString(R.string.persons_collection));
        localStorageHelper = new LocalStorageHelper(file);
    }

    /**
     * Prepare for a new quiz-session by initializing views and setting default states.
     */
    private void prepareQuiz() {
        quizViewSwitcher = findViewById(R.id.quiz_view_switcher);
        quizViewSwitcher.setVisibility(View.VISIBLE);
        guessInput = findViewById(R.id.quiz_guess_input);
        guessInfo = findViewById(R.id.quiz_guess_info);
        submitGuessButton = findViewById(R.id.quiz_guess_button);
        nextPersonButton = findViewById(R.id.quiz_next_button);
        submitGuessButton.setVisibility(View.VISIBLE);
        nextPersonButton.setVisibility(View.VISIBLE);
    }

    /**
     * Start a new quiz-session.
     */
    private void startQuiz() {
        currentScore = 0;
        currentScoreInfo = findViewById(R.id.quiz_current_score_info);
        currentScoreCounter = findViewById(R.id.quiz_current_score_counter);
        currentScoreInfo.setVisibility(View.VISIBLE);
        currentScoreCounter.setVisibility(View.VISIBLE);
        currentScoreCounter.setText(Integer.toString(currentScore));
        generateNewQuestion();
    }

    /**
     * End a quiz-session, update and save the user score and reset the states of the views.
     */
    private void stopQuiz() {
        SharedPreferences.Editor spe = sharedPreferences.edit();
        spe.putInt(getString(R.string.quiz_preferences_best_score), bestScore);
        spe.apply();

        currPersonImage.setImageResource(R.drawable.ic_person_outline_black_24dp);
        quizViewSwitcher.setVisibility(View.INVISIBLE);
        currentScoreInfo.setVisibility(View.INVISIBLE);
        currentScoreCounter.setVisibility(View.INVISIBLE);
        submitGuessButton.setVisibility(View.INVISIBLE);
        nextPersonButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Generates a new question for the quiz by fetching a new Person-object from the
     * PersonCollection.
     */
    private void generateNewQuestion() {
        Random rnd;

        rnd = new Random();
        int randomPersonIndex = rnd.nextInt(personsCollection.getSize());
        Person newPerson = personsCollection.getPerson(randomPersonIndex);

        while(newPerson.equals(currentPerson) && personsCollection.getSize() > 1) {
            randomPersonIndex = rnd.nextInt(personsCollection.getSize());
            newPerson = personsCollection.getPerson(randomPersonIndex);
        }

        currentPerson = newPerson;
        if(URLUtil.isContentUrl(currentPerson.getPicturePath()))
            setPictureFromGallery(currPersonImage, currentPerson.getPicturePath());
        else
            setPicture(currPersonImage, currentPerson.getPicturePath());
    }

    /**
     * Make a guess by comparing the user input (input text field) to the actual name of the Person
     * currently being displayed in the active quiz-session.
     */
    private void makeGuess() {
        hideSoftKeyboard();

        String trimmedInput = guessInput.getText().toString().trim();
        boolean isRightGuess = currentPerson.getName().equals(trimmedInput);

        submitGuessButton.setEnabled(false);
        guessInput.setText("");

        if(isRightGuess) {
            guessInfo.setText(getString(R.string.quiz_correct_guess_text));
            quizViewSwitcher.showNext();
            currentScore++;
            currentScoreCounter.setText(Integer.toString(currentScore));

            if(currentScore > bestScore) {
                bestScore++;
                bestScoreCounter.setText(Integer.toString(bestScore));
            }

        } else {
            guessInfo.setText(getString(R.string.quiz_wrong_guess_text) + " " + currentPerson.getName());
            quizViewSwitcher.showNext();
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(guessInput.getWindowToken(), 0);
    }

    /**
     * onToggle-handler for the "Toggle Quiz" button.
     *
     * @param view The view that fired the onClick-event
     */
    public void onToggleQuizButtonClick(View view) {
        personsCollection = localStorageHelper.loadPersonsCollection();
        boolean isEmptyCollection = personsCollection.getSize() <= 0;
        if(isEmptyCollection) {
            ((ToggleButton) findViewById(R.id.quiz_toggle_quiz_button)).toggle();
            Toast.makeText(this, getString(R.string.quiz_toast_message), Toast.LENGTH_LONG).show();
        } else {
            prepareQuiz();
            boolean isStartedState = ((ToggleButton) view).isChecked();

            if(isStartedState)
                startQuiz();
            else
                stopQuiz();
        }
    }

    /**
     * onClick-handler for the "Next person" button.
     *
     * @param view The view that fired the onClick-event
     */
    public void onNextButtonClick(View view) {
        generateNewQuestion();
        submitGuessButton.setEnabled(true);

        if(quizViewSwitcher.getNextView().equals(guessInput))
            quizViewSwitcher.showNext();
    }

    /**
     * onClick-handler for the "Guess" button.
     *
     * @param view The view that fired the onClick-event
     */
    public void onGuessButtonClick(View view) {
        makeGuess();
    }

    /**
     * TODO: Duplicate method.
     */
    private void setPicture(ImageView imageView, String picurePath) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = Integer.parseInt(getString(R.string.scale_factor));

        Bitmap bitmap = BitmapFactory.decodeFile(picurePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private void setPictureFromGallery(ImageView imageView, String stringUri) {
        Uri uri = Uri.parse(stringUri);
        try (InputStream inputStreamImage = getContentResolver().openInputStream(uri)) {

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = Integer.parseInt(getString(R.string.scale_factor));

            Bitmap bitmap = BitmapFactory.decodeStream(inputStreamImage, null, bmOptions);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e("setPictureFromGallery", "Could not get file from uri.");
        } catch (IOException e) {
            Log.e("setPictureFromGallery", "IOException.");
        }
    }
}
