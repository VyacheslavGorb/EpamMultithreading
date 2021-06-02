package edu.gorb.seaport.reader;

import edu.gorb.seaport.exception.SeaPortException;
import edu.gorb.seaport.validator.PortFileValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShipFileReader {
    private static final Logger logger = LogManager.getLogger();

    public List<String> readFile(String filePathString) throws SeaPortException {
        if (!PortFileValidator.isValidFile(filePathString)) {
            logger.log(Level.ERROR, "File path represents invalid file");
            throw new SeaPortException("File path represents invalid file");
        }
        Path path = Paths.get(filePathString);
        List<String> textLines;
        try (Stream<String> fileLines = Files.lines(path)) {
            textLines = fileLines.collect(Collectors.toList());
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error while reading file {}", filePathString);
            throw new SeaPortException("Error while reading file " + filePathString);
        }
        logger.log(Level.INFO, "Ships were read successfully in file: {}", filePathString);
        return textLines;
    }
}
