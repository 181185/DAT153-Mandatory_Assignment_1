package no.hvl.dat153.slo.namequiz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class for a collection of Person objects.
 */
public class PersonsCollection implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Person> collection;

    public PersonsCollection() {
        collection = new ArrayList<>();
    }

    public void add(String image_path, String name) {
        Person p = new Person(image_path, name);
        collection.add(p);
    }

    public void remove(int index) { collection.remove(index); }

    public String getName(int position) {
        String res = "Test";
        if(position < collection.size())
            res = collection.get(position).getName();
        return res;
    }

    public Person getPerson(int index) { return collection.get(index); }

    public String getPicturePath(int position) {
        return collection.get(position).getPicturePath();
    }

    public int getSize() {
        return collection.size();
    }
}