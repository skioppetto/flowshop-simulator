package com.flowshop;

import java.util.LinkedList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkstationBuffer extends LinkedList<Operation>{

   public enum Type {AFTER, BEFORE};

   @Getter private final Type type;
   @Getter private final Workstation workstation; 
   
}
