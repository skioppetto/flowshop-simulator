package com.flowshop.writer;

import com.flowshop.simulator.Workstation;

import lombok.Data;

@Data
public class WorkstationEvent {

   private String workstationId;
   private String workGroupId;
   private Workstation.Status status;
   private long startTime;
   private long duration;
   

}
