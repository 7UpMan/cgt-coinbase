package com.s4apps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CsvRow {
    private static final Logger LOGGER = LogManager.getLogger();

    private static PreparedStatement psIns = null;
    private static PreparedStatement psSel = null;
    private static PreparedStatement psSelExists = null;
    private static PreparedStatement psUpd = null;

    // Define the Insert SQL
    private static final String INSERT_SQL = "INSERT INTO csvImport(id, timeStamp, transactionType, "
            + "asset, quantityTransacted, priceCurrency, priceAtTransaction, subtotal, "
            + "total, feesAndSpreads, notes, fileName, importDatetime) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,now())";

    // Define the Update SQL
    private static final String UPDATE_SQL = "UPDATE csvImport SET timeStamp=?, transactionType=?, "
            + "asset=?, quantityTransacted=?, priceCurrency=?, priceAtTransaction=?, "
            + "subtotal=?,total=?, feesAndSpreads=?, notes=?, fileName=?, "
            + "importDatetime=?  WHERE id = ?";

    // Define the SELECT SQL
    private static final String SELECT_SQL = "SELECT id, timeStamp, transactionType, asset, quantityTransacted, "
            + "priceCurrency, priceAtTransaction, subtotal, total, feesAndSpreads, notes, fileName, "
            + "importDatetime FROM csvImport WHERE id=?";

    // Define SQL to see if an ID exists
    private static final String CHECK_EXISTS_SQL = "SELECT COUNT(id) from csvImport WHERE id = ?";

    private String csvId;
    private String csvTimestamp;
    private String csvTransactionType;
    private String csvAsset;
    private String csvQuantityTransacted;
    private String csvPriceCurrency;
    private String csvPriceAtTransaction;
    private String csvSubtotal;
    private String csvTotal;
    private String csvFeesAndSpreads;
    private String csvNotes;

    public CsvRow(String csvId, String csvTimestamp, String csvTransactionType, String csvAsset,
            String csvQuantityTransacted, String csvPriceCurrency, String csvPriceAtTransaction, String csvSubtotal,
            String csvTotal, String csvFeesAndSpreads, String csvNotes) {
        this.csvId = csvId;
        this.csvTimestamp = csvTimestamp;
        this.csvTransactionType = csvTransactionType;
        this.csvAsset = csvAsset;
        this.csvQuantityTransacted = csvQuantityTransacted;
        this.csvPriceCurrency = csvPriceCurrency;
        this.csvPriceAtTransaction = csvPriceAtTransaction;
        this.csvSubtotal = csvSubtotal;
        this.csvTotal = csvTotal;
        this.csvFeesAndSpreads = csvFeesAndSpreads;
        this.csvNotes = csvNotes;

    }

    public String getCsvId() {
        return csvId;
    }

    public void setCsvId(String csvId) {
        this.csvId = csvId;
    }

    public String getCsvTimestamp() {
        return csvTimestamp;
    }

    public void setCsvTimestamp(String csvTimestamp) {
        this.csvTimestamp = csvTimestamp;
    }

    public String getCsvTransactionType() {
        return csvTransactionType;
    }

    public void setCsvTransactionType(String csvTransactionType) {
        this.csvTransactionType = csvTransactionType;
    }

    public String getCsvAsset() {
        return csvAsset;
    }

    public void setCsvAsset(String csvAsset) {
        this.csvAsset = csvAsset;
    }

    public String getCsvQuantityTransacted() {
        return csvQuantityTransacted;
    }

    public void setCsvQuantityTransacted(String csvQuantityTransacted) {
        this.csvQuantityTransacted = csvQuantityTransacted;
    }

    public String getCsvPriceCurrency() {
        return csvPriceCurrency;
    }

    public void setCsvPriceCurrency(String csvPriceCurrency) {
        this.csvPriceCurrency = csvPriceCurrency;
    }

    public String getCsvPriceAtTransaction() {
        return csvPriceAtTransaction;
    }

    public void setCsvPriceAtTransaction(String csvPriceAtTransaction) {
        this.csvPriceAtTransaction = csvPriceAtTransaction;
    }

    public String getCsvSubtotal() {
        return csvSubtotal;
    }

    public void setCsvSubtotal(String csvSubtotal) {
        this.csvSubtotal = csvSubtotal;
    }

    public String getCsvTotal() {
        return csvTotal;
    }

    public void setCsvTotal(String csvTotal) {
        this.csvTotal = csvTotal;
    }

    public String getCsvFeesAndSpreads() {
        return csvFeesAndSpreads;
    }

    public void setCsvFeesAndSpreads(String csvFeesAndSpreads) {
        this.csvFeesAndSpreads = csvFeesAndSpreads;
    }

    public String getCsvNotes() {
        return csvNotes;
    }

    public void setCsvNotes(String csvNotes) {
        this.csvNotes = csvNotes;
    }

    public int persist(DbHelper dbHelper, String fileName) {
        setupDb(dbHelper);

        try {
            psSelExists.setString(1, csvId);
            ResultSet rs = psSelExists.executeQuery();
            rs.next();
            int rowCount = rs.getInt(1);
            rs.close();
            if (rowCount != 0) {
                // Already there so return
                return 0;
            }

            psIns.setString(1, csvId);
            psIns.setString(2, Tools.parseDate(csvTimestamp));
            psIns.setString(3, csvTransactionType);
            psIns.setString(4, csvAsset);
            psIns.setString(5, csvQuantityTransacted);
            psIns.setString(6, csvPriceCurrency);
            psIns.setString(7, Tools.stripDollar(csvPriceAtTransaction));
            psIns.setString(8, Tools.stripDollar(csvSubtotal));
            psIns.setString(9, Tools.stripDollar(csvTotal));
            psIns.setString(10, Tools.stripDollar(csvFeesAndSpreads));
            psIns.setString(11, csvNotes);
            psIns.setString(12, fileName);

            return psIns.executeUpdate();

        } catch (SQLException se) {
            LOGGER.error("Issue INSERTing row.", se);
            System.exit(1);
        }
        return 0;
    }

    private static void setupDb(DbHelper dbHelper) {
        Connection connection = dbHelper.getConnection();

        try {
            // Prepare the statements
            if (psIns == null) {
                psIns = connection.prepareStatement(INSERT_SQL);
            }

            if (psSel == null) {
                psSel = connection.prepareStatement(SELECT_SQL);
            }

            if (psUpd == null) {
                psUpd = connection.prepareStatement(UPDATE_SQL);
            }

            if (psSelExists == null) {
                psSelExists = connection.prepareStatement(CHECK_EXISTS_SQL);
            }

        } catch (SQLException se) {
            LOGGER.error("Cannot prepare statements.", se);
            System.exit(1);
        }
    }
}
