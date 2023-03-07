package com.flowshop.writer;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.flowshop.simulator.ISimulationTimer;
import com.flowshop.simulator.ObservableSimObject;
import com.flowshop.simulator.Operation;
import com.flowshop.simulator.Operator;
import com.flowshop.simulator.SimObjectObserver;
import com.flowshop.simulator.WorkCell;

public class OperatorListener implements SimObjectObserver {

   private final ISimulationTimer timer;
   private final Queue<Object> queue;
   private final Map<Operator, OperatorEvent> runningEvents = new HashMap<>();

   public OperatorListener(ISimulationTimer timer, Queue<Object> queue) {
      this.timer = timer;
      this.queue = queue;
   }

   @Override
   public void onChange(ObservableSimObject observableSimObject) {
      Operator operator = (Operator) observableSimObject;
      OperatorEvent latestEvent = runningEvents.get(operator);
      long time = timer.getSimulationTime();
      if (!operator.getStatus().equals(latestEvent.getStatus())) {
         latestEvent.setDuration(time - latestEvent.getStartTime());
         queue.add(latestEvent);
         OperatorEvent nextEvent = new OperatorEvent(operator.getId(), operator.getGroup());
         runningEvents.put(operator, nextEvent);
         nextEvent.setStatus(operator.getStatus());
         nextEvent.setStartTime(time);
         if (operator.getAssignedWorkstation() != null) {
            WorkCell workstation = operator.getAssignedWorkstation();
            nextEvent.setWorkstationId(workstation.getId());
            if (workstation.getCurrentOperation() != null) {
               Operation operation = workstation.getCurrentOperation();
               nextEvent.setOperationId(operation.getId());
               if (operation.getOrder() != null)
                  nextEvent.setOrderId(operation.getOrder().getId());
            }
         }
      }
   }

   @Override
   public void onAdded(ObservableSimObject observableSimObject) {
      Operator operator = (Operator) observableSimObject;
      OperatorEvent running = new OperatorEvent(operator.getId(), operator.getGroup());
      running.setStatus(Operator.Status.IDLE);
      running.setStartTime(timer.getSimulationTime());
      runningEvents.put(operator, running);
   }

   @Override
   public void onStopSimulation() {
      long simulationTime = timer.getSimulationTime();
      for (OperatorEvent event : runningEvents.values()) {
         long duration = simulationTime - event.getStartTime();
         if (duration > 0) {
            event.setDuration(duration);
            queue.add(event);
         }
      }
      runningEvents.clear();
   }

}
