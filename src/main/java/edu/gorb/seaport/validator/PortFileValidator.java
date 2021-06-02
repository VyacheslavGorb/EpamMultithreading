package edu.gorb.seaport.validator;

import java.io.File;

public class PortFileValidator {
    private PortFileValidator() {
    }

    public static boolean isValidFile(String filePathString) {
        if (filePathString == null) {
            return false;
        }
        File file = new File(filePathString); // throws NullPointerException if filePathString is null
        if (!file.isFile()) {
            return false;
        }
        return file.length() != 0;
    }

}
