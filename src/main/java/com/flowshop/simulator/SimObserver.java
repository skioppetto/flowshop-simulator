package com.flowshop.simulator;

public interface SimObserver {

   public void onStopSimulation(Simulation sim);

   public void onStartSimulation(Simulation sim);

   public void onProcessEnd(Simulation sim);

   public void onProcessStart(Simulation sim);
}
