package no.hvl.dat153.slo.namequiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Activity class representing the overview (list) of people recorded within the app.
 */
public class PersonsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private File file;
    private PersonsCollection personsCollection;
    private StorageHelper storageHelper;

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
        storageHelper = new StorageHelper(file);
        personsCollection = storageHelper.loadPersonsCollection();

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
