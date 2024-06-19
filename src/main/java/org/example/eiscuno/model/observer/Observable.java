package org.example.eiscuno.model.observer;

public interface Observable {
    void addObserver(Observer o);
    void deleteObserver(Observer o);
    void notifyObservers();
}
