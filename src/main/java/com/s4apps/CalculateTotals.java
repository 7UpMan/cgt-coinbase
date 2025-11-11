package com.s4apps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.s4apps.htmlOutput.BuySellConvertTransactions;
import com.s4apps.htmlOutput.DepositTransactions;
import com.s4apps.htmlOutput.OtherTransactions;
import com.s4apps.htmlOutput.ContentsTable;
import com.s4apps.htmlOutput.Page;
import com.s4apps.htmlOutput.TaxSummaryTable;

public class CalculateTotals {
    private static final String HTMLFILE = "/home/shares/tmp/Coinbase.html";
    private static final Logger LOGGER = LogManager.getLogger();

    private List<TableRow> allRows = null;
    private List<TableRow> cashTransactions = new ArrayList<>();
    private HashMap<String, List<TableRow>> trades = new HashMap<>();
    private List<TableRow> otherTransactions = new ArrayList<>();
    private int totalRowsProcessed = 0;

    private DepositTotals cashTotals = new DepositTotals(0f, 0f);
    private TradeTotals tradeTotals = new TradeTotals(0f, 0f, 0f);

    /**
     * @param args
     */
    public static void main(String[] args) {

        CalculateTotals ct = new CalculateTotals();

        List<String> pageSegments = new ArrayList<>(Collections.nCopies(5, null));
        
        // These are out of order because the Summary table needs printTrades() to run first.
        // All of the calculations should be in proessAllTransactions(), but the total
        // counters are not.
        
        pageSegments.set(0, ContentsTable.getTable());
        pageSegments.set(2, ct.printDepositTransactions());
        pageSegments.set(3, ct.printTrades());
        pageSegments.set(4, ct.printOtherTransactions());
        TaxSummaryTable taxSummaryTable = new TaxSummaryTable(ct.tradeTotals);
        pageSegments.set(1, taxSummaryTable.getTable());

        Page outputPage = new Page();

        // Add all of the segments to the page
        pageSegments.forEach(segment -> outputPage.addSegment(segment));

        ct.writeReport(outputPage);
    }

    public CalculateTotals() {
        LOGGER.info("Processing all transactions to calculate totals.");
        getRows();
        processAllTransactions();
        LOGGER.info(
                String.format("Completed processing all transactions, total rows processed: %d", totalRowsProcessed));
    }

    /**
     * Get all of the rows from the database.
     * 
     * !!!!!!!! There is a date hard coded in the TableRow class that limits the rows returned. !!!!!!!!
     */
    private void getRows() {
        // Get all of the rows
        DbHelper dbHelper = new DbHelper();
        allRows = TableRow.getAllRows(dbHelper);
        dbHelper.close();
    }

    /**
     * Store tha page in a file.
     * 
     * @param outputPage
     */
    private void writeReport(Page outputPage) {
        LOGGER.info("Writing HTML output to file: {}", HTMLFILE);
        Tools.writeHtmlOutput(HTMLFILE, outputPage.getPage());
        LOGGER.info("Processed {} out of {} rows.", totalRowsProcessed, allRows.size());

    }

    /**
     * Returns a String object that contains the summary of closing positions plus a
     * table per asset for which there are trades.
     * 
     * @return String
     */
    private void processAllTransactions() {

        // Process all of the trades
        for (TableRow tableRow : allRows) {
            // Flag the asset as processed
            tableRow.setNpProcessed(true);
            totalRowsProcessed++;

            String transactionType = tableRow.getTransactionType();

            // Depending on the transaction type, process accordingly
            switch (transactionType) {
                case "Buy", "Sell", "Convert", "Staking Income", "Unwrap":
                    processTrade(tableRow);
                    break;
                case "Deposit":
                    processDeposit(tableRow);
                    break;
                default:
                    processOther(tableRow);
                    break;
            }
        }
    }

    private void processTrade(TableRow tableRow) {
        String transactionType = tableRow.getTransactionType();

        List<TableRow> transactionsForAsset = null;

        // If the quantity is 0, then add to other table and return
        if (Tools.isDoubleZero(tableRow.getQuantityTransacted())) {
            processOther(tableRow);
            return;
        }

        // If we already have trades for this asset add them to the list, if not create
        // the list.
        if (trades.containsKey(tableRow.getAsset())) {
            transactionsForAsset = trades.get(tableRow.getAsset());
        } else {
            transactionsForAsset = new ArrayList<>();
            trades.put(tableRow.getAsset(), transactionsForAsset);
        }

        // Set "Buy" transactions to have negative cost
        if (transactionType.equals("Buy")) {
            tableRow.setSubtotal(-Math.abs(tableRow.getSubtotal()));
            tableRow.setTotal(-Math.abs(tableRow.getTotal()));
        }

        // Fees are always positive values in the data, but they are a spend so need to
        // be negative
        tableRow.setFeesAndSpreads(-Math.abs(tableRow.getFeesAndSpreads()));

        // We may create a new row, shomewhere to hold it
        TableRow newRow = null;

        // For a "Convert" generate a buy & sell trnasaction with the data
        if (transactionType.equals("Convert")) {
            String[] notes = tableRow.getNotes().split(" ");

            // Create the new table row, then copy over what we want. Fees go to the "Sell"
            // transacion.
             newRow = (TableRow) tableRow.clone();
            newRow.setTransactionType("Convert/Buy");
            String newAsset = notes[5];
            newRow.setAsset(newAsset);
            newRow.setQuantityTransacted(Double.parseDouble(notes[4]));
            newRow.setPriceAtTransaction(newRow.getSubtotal() / newRow.getQuantityTransacted());
            newRow.setFeesAndSpreads(0f);
            newRow.setSubtotal(-Math.abs(newRow.getSubtotal()));
            newRow.setTotal(-Math.abs(newRow.getTotal()));

            // Update the original transaction to be a "Sell"
            transactionType = "Convert/Sell";
            tableRow.setTransactionType(transactionType);

            tableRow.setTotal(tableRow.getSubtotal() + tableRow.getFeesAndSpreads());

            // Store the new Asset
            if (trades.containsKey(newAsset)) {
                trades.get(newAsset).add(newRow);
            } else {
                ArrayList<TableRow> newList = new ArrayList<>();
                newList.add(newRow);
                trades.put(newAsset, newList);
            }

        }

        // Update the counters
        if (transactionType.equals("Convert/Sell") || transactionType.equals("Sell")  || transactionType.equals("Unwrap")) {
            tradeTotals.incrementNumSellTrades();
            tradeTotals.addToTotalSellGbp(tableRow.getSubtotal());
            tradeTotals.addFees(-tableRow.getFeesAndSpreads());
        }
        if (transactionType.equals("Buy")) {
            // All buys are -ve, so correct that
            tradeTotals.addToTotalBuyGbp(-tableRow.getSubtotal());
            tradeTotals.addFees(-tableRow.getFeesAndSpreads());
        }
        if (newRow!=null && newRow.getTransactionType().equals("Convert/Buy")) {
            // All buys are -ve, so correct that
            tradeTotals.addToTotalBuyGbp(-newRow.getSubtotal());
        }

        // For Staking Income, zero out most of the fields
        if (transactionType.equals("Staking Income")) {
            tableRow.setPriceAtTransaction(0f);
            tableRow.setSubtotal(0f);
            tableRow.setTotal(0f);
            tableRow.setFeesAndSpreads(0f);
        }

        transactionsForAsset.add(tableRow);

    }

    private void processDeposit(TableRow tableRow) {

        cashTransactions.add(tableRow);

        cashTotals.addDeposit(tableRow.getTotal());
        cashTotals.addFees(tableRow.getFeesAndSpreads());
    }

    private void processOther(TableRow tableRow) {
        otherTransactions.add(tableRow);
    }

    /**
     * Returns a String object that contains the summary of closing positions plus a
     * table per asset for which there are trades.
     * 
     * @return String
     */
    private String printTrades() {
        StringBuilder htmlTtradesDetail = new StringBuilder();
        StringBuilder htmlTradesSummary = new StringBuilder();

        BuySellConvertTransactions bsst = new BuySellConvertTransactions();

        htmlTradesSummary.append(bsst.tableHead());

        // Sort the keys by asset name first
        ArrayList<String> sortedKeys = new ArrayList<>(trades.keySet());
        Collections.sort(sortedKeys);

        // Process items for each asset class
        for (String asset : sortedKeys) {
            htmlTtradesDetail.append(bsst.assetTableHead(asset));

            // Totals for an asset
            double assetQtyHeld = 0f;
            double assetTotalPaid = 0f;
            double assetAvgCost = 0f;
            double assetFees = 0f;
            double assetProffit = 0f;
            double assetPurchaseCost = 0f;

            // Process the trades
            for (TableRow trade : trades.get(asset)) {
                double lineProfit = 0f;
                String tradeType = trade.getTransactionType();
                if (tradeType.equals("Buy") || tradeType.equals("Convert/Buy")) {
                    // Buy transaction
                    assetQtyHeld += trade.getQuantityTransacted();
                    assetTotalPaid += trade.getSubtotal();
                    assetAvgCost = -assetTotalPaid / assetQtyHeld;
                    assetFees += trade.getFeesAndSpreads();
                    assetPurchaseCost += trade.getSubtotal();
                } else {
                    // Sell transaction
                    double transQty = trade.getQuantityTransacted();
                    lineProfit = trade.getSubtotal() - (-transQty * assetAvgCost);
                    assetQtyHeld += transQty;
                    assetTotalPaid -= assetAvgCost * transQty;
                    // Fees are negative for sell transactions
                    assetFees += trade.getFeesAndSpreads();
                }
                htmlTtradesDetail.append(bsst.assetAddRow(trade, assetAvgCost, lineProfit));
                assetProffit += lineProfit;
            }

            htmlTtradesDetail.append(bsst.assetTableFooter(assetAvgCost));

            // If we hold zero items, set avg cost to zero
            if (Tools.isDoubleZero(assetQtyHeld)) {
                assetAvgCost = 0f;
                assetPurchaseCost = 0f;
            }
            htmlTradesSummary.append(
                    bsst.addRow(asset, Math.abs(assetFees), Tools.round(assetQtyHeld, 3),
                            Math.abs(assetAvgCost), Math.abs(assetQtyHeld * assetAvgCost), assetProffit));

            // Update report totals
            tradeTotals.addProfit(assetProffit);
            tradeTotals.addPurchases(assetPurchaseCost);
            tradeTotals.addFees(Math.abs(assetFees));
            tradeTotals.addToClosingStockValue(Math.abs(assetQtyHeld * assetAvgCost));
        }

        htmlTradesSummary.append(bsst.tableFooter(Math.abs(tradeTotals.getTotalFees()),
                Math.abs(tradeTotals.getClosingStockValue()), tradeTotals.getTotalProfit()));


        return htmlTradesSummary.toString() + htmlTtradesDetail.toString();
    }

    /**
     * @return String
     */
    private String printDepositTransactions() {
        StringBuilder htmlSegment = new StringBuilder();

        DepositTransactions deposits = new DepositTransactions();
        htmlSegment.append(deposits.tableHead());

        for (TableRow tableRow : cashTransactions) {
            // Generate the HTML
            htmlSegment.append(deposits.addRow(tableRow));
        }

        htmlSegment.append(deposits.tableFooter(cashTotals.getTotalFees(), cashTotals.getTotalDeposit()));

        return htmlSegment.toString();
    }

    private String printOtherTransactions() {
        StringBuilder htmlSegment = new StringBuilder();

        OtherTransactions other = new OtherTransactions();
        htmlSegment.append(other.tableHead());

        for (TableRow tableRow : otherTransactions) {
            // Generate the HTML
            htmlSegment.append(other.addRow(tableRow));
        }

        htmlSegment.append(other.tableFooter());

        return htmlSegment.toString();
    }

    private class DepositTotals {
        private double totalFees;
        private double totalDeposit;

        DepositTotals(double totalFees, double totalDeposit) {
            this.totalFees = totalFees;
            this.totalDeposit = totalDeposit;
        }

        private double getTotalFees() {
            return totalFees;
        }

        private double getTotalDeposit() {
            return totalDeposit;
        }

        private void addFees(double fees) {
            totalFees += fees;
        }

        private void addDeposit(double deposit) {
            totalDeposit += deposit;
        }
    }

    public class TradeTotals {
        private double totalFees;
        private double totalPurchases;
        private double totalProfit;
        private int numSellTrades;
        private double totalBuyGbp;
        private double totalSellGbp;
        private double closingStockValue;
        private double openingStockValue;

        TradeTotals(double totalFees, double totalPurchases, double totalProfit) {
            this.totalFees = totalFees;
            this.totalPurchases = totalPurchases;
            this.totalProfit = totalProfit;
            numSellTrades = 0;
            totalBuyGbp = 0;
            totalSellGbp = 0;
            closingStockValue = 0;
            openingStockValue = 0;
        }

        public double getTotalFees() {
            return totalFees;
        }

        public double getTotalPurchases() {
            return totalPurchases;
        }

        public double getTotalProfit() {
            return totalProfit;
        }

        public void addFees(double fees) {
            totalFees += fees;
        }

        public void addPurchases(double purchases) {
            totalPurchases += purchases;
        }

        public void addProfit(double profit) {
            totalProfit += profit;
        }

        public void incrementNumSellTrades() {
            numSellTrades++;
        }

        public int getNumSellTrades() {
            return numSellTrades;
        }

        public void addToTotalBuyGbp(double amount) {
            totalBuyGbp += amount;
        }

        public double getTotalBuyGbp() {
            return totalBuyGbp;
        }

        public double getTotalSellGbp() {
            return totalSellGbp;
        }

        public double getClosingStockValue() {
            return closingStockValue;
        }

        public double getOpeningStockValue() {
            return openingStockValue;
        }

        public void addToTotalSellGbp(double amount) {
            totalSellGbp += amount;
        }

        public void addToClosingStockValue(double amount) {
            closingStockValue += amount;
        }

        public void addToOpeningStockValue(double amount) {
            openingStockValue += amount;
        }
    }
}
