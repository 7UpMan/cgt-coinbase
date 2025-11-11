package com.s4apps;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CsvHandler  {
    private static final Logger LOGGER = LogManager.getLogger();

    private String filePath;
    private FileReader reader = null;
    Iterable<CSVRecord> records = null;
    Iterator<CSVRecord> recordI = null;

    public CsvHandler(String filePath) {
        this.filePath = filePath;

        try {
            reader = new FileReader(filePath);

            // Skip 2 header lines
            skipLine(reader);
            skipLine(reader);
            skipLine(reader);

            records = CSVFormat.DEFAULT.builder().setHeader().build().parse(reader);
            recordI = records.iterator();

        } catch (IOException fnfe) {
            LOGGER.error("Error processing file {}", filePath, fnfe);
            System.exit(1);
        } finally {

        }
    }

    public CsvRow getRow() {

        if (!recordI.hasNext() || filePath == null) {
            return null;
        }
        CSVRecord csvRecord = recordI.next();
        String csvId = csvRecord.get("ID");
        String csvTimestamp = csvRecord.get("Timestamp");
        String csvTransactionType = csvRecord.get("Transaction Type");
        String csvAsset = csvRecord.get("Asset");
        String csvQuantityTransacted = csvRecord.get("Quantity Transacted");
        String csvPriceCurrency = csvRecord.get("Price Currency");
        String csvPriceAtTransaction = csvRecord.get("Price at Transaction");
        String csvSubtotal = csvRecord.get("Subtotal");
        String csvTotal = csvRecord.get(8);
        String csvFeesAndSpreads = csvRecord.get(9);
        String csvNotes = csvRecord.get(10);

        return new CsvRow(csvId, csvTimestamp, csvTransactionType, csvAsset,
                csvQuantityTransacted, csvPriceCurrency, csvPriceAtTransaction, csvSubtotal,
                csvTotal, csvFeesAndSpreads, csvNotes);

    }

    public void close() {
        try {
            if (reader != null) {
                reader.close();
            }

        } catch (IOException e) {
            // Do nothing
        }
    }

    public Iterable<CsvRow> getAllRows() {
        return new CsvRowIterable();
    }

    private class CsvRowIterable implements Iterable<CsvRow> {

        @Override
        public Iterator<CsvRow> iterator() {
            return new CsvRowIterator();
        }
    }

    private class CsvRowIterator implements Iterator<CsvRow> {
        @Override
        public boolean hasNext() {
            return recordI.hasNext();
        }

        @Override
        public CsvRow next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return getRow();
        }

        @Override
        public void remove() {
            recordI.remove();
        }
    }

    /**
     * Process the input until a char 10 is found, then return. If an End Of File is
     * reached first, then throw an Exception.
     * 
     * @param in
     * @throws IOException
     */
    private void skipLine(Reader in) throws IOException {
        while (true) {
            int inChar = in.read();
            if (inChar == -1) {
                LOGGER.error("Got EOF in skipLine.");
                System.exit(2);
            }

            if (inChar == 10)
                return;
        }
    }
}
