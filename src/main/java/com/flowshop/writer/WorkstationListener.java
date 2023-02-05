package com.flowshop.writer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.flowshop.simulator.BufferedWorkstation;
import com.flowshop.simulator.ISimulationTimer;
import com.flowshop.simulator.ObservableSimObject;
import com.flowshop.simulator.SimObjectObserver;
import com.flowshop.simulator.WorkCell;
import com.flowshop.simulator.WorkGroup;
import com.flowshop.simulator.Workstation;

public class WorkstationListener implements SimObjectObserver {

   private ISimulationTimer timer;

   private final Queue<WorkstationEvent> queue = new LinkedList<>();
   private final Map<Workstation, WorkstationEvent> runningEvents = new HashMap<>();

   public WorkstationListener(ISimulationTimer timer) {
      this.timer = timer;
   }

   private void initWorkCell(WorkCell workstation, long simulationTime) {
      WorkstationEvent running = new WorkstationEvent();
      running.setStartTime(simulationTime);
      running.setStatus(workstation.getStatus());
      running.setWorkstationId(workstation.getId());
      if (workstation.getWorkGroup() != null)
         running.setWorkGroupId(workstation.getWorkGroup().getId());
      runningEvents.put(workstation, running);
   }

   @Override
   public void onAdded(ObservableSimObject observableSimObject) {
      Workstation workstation = null;
      if (observableSimObject instanceof BufferedWorkstation)
         workstation = ((BufferedWorkstation) observableSimObject).getWorkstation();
      else if (observableSimObject instanceof Workstation)
         workstation = (Workstation) observableSimObject;
      if (null != workstation) {
         if (workstation instanceof WorkCell) {
            initWorkCell((WorkCell) workstation, timer.getSimulationTime());

         } else if (workstation instanceof WorkGroup) {
            WorkGroup group = (WorkGroup) workstation;
            long simulationTime = timer.getSimulationTime();
            for (WorkCell cell : group.getWorkCells()) {
               initWorkCell(cell, simulationTime);
            }
         }
      }
   }

   @Override
   public void onChange(ObservableSimObject observableSimObject) {
      if (observableSimObject instanceof WorkCell) {
         WorkCell workstation = (WorkCell) observableSimObject;
         WorkstationEvent running = runningEvents.get(observableSimObject);
         if (!workstation.getStatus().equals(running.getStatus())) {
            long simulationTime = timer.getSimulationTime();
            long duration = simulationTime - running.getStartTime();
            if (duration > 0) {
               running.setDuration(duration);
               queue.add(running);
            }
            initWorkCell(workstation, simulationTime);
         }
      }
   }

   public WorkstationEvent dequeue() {
      return queue.poll();
   }

}
