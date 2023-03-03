package com.flowshop.writer;

import java.util.concurrent.LinkedBlockingDeque;

import com.flowshop.simulator.BufferedWorkstation;
import com.flowshop.simulator.Operator;
import com.flowshop.simulator.Simulation;
import com.flowshop.simulator.Workstation;

public abstract class AbstractEventsWriter implements Runnable {
    private final WorkstationListener workstationListener;
    private final OperatorListener operatorListener;
    private final WorkstationBufferListener bufferListener;
    private final LinkedBlockingDeque<Object> queue = new LinkedBlockingDeque<>();
    private volatile boolean stopped = false;

    public AbstractEventsWriter(Simulation simulation) {
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
    public void run() {
        try {
            while (!stopped || !queue.isEmpty()) {
                Object event = queue.take();
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
        } catch (InterruptedException ie) {

        }
        close();
    }

    public void setStopped(boolean b) {
        this.stopped = true;
        // TODO: il modo migliore sarebbe rendere l'ObserverSimObject una classe astratta, in fase di creazione passare una simulazione, aggiugerlo ad un set di observers gestito dalla simulazione e al termine 
        // dovrebbe essere la simulazione a notificare tutti gli observers.
        workstationListener.onEndSimulation();
        operatorListener.onEndSimulation();
        bufferListener.onEndSimulation();
     }

    abstract void catchOperatorEvent(OperatorEvent event);

    abstract void catchWorkstationBufferEvent(WorkstationBufferEvent event);

    abstract void catchWorkstationEvent(WorkstationEvent event);

    abstract void close();
}
