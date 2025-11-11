package com.s4apps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tools {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String CURR = "&#36;";

    private Tools() {
        // Prevent instantiation
    }

    public static String parseDate(String oldDate) {
        // Parse a date like "2024-12-16 14:28:11 UTC" into something that MySQL likes i.e. "2024-12-16 14:28:11"
        String [] parsed = oldDate.split(" ");
        return parsed[0] + " " + parsed[1];
    }

    public static String stripDollar(String oldString) {
        return oldString.replace("$","");
    }

    public static void moveToDir(File sourceFile, File targetDir) {
        // Check the source
        if(sourceFile.isFile() && sourceFile.canRead()) {
            // All good
        } else {
            LOGGER.error("File \"{}\" not a file or cannot read it.", sourceFile.getName());
            System.exit(1);
        }

        // Check the target
        if(targetDir.isDirectory() && targetDir.canWrite()) {
            // All good
        } else {
            LOGGER.error("File \"{}\" not a directory or cannot write it.", targetDir.getName());
            System.exit(1);
        }

        try {
            Path sourcePath =Paths.get(sourceFile.getAbsolutePath());
            Path targetPath = Paths.get(targetDir.getAbsolutePath());
            Path newPath = Files.move(sourcePath, targetPath.resolve(sourcePath.getFileName()));
        } catch (IOException e) {
            LOGGER.error("File \"{}\" not moved to directory \"{}\", got exception.", sourceFile.getAbsolutePath(), targetDir.getAbsolutePath(), e);
            System.exit(2);
        }

    }

public static void writeHtmlOutput(String fileName, String content) {
        BufferedWriter outputReportFile;

        try {
            // Create the file

            //Specify the file name and path here
            File file = new File(fileName);

            // This logic will make sure that the file gets created if it is not present at the
            // specified location
            if (!file.exists()) {
                file.createNewFile();
            }
            outputReportFile = new BufferedWriter(new FileWriter(file));

            // Write the content
            outputReportFile.write(content);

            // Close the file
            outputReportFile.flush();
            outputReportFile.close();

        } catch (IOException ex) {
            LOGGER.error("Could not create HTML Report file {}.", fileName, ex);
            System.exit(1);
        }
    }

    /**
     * Check if a double is effectively zero
     * @param dbl
     * @return
     */
    public static boolean isDoubleZero(double dbl) {
        final double TOLERANCE = 0.000001;
        return Math.abs(dbl) < TOLERANCE;
    }

    public static double round(double dbl, int places) {
        double scale = Math.pow(10, places);
        
        return Math.round(dbl * scale) / scale;

    }
}
