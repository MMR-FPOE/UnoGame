package org.example.eiscuno.model.observer;

import java.util.HashSet;
import java.util.Set;

public class ThreadObservable implements Observable{
    Set<Observer> observers = new HashSet<>();

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void deleteObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer: observers){
            observer.update();
        }
    }
}
