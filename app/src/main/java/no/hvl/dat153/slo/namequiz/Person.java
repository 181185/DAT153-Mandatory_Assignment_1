package no.hvl.dat153.slo.namequiz;

import java.io.Serializable;

/**
 * A person in the context of this app is represented by a name and a photo (actually a file path
 * to the location where an image of the person is stored).
 */
public class Person implements Serializable {
    private String picturePath;
    private String name;

    public Person(String picturePath, String name) {
        this.picturePath = picturePath;
        this.name = name;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
