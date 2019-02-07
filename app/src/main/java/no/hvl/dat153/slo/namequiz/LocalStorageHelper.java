package no.hvl.dat153.slo.namequiz;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Helper class for persistent storage of PersonCollection objects, implemented using serialization.
 */
public class LocalStorageHelper implements IStorageHelper {
    private File file;

    public LocalStorageHelper(File file) { this.file = file; }

    /**
     * Load a serialized PersonCollection object from persistent storage.
     *
     * @return A PersonCollection object if successfully read from storage, a new PersonCollection
     *  object else.
     */
    public PersonsCollection loadPersonsCollection() {
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                return (PersonsCollection) ois.readObject();
            } catch (FileNotFoundException e) {
                Log.e("loadPersonsCollection", "The file could not be found.");
            } catch (IOException e) {
                Log.e("loadPersonsCollection", "Object could not be read from FileInputStream.");
            } catch (ClassNotFoundException e) {
                Log.e("loadPersonsCollection", "Class could not be correctly deserialized.");
            }
        }
        return new PersonsCollection();
    }

    /**
     * Save a PersonCollection object by serializing it and writing it to storage.
     *
     * @param collection The PersonCollection we want to save
     */
    public void savePersonsCollection(PersonsCollection collection) {
        try (FileOutputStream fos = new FileOutputStream(file, false);
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(collection);
        } catch (FileNotFoundException e) {
            Log.e("loadPersonsCollection", "The file could not be found.");
        } catch (IOException e) {
            Log.e("loadPersonsCollection", "Object could not be written to FileInputStream.");
        }
    }
}
