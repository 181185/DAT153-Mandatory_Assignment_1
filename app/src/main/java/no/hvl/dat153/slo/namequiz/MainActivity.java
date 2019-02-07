package no.hvl.dat153.slo.namequiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

/**
 * Activity class representing the main activity (the "home screen").
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolveAppUsername();
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

    private void resolveAppUsername() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isAlreadySetUsername = sp.contains(getString(R.string.settings_username_key));

        if(isAlreadySetUsername) {
            return;
        } else {
            createSetUsernameDialog();
        }
    }

    /**
     * Create a dialog for adding a username if one does not already exist in SharedPreferences.
     */
    private void createSetUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = View.inflate(this, R.layout.username_dialog, null);


        builder.setTitle(getString(R.string.set_username_dialog_title));
        builder.setView(view);
        builder.setPositiveButton(R.string.set_username_positive_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        EditText usernameInput = view.findViewById(R.id.username_dialog_input);
                        String username = usernameInput.getText().toString();
                        updateSharedPreferencesUsername(username);
                        dialog.dismiss();
                        break;
                    }
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void updateSharedPreferencesUsername(String username) {
        SharedPreferences.Editor spEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        spEditor.putString(getString(R.string.settings_username_key), username).commit();
    }
}
