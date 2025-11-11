package com.s4apps;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TableRow implements Cloneable {
    private static final Logger LOGGER = LogManager.getLogger();

    private static PreparedStatement psIns = null;
    private static PreparedStatement psSel = null;
    private static PreparedStatement psSelExists = null;
    private static PreparedStatement psUpd = null;
    private static PreparedStatement psSelAll = null;


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
    
    // Define the SELECT SQL for all rows
    private static final String SELECT_ALL_SQL = "SELECT id, timeStamp, transactionType, asset, quantityTransacted, "
            + "priceCurrency, priceAtTransaction, subtotal, total, feesAndSpreads, notes, fileName, "
            + "importDatetime FROM csvImport WHERE timeStamp <= '2025-04-05' ORDER BY timeStamp";


    // Define SQL to see if an ID exists
    private static final String CHECK_EXISTS_SQL = "SELECT COUNT(id) from csvImport WHERE id = ?";

    private String id;
    private LocalDateTime timestamp;
    private String transactionType;
    private String asset;
    private double quantityTransacted;
    private String priceCurrency;
    private double priceAtTransaction;
    private double subtotal;
    private double total;
    private double feesAndSpreads;
    private String notes;
    private boolean npProcessed = false;

    public TableRow(String id, LocalDateTime timestamp, String transactionType, String asset,
            double quantityTransacted, String priceCurrency, double priceAtTransaction, double subtotal,
            double total, double feesAndSpreads, String notes) {
        this.id = id;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.asset = asset;
        this.quantityTransacted = quantityTransacted;
        this.priceCurrency = priceCurrency;
        this.priceAtTransaction = priceAtTransaction;
        this.subtotal = subtotal;
        this.total = total;
        this.feesAndSpreads = feesAndSpreads;
        this.notes = notes;

    }

    @Override
    public Object clone() {
        return new TableRow(id, timestamp, transactionType, asset, 
        quantityTransacted, priceCurrency, priceAtTransaction, subtotal, total, feesAndSpreads, notes);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public double getQuantityTransacted() {
        return quantityTransacted;
    }

    public void setQuantityTransacted(double quantityTransacted) {
        this.quantityTransacted = quantityTransacted;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public double getPriceAtTransaction() {
        return priceAtTransaction;
    }

    public void setPriceAtTransaction(double priceAtTransaction) {
        this.priceAtTransaction = priceAtTransaction;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getFeesAndSpreads() {
        return feesAndSpreads;
    }

    public void setFeesAndSpreads(double feesAndSpreads) {
        this.feesAndSpreads = feesAndSpreads;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    
    
    /** 
     * Check the flag "processed".  This flag is not read from the database and is not 
     * persisted.  It defaults to false;
     * 
     * @return boolean
     */
    public boolean isNpProcessed() {
        return npProcessed;
    }

    
    
    /**
     * Set the flag "processed".  This flag is not read from the dateabase and is not presisted.  It defaults to false;
     * @param npProcessed
     */
    public void setNpProcessed(boolean npProcessed) {
        this.npProcessed = npProcessed;
    }

    public int persist(DbHelper dbHelper, String fileName) {
        setupDb(dbHelper);

        try {
            psSelExists.setString(1, id);
            ResultSet rs = psSelExists.executeQuery();
            rs.next();
            int rowCount = rs.getInt(1);
            rs.close();
            if (rowCount != 0) {
                // Already there so return
                return 0;
            }

            psIns.setString(1, id);
            psIns.setTimestamp(2, Timestamp.valueOf(timestamp));
            psIns.setString(3, transactionType);
            psIns.setString(4, asset);
            psIns.setDouble(5, quantityTransacted);
            psIns.setString(6, priceCurrency);
            psIns.setDouble(7, priceAtTransaction);
            psIns.setDouble(8, subtotal);
            psIns.setDouble(9, total);
            psIns.setDouble(10, feesAndSpreads);
            psIns.setString(11, notes);
            psIns.setString(12, fileName);

            return psIns.executeUpdate();

        } catch (SQLException se) {
            LOGGER.error("Issue INSERTing row.", se);
            System.exit(1);
        }
        return 0;
    }

    
    /** 
     * Get all of the rows from the "csvImport" table.  The data is ordered by the "timestamp" on the record (which is
     * the date of the transaction from Coinbase).
     * 
     * @param dbHelper
     * @return List<TableRow>
     */
    public static List<TableRow> getAllRows(DbHelper dbHelper) {
        setupDb(dbHelper);
        
        ArrayList<TableRow> rowList = new ArrayList<>();

        try {
            ResultSet rs = psSelAll.executeQuery();
            while (rs.next()) {
                TableRow newRow = new TableRow(
                    rs.getString("id"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    rs.getString("transactionType"),
                    rs.getString("asset"),
                    rs.getDouble("quantityTransacted"),
                    rs.getString("priceCurrency"),
                    rs.getDouble("priceAtTransaction"),
                    rs.getDouble("subtotal"),
                    rs.getDouble("total"),
                    rs.getDouble("feesAndSpreads"),
                    rs.getString("notes")
                    );
                rowList.add(newRow);
            }


        } catch (SQLException se) {

        }

        return rowList;
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

            if (psSelAll == null) {
                psSelAll = connection.prepareStatement(SELECT_ALL_SQL);
            }
        } catch (SQLException se) {
            LOGGER.error("Cannot prepare statements.", se);
            System.exit(1);
        }
    }
}
