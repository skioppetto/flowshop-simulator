package com.flowshop.writer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.flowshop.simulator.ISimulationTimer;
import com.flowshop.simulator.ObservableSimObject;
import com.flowshop.simulator.SimObjectObserver;
import com.flowshop.simulator.Workstation;

public class WorkstationListener implements SimObjectObserver {

   private ISimulationTimer timer;

   private final Queue<WorkstationEvent> queue = new LinkedList<>();
   private final Map<Workstation, WorkstationEvent> runningEvents = new HashMap<>();

   public WorkstationListener(ISimulationTimer timer) {
      this.timer = timer;
   }

   @Override
   public void onAdded(ObservableSimObject observableSimObject) {
      if (observableSimObject instanceof Workstation) {
         Workstation workstation = (Workstation) observableSimObject;
         WorkstationEvent running = new WorkstationEvent();
         running.setStartTime(timer.getSimulationTime());
         running.setStatus(workstation.getStatus());
         running.setWorkstationId(workstation.getId());
         runningEvents.put(workstation, running);
      }

   }

   @Override
   public void onChange(ObservableSimObject observableSimObject) {
      if (observableSimObject instanceof Workstation) {
         Workstation workstation = (Workstation) observableSimObject;
         WorkstationEvent running = runningEvents.get(observableSimObject);
         if (!workstation.getStatus().equals(running.getStatus())) {
            long simulationTime = timer.getSimulationTime();
            long duration = simulationTime - running.getStartTime();
            if (duration > 0) {
               running.setDuration(duration);
               queue.add(running);
            }
            WorkstationEvent next = new WorkstationEvent();
            next.setWorkstationId(workstation.getId());
            next.setStatus(workstation.getStatus());
            next.setStartTime(simulationTime);
            runningEvents.put(workstation, next);
         }
      }

   }

   public WorkstationEvent dequeue() {
      return queue.poll();
   }

}
