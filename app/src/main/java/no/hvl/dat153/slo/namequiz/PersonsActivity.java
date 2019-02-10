package no.hvl.dat153.slo.namequiz;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.io.File;

/**
 * Activity class representing the overview (list) of people recorded within the app.
 */
public class PersonsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private File file;

    @VisibleForTesting
    public PersonsCollection personsCollection;
    private LocalStorageHelper localStorageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons);

        recyclerView = findViewById(R.id.names_recycler_view);
        recyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        file = new File(getFilesDir(), getString(R.string.persons_collection));
        localStorageHelper = new LocalStorageHelper(file);
        personsCollection = localStorageHelper.loadPersonsCollection();

        // Specify an adapter
        adapter = new PersonsAdapter(personsCollection, file);
        recyclerView.setAdapter(adapter);
    }

    /**
     * onClick-handler for the floating action button.
     *
     * @param view The view that fired the onClick-event
     */
    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, AddPersonActivity.class);
        startActivity(intent);
    }
}
