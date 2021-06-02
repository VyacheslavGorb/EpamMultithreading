package edu.gorb.seaport._main;

import edu.gorb.seaport.entity.Ship;
import edu.gorb.seaport.exception.SeaPortException;
import edu.gorb.seaport.parser.ShapeParser;
import edu.gorb.seaport.reader.ShipFileReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    private static final String SHIP_FILE_PATH = "files/shipData.txt";

    public static void main(String[] args) {
        URL fileURL = Main.class.getClassLoader().getResource(SHIP_FILE_PATH);
        if(fileURL == null){
            logger.log(Level.FATAL, "File {} does not exist", SHIP_FILE_PATH);
            return;
        }
        File shipFile = new File(fileURL.getFile());
        ShipFileReader reader = new ShipFileReader();
        List<String> fileLines;
        List<Ship> ships;
        try {
            fileLines = reader.readFile(shipFile.getAbsolutePath());
            ShapeParser parser = new ShapeParser();
            ships = parser.parseShips(fileLines);
        } catch (SeaPortException e) {
            logger.log(Level.FATAL, e.getMessage());
            return;
        }
        ExecutorService service = Executors.newFixedThreadPool(ships.size());
        ships.forEach(service::execute);
        service.shutdown();
    }
}
