package com.s4apps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Hello world!
 *
 */
public class LoadData {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String INPUTDIR = "data";
    public static final String OUTPUTDIR = "data/done/";
    private static DbHelper dbHelper = null;

    public static void main(String[] args) {
        // Target DIR
        File outputDir = new File(OUTPUTDIR);

        // Get a list of files that we want from a directory
        DirHelper dirHelper = new DirHelper(INPUTDIR);

        dbHelper = new DbHelper();

        for(File file: dirHelper.getAllMatchingFiles()) {
            process1file(file);
            Tools.moveToDir(file, outputDir);
        }


        
        dbHelper.close();

    }

    private static void process1file(File file) {
        String absolutePath = file.getAbsolutePath();

        CsvHandler csvHandler = new CsvHandler(absolutePath);

        int totalRows = 0;
        int addedRows = 0;

        for(CsvRow csvRow: csvHandler.getAllRows()) {
            totalRows++;
            addedRows += csvRow.persist(dbHelper, file.getName());
        }
        dbHelper.commit();
        csvHandler.close();
        
        
        LOGGER.info("Processed a total of {} records and added {} of them.", totalRows, addedRows);
    }


}