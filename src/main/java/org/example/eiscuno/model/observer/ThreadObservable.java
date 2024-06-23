package org.example.eiscuno.model.observer;

import java.util.HashSet;
import java.util.Set;

/**
 * ThreadObservable Class that implements Observable
 */
public class ThreadObservable implements Observable{
    /**
     * The observers set
     */
    Set<Observer> observers = new HashSet<>();

    /**
     * Adds the observable to the observers set
     */
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    /**
     * Delete the observable from the observers set
     */
    @Override
    public void deleteObserver(Observer o) {
        observers.remove(o);
    }

    /**
     * notify subscribers
     */
    @Override
    public void notifyObservers() {
        for (Observer observer: observers){
            observer.update();
        }
    }
}
