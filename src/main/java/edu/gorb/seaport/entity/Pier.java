package edu.gorb.seaport.entity;

import edu.gorb.seaport.util.PierIdGenerator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Pier {
    private static final Logger logger = LogManager.getLogger();
    private final int pierId;

    public Pier() {
        pierId = PierIdGenerator.generateId();
    }

    public void processShip(Ship ship) {
        logger.log(Level.INFO, "Pier {} processes ship {}: Started", pierId, ship.getShipId());
        try {
            TimeUnit.MILLISECONDS.sleep(100); // TODO
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, "Error while processing ship : {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        SeaPort seaPort = SeaPort.getInstance();
        switch (ship.getTask()) {
            case LOAD -> seaPort.loadContainer();
            case UNLOAD -> seaPort.unloadContainer();
        }
        logger.log(Level.INFO, "Pier {} processes ship {} : Complete", pierId, ship.getShipId());
    }

    public int getPierId() {
        return pierId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Pier{");
        sb.append("pierId=").append(pierId);
        sb.append('}');
        return sb.toString();
    }

    //TODO equals hashcode ??????
}
