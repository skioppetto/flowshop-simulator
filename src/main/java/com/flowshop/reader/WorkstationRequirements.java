package com.flowshop.reader;

import lombok.Data;

@Data
public class WorkstationRequirements {
    
    private String name;
    private Integer bufferAfter;
    private Integer bufferBefore;
    private Integer workGroupCells;

}
