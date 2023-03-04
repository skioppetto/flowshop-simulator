package com.flowshop.utils;

import lombok.Data;

@Data
public class OrdersGeneratorConfig {
   
   private int ordersCount;
   private int operationsCount;
   private long cycleTimeMin;
   private long cycleTimeMax;
   private int operatorsMin;
   private int operatorsMax;
   private String filename;

}
