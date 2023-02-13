package com.flowshop.reader;

public interface IConfigurationReader {

   public WorkstationRequirements[] getWorkstationsRequirements();

   public OperatorRequirements[] getOperatorRequirements();

   public OrderRequirements[] getOrdersRequirements();

}
