package com.flowshop.simulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class ObservableSimObject {

   private final List<SimObjectObserver> observers = new ArrayList<SimObjectObserver>();
   private boolean runningNotification = false;
   private final Set<SimObjectObserver> toBeRemoved = new HashSet<>();

   public void addSimObjectObserver(SimObjectObserver observer) {
      this.observers.add(observer);
      observer.onAdded(this);
   }

   protected void notifySimObjectObservers() {
      runningNotification = true;
      Iterator<SimObjectObserver> observerIt = observers.iterator();
      while (observerIt.hasNext())
         observerIt.next().onChange(this);
      runningNotification = false;
      observers.removeAll(toBeRemoved);
      toBeRemoved.clear();

   }

   public void removeSimObjectObserver(SimObjectObserver observer) {
      if (runningNotification)
         toBeRemoved.add(observer);
      else
         this.observers.remove(observer);
   }

}
