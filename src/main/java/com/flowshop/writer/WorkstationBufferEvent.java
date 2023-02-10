package com.flowshop.writer;

import com.flowshop.simulator.WorkstationBuffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class WorkstationBufferEvent {

   public enum EventType {
      ENQUEUE, DEQUEUE
   };

   @Getter
   private final String workstationId;
   @Getter
   private final WorkstationBuffer.Type bufferType;
   @Getter
   @Setter
   private long time;
   @Getter
   @Setter
   private int size;
   @Getter
   @Setter
   private EventType eventType;
}
