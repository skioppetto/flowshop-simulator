package com.flowshop.writer;

import com.flowshop.simulator.ISimulationTimer;
import com.flowshop.simulator.ObservableSimObject;
import com.flowshop.simulator.SimObjectObserver;

public class WorkstationListener implements SimObjectObserver {

   private ISimulationTimer timer;

   public WorkstationListener(ISimulationTimer timer) {
      this.timer = timer;
   }

   @Override
   public void onChange(ObservableSimObject observableSimObject) {
      // TODO Auto-generated method stub

   }

}
