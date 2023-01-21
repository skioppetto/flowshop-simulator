package com.flowshop;

import lombok.Data;

@Data
public class Operation {
   private final String id;
   private int requiredOperators;
}
