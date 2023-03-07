package com.flowshop.simulator;

public interface SimObjectObserver {

   public void onChange(ObservableSimObject observableSimObject);

   public void onAdded(ObservableSimObject observableSimObject);

   public void onStopSimulation();

}
