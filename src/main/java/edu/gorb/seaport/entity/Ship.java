package edu.gorb.seaport.entity;

import edu.gorb.seaport.util.ShipIdGenerator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ship implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    private ShipTask task;
    private final int shipId;

    //TODO add state

    public enum ShipTask {
        LOAD, UNLOAD;
    }

    public Ship(ShipTask task) {
        this.task = task;
        shipId = ShipIdGenerator.generateId();
    }

    public ShipTask getTask() {
        return task;
    }

    public int getShipId() {
        return shipId;
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Ship {} started {}", shipId, task.toString().toLowerCase());
        SeaPort seaPort = SeaPort.getInstance();
        Pier pier = seaPort.obtainPier();
        pier.processShip(this);
        seaPort.releasePier(pier);
        logger.log(Level.INFO, "EXIT: Ship {} ended {}", shipId, task.toString().toLowerCase());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Ship{");
        sb.append("task=").append(task.toString().toLowerCase());
        sb.append(", shipId=").append(shipId);
        sb.append('}');
        return sb.toString();
    }

    //TODO equals hashcode ??????
}
