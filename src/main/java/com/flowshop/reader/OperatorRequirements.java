package com.flowshop.reader;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class OperatorRequirements {

   private String name;
   @Expose
   private String group;

}
