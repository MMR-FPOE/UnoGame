package org.example.eiscuno.model.observer;

/**
 * Observable interface
 */
public interface Observable {
    /**
     * Adds the observable to the observers set
     */
    void addObserver(Observer o);
    /**
     * Delete the observable from the observers set
     */
    void deleteObserver(Observer o);
    /**
     * notify subscribers
     */
    void notifyObservers();
}
