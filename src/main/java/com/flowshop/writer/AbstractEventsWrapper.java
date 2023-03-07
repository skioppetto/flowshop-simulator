package com.flowshop.writer;

import java.util.concurrent.LinkedBlockingDeque;

import com.flowshop.simulator.BufferedWorkstation;
import com.flowshop.simulator.Operator;
import com.flowshop.simulator.SimObserver;
import com.flowshop.simulator.Simulation;
import com.flowshop.simulator.Workstation;

public abstract class AbstractEventsWrapper implements SimObserver {
    private WorkstationListener workstationListener;
    private OperatorListener operatorListener;
    private WorkstationBufferListener bufferListener;
    private final LinkedBlockingDeque<Object> queue = new LinkedBlockingDeque<>();

    @Override
    public void onProcessEnd(Simulation sim) {
        if (!queue.isEmpty()) {
            Object event = queue.poll();
            if (event != null) {
                if (event instanceof WorkstationEvent) {
                    catchWorkstationEvent((WorkstationEvent) event);
                } else if (event instanceof WorkstationBufferEvent) {
                    catchWorkstationBufferEvent((WorkstationBufferEvent) event);
                } else if (event instanceof OperatorEvent) {
                    catchOperatorEvent((OperatorEvent) event);
                }
            }
        }

    }

    @Override
    public void onProcessStart(Simulation sim) {

    }

    @Override
    public void onStartSimulation(Simulation simulation) {
        this.workstationListener = new WorkstationListener(simulation, queue);
        this.operatorListener = new OperatorListener(simulation, queue);
        this.bufferListener = new WorkstationBufferListener(simulation, queue);
        for (Workstation w : simulation.getWorkstations()) {
            w.addSimObjectObserver(workstationListener);
            if (w instanceof BufferedWorkstation) {
                BufferedWorkstation bw = (BufferedWorkstation) w;
                bw.getAfterBuffer().addSimObjectObserver(bufferListener);
                bw.getBeforeBuffer().addSimObjectObserver(bufferListener);
            }
        }
        for (Operator op : simulation.getAvailableOperators())
            op.addSimObjectObserver(operatorListener);
    }

    @Override
    public void onStopSimulation(Simulation sim) {
        workstationListener.onStopSimulation();
        operatorListener.onStopSimulation();
        bufferListener.onStopSimulation();
        close();
    }

    abstract void catchOperatorEvent(OperatorEvent event);

    abstract void catchWorkstationBufferEvent(WorkstationBufferEvent event);

    abstract void catchWorkstationEvent(WorkstationEvent event);

    abstract void close();
}
