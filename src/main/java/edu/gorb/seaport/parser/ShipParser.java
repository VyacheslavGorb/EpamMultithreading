package edu.gorb.seaport.parser;

import edu.gorb.seaport.entity.Ship;
import edu.gorb.seaport.exception.SeaPortException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class ShipParser {
    private static final Logger logger = LogManager.getLogger();

    public List<Ship> parseShips(List<String> shipLines) throws SeaPortException {
        List<Ship> shipList;
        try {
            shipList = shipLines.stream()
                    .map(Ship.ShipTask::valueOf)
                    .map(Ship::new)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            logger.log(Level.ERROR, "Error while parsing ship lines");
            throw new SeaPortException("Error while parsing ship lines");
        }
        logger.log(Level.INFO, "Lines parsed successfully");
        return shipList;
    }
}
