package com.s4apps;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    private int directoryListingIndex = 0;
    private ArrayList<File> matchingFiles = new ArrayList<>();

    public DirHelper(String dirPath) {

        // Open the dir
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            LOGGER.error("{} is not a Directory.", dir.getName());
            System.exit(1);
        }
        LOGGER.info("Processing files in folder {}", dir.getAbsolutePath());

        // Iterate through files looking for CSV ones and add to an ArrayList
        for (File singleFile : dir.listFiles()) {
            String fileName = singleFile.getName();
            String fileLast4 = fileName.substring(fileName.length() - 4).toLowerCase();
            if (!singleFile.isFile() || !singleFile.canRead() || !fileLast4.equals(".csv")) {
                LOGGER.info("Skipping file {}", singleFile.getName());
            } else {
                matchingFiles.add(singleFile);
            }
        }

    }

    public Iterable<File> getAllMatchingFiles() {
        return new FileIterable();
    }

    private class FileIterable implements Iterable<File> {
        @Override
        public Iterator<File> iterator() {
            return new FileIterator();
        }

    }

    private class FileIterator implements Iterator<File> {
        @Override
        public boolean hasNext() {
            return directoryListingIndex < matchingFiles.size();
        }

        @Override
        public File next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return matchingFiles.get(directoryListingIndex++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove operation is not supported");
        }
    }

}
