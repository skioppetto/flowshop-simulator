package com.flowshop;

import java.util.ArrayList;
import java.util.List;

public abstract class ObservableSimObject {

   private final List<SimObjectObserver> observers = new ArrayList<SimObjectObserver>();

   public void addSimObjectObserver(SimObjectObserver observer) {
      this.observers.add(observer);
   }

   public void notifySimObjectObservers() {
      for (SimObjectObserver observer : observers)
         observer.onChange(this);
   }

   public void removeSimObjectObserver(SimObjectObserver observer) {
      this.observers.remove(observer);
   }

}
