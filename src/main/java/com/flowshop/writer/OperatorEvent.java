package com.flowshop.writer;

import com.flowshop.simulator.Operator;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class OperatorEvent {

   private final String operatorId;
   private final String groupId;
   private Operator.Status status;
   private long startTime;
   private long duration;
   private String workstationId;
   private String operationId;
   private String orderId;
}
