package com.flowshop.writer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.flowshop.simulator.ISimulationTimer;
import com.flowshop.simulator.ObservableSimObject;
import com.flowshop.simulator.SimObjectObserver;
import com.flowshop.simulator.WorkstationBuffer;

public class WorkstationBufferListener implements SimObjectObserver {

   private final ISimulationTimer timer;

   private final Map<String, WorkstationBufferEvent> runningEvents = new HashMap<>();

   private final Queue<WorkstationBufferEvent> queue = new LinkedList<>();

   public WorkstationBufferListener(ISimulationTimer timer) {
      this.timer = timer;
   }

   @Override
   public void onChange(ObservableSimObject observableSimObject) {
      if (observableSimObject instanceof WorkstationBuffer) {
         WorkstationBuffer buffer = (WorkstationBuffer) observableSimObject;
         WorkstationBufferEvent running = runningEvents.get(buildMapKey(buffer));
         if (buffer.size() != running.getSize()) {
            if (buffer.size() > running.getSize()) {
               running.setEventType(WorkstationBufferEvent.EventType.ENQUEUE);
            } else if (buffer.size() < running.getSize()) {
               running.setEventType(WorkstationBufferEvent.EventType.DEQUEUE);
            }
            running.setSize(buffer.size());
            running.setTime(timer.getSimulationTime());
            queue.add(running);
         }
         runningEvents.put(buildMapKey(buffer), initEvent(buffer));
      }
   }

   @Override
   public void onAdded(ObservableSimObject observableSimObject) {
      if (observableSimObject instanceof WorkstationBuffer) {
         WorkstationBuffer buffer = (WorkstationBuffer) observableSimObject;
         runningEvents.put(buildMapKey(buffer), initEvent(buffer));
      }
   }

   private WorkstationBufferEvent initEvent(WorkstationBuffer buffer) {
      WorkstationBufferEvent event = new WorkstationBufferEvent(buffer.getWorkstation().getId(), buffer.getType());
      event.setSize(buffer.size());
      return event;
   }

   private String buildMapKey(WorkstationBuffer buffer) {
      return buffer.getWorkstation().getId() + buffer.getType().toString();
   }

   public WorkstationBufferEvent dequeue() {
      return queue.poll();
   }

}
