package com.flowshop.reader;

import lombok.Data;

@Data
public class OrderRequirements {
   private String orderId;
   private OperationRequrements[] operations;
}
