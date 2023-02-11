package com.flowshop.reader;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class OperationRequrements {
   private String operationId;
   private long cycleTime;
   @SerializedName("requiredWorkstation")
   private String workstation;
   @SerializedName("requiredOperatorsAny")
   @Expose
   private int operatorsAny;
   @SerializedName("requiredOperatorsGroups")
   @Expose
   private List<OperatorsGroupRequirements> operatorsGroups;

}