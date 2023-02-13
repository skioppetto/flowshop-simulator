package com.flowshop.reader;

import lombok.Data;

@Data
public class WorkstationRequirements {
    
    private String name;
    private int bufferAfter;
    private int bufferBefore;
    private int workGroupCells;

}
