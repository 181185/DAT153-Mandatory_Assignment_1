package no.hvl.dat153.slo.namequiz;

public interface IStorageHelper {
    PersonsCollection loadPersonsCollection();

    void savePersonsCollection(PersonsCollection collection);
}
