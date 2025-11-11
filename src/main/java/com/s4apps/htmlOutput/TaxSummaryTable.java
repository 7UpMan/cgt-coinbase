package com.s4apps.htmlOutput;

import com.s4apps.CalculateTotals.TradeTotals;
import com.s4apps.Tools;

/**
 *
 * @author mat
 */
public class TaxSummaryTable {

    double totalFees = 0;
    double totalProfit = 0;
    int numSellTrades = 0;
    double totalBuyGbp = 0;
    double totalSellGbp = 0;
    double totalPurchaseCost = 0;
    double openingStockValue = 0;

    // Create the output report
    /**
     * 
     * @param totalFees The total of all the fees incurred.
     * @param totalProfit
     * @param numSellTrades The number of sell trades.
     * @param totalBuyGbp Total amount spent buying stocks ex fees.
     * @param totalSellGbp Total amount spent selling stocks ex fees.
     * @param closingStockValue The price spent on stock that is carried forward to next year.
     * @param openingStockValue The price spent on stock that was brought into this year.
     */
    public TaxSummaryTable(double totalFees, double totalProfit, int numSellTrades,
            double totalBuyGbp, double totalSellGbp, double closingStockValue, double openingStockValue) {
        this.totalFees = totalFees;
        this.totalProfit = totalProfit;
        this.numSellTrades = numSellTrades;
        this.totalBuyGbp = totalBuyGbp;
        this.totalSellGbp = totalSellGbp;
        this.totalPurchaseCost = closingStockValue;
        this.openingStockValue = openingStockValue;
    }

    public TaxSummaryTable(TradeTotals tradeTotals) {
        this.totalFees = tradeTotals.getTotalFees();
        this.totalProfit = tradeTotals.getTotalProfit();
        this.numSellTrades = tradeTotals.getNumSellTrades();
        this.totalBuyGbp = tradeTotals.getTotalBuyGbp();
        this.totalSellGbp = tradeTotals.getTotalSellGbp();
        this.totalPurchaseCost = tradeTotals.getClosingStockValue();
        this.openingStockValue = tradeTotals.getOpeningStockValue();
    }

    public String getTable() {
        StringBuilder sb = new StringBuilder();
        sb.append(tableHead());
        sb.append(tableBody());
        sb.append(tableFooter());

        return sb.toString();
    }

    private String tableBody() {
        StringBuilder sb = new StringBuilder();

        //double allowableCosts = totalBuyGbp - totalPurchaseCost + openingStockValue + totalFees;
        double allowableCosts = totalBuyGbp - totalPurchaseCost + totalFees;
        double gainsForYear = totalSellGbp - allowableCosts;

        sb.append(tableRow("Opening Stock Value", String.format(""+Tools.CURR+"%,.2f", openingStockValue)));
        sb.append(tableRow("Closing Stock Value", String.format(""+Tools.CURR+"%,.2f", totalPurchaseCost)));
        sb.append(tableRow("Total Buy Transactions", String.format(""+Tools.CURR+"%,.2f", totalBuyGbp)));
        sb.append(tableRow("Total Sell Transactions", String.format(""+Tools.CURR+"%,.2f", totalSellGbp)));
        sb.append(tableRow("Fees", String.format(""+Tools.CURR+"%,.2f", totalFees)));
        
        sb.append(tableRow("&nbsp;", "&nbsp;"));
        
        sb.append(tableRow("Number of disposals", String.format("%,d", numSellTrades)));
        sb.append(tableRow("Disposal proceeds", String.format(""+Tools.CURR+"%,.2f", totalSellGbp)));
        
        sb.append(tableRow("Allowable costs = Total Buy Transactions - Closing Stock Value + Fees",
                String.format(""+Tools.CURR+"%,.2f", allowableCosts)));

        sb.append(tableRow("Gains for the year = Total Sell Transactions - Allowable Costs", String.format(""+Tools.CURR+"%,.2f", gainsForYear)));
        
        
        
        sb.append(tableRow(String.format("Gain got the year = TotalSell "+Tools.CURR+"%,.2f - "
                + "TotalBuy "+Tools.CURR+"%,.2f + Closing Stock "+Tools.CURR+"%,.2f - Fees "+Tools.CURR+"%,.2f", 
                totalSellGbp, totalBuyGbp,totalPurchaseCost,totalFees)
                , String.format(""+Tools.CURR+"%,.2f", totalSellGbp-totalBuyGbp+totalPurchaseCost-totalFees)));
        
        

        return sb.toString();
    }

    private StringBuilder tableRow(String name, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("     <tr>\n");
        sb.append("      <td>");
        sb.append(name);
        sb.append("</td>\n");
        sb.append("      <td class=\"text-right\">");
        sb.append(value);
        sb.append("</td>\n");
        sb.append("     </tr>\n");

        return sb;
    }

    private String tableHead() {
        StringBuilder sb = new StringBuilder();

        sb.append("<h1 id=\"tax-summary\">Tax Summary</h1>\n");
        sb.append("   <table class=\"table table-bordered table-sm table-dark\">\n");
        sb.append("    <thead>\n");
        sb.append("     <tr>\n");
        sb.append("      <th>Description</th>\n");
        sb.append("      <th>Value</th>\n");
        sb.append("     </tr>\n");
        sb.append("    </thead>\n");
        sb.append("    <tbody>\n");

        return sb.toString();
    }

    private String tableFooter() {
        StringBuilder sb = new StringBuilder();

        sb.append("    </tbody>\n");
        sb.append("   </table>\n");

        return sb.toString();
    }
}
