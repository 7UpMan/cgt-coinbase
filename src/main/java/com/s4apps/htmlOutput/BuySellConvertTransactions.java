package com.s4apps.htmlOutput;

import java.time.format.DateTimeFormatter;

import com.s4apps.TableRow;
import com.s4apps.Tools;

public class BuySellConvertTransactions {
    private double totalQuantity = 0d;
    private double totalFees = 0d;
    private double totalLineProfit = 0d;

    public BuySellConvertTransactions() {
        // Empty constructor
    }

    

    public String addRow(String assetName, double totalFees, double totalHeld, double avgCost, double purchaseCost, double profitExFees) {
        StringBuilder sb = new StringBuilder();

        sb.append("    <tr>\n");

        sb.append("     <td><a href=\"#asset-"+assetName+"\">");
        sb.append(assetName);
        sb.append("</a></td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",totalFees));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("%,.2f",totalHeld));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.4f",avgCost));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",purchaseCost));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",profitExFees));
        sb.append("</td>\n");

        sb.append("    </tr>\n");
        return sb.toString();
    }

    public String tableHead() {
        StringBuilder sb = new StringBuilder();

        sb.append("<h1 id=\"closing-positions\">Transaction Summary</h1>\n");
        sb.append("   <table class=\"table table-bordered table-sm table-dark\">\n");
        sb.append("    <thead>\n");
        sb.append("     <tr>\n");
        sb.append("      <th>Asset Name</th>\n");
        sb.append("      <th>Total Fees</th>\n");
        sb.append("      <th>Total Held</th>\n");
        sb.append("      <th>Avg Cost</th>\n");
        sb.append("      <th>Purchase Cost</th>\n");
        sb.append("      <th>Profilt Ex Fees</th>\n");
        sb.append("     </tr>\n");
        sb.append("    </thead>\n");
        sb.append("    <tbody>\n");

        return sb.toString();
    }


    public String assetTableHead(String assetName) {
          totalQuantity = 0d;
          totalFees = 0d;
          totalLineProfit = 0d;
    
        StringBuilder sb = new StringBuilder();

        sb.append("<h2 id=\"asset-"+assetName+"\">Transactions for "+assetName+"</h2>\n");
        sb.append("   <table class=\"table table-bordered table-sm table-dark\">\n");
        sb.append("    <thead>\n");
        sb.append("     <tr>\n");
        sb.append("      <th>Date</th>\n");
        sb.append("      <th>Buy/Sell</th>\n");
        sb.append("      <th>Currency</th>\n");
        sb.append("      <th>Quantity</th>\n");
        sb.append("      <th>Unit Cost</th>\n");
        sb.append("      <th>Total</th>\n");
        sb.append("      <th>Fees</th>\n");
        sb.append("      <th>Total inc Fees</th>\n");
        sb.append("      <th>Avg Cost</th>\n");
        sb.append("      <th>Line Profit</th>\n");

        sb.append("     </tr>\n");
        sb.append("    </thead>\n");
        sb.append("    <tbody>\n");

        return sb.toString();
    }

    public String assetAddRow(TableRow row, double avgCost, double lineProfit) {
        if (row.getTransactionType().equals("Sell") || row.getTransactionType().equals("Convert/Sell")) {
            totalQuantity  += row.getQuantityTransacted();
            totalFees += row.getFeesAndSpreads();
        } else {
            totalQuantity  += row.getQuantityTransacted();
            totalFees += row.getFeesAndSpreads();
        }
        totalLineProfit += lineProfit;


        StringBuilder sb = new StringBuilder();

        sb.append("    <tr>\n");

        sb.append("     <td>");
        sb.append(row.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sb.append("</td>\n");

        sb.append("     <td>");
        sb.append(row.getTransactionType());
        sb.append("</td>\n");

        sb.append("     <td>");
        sb.append(row.getPriceCurrency());
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("%,.4f",row.getQuantityTransacted()));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.4f",row.getPriceAtTransaction()));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",row.getSubtotal()));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",row.getFeesAndSpreads()));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",row.getTotal()));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",avgCost));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",lineProfit));
        sb.append("</td>\n");

        sb.append("    </tr>\n");
        return sb.toString();
    }

    public String assetTableFooter(double avgCost) {
        StringBuilder sb = new StringBuilder();

        sb.append("    </tbody>\n");
        sb.append("    <tfoot>\n");
        sb.append("    <tr>\n");

        sb.append("     <td>Total</td>\n");
        sb.append("     <td>&nbsp;</td>\n");
        sb.append("     <td>&nbsp;</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("%,.4f",Tools.round(totalQuantity,3)));
        sb.append("</td>\n");

        sb.append("     <td>&nbsp;</td>\n");
        sb.append("     <td>&nbsp;</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",totalFees));
        sb.append("</td>\n");

        sb.append("     <td>&nbsp;</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",avgCost));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",totalLineProfit));
        sb.append("</td>\n");
        
        sb.append("    </tr>\n");
        sb.append("    </tfoot>\n");

        sb.append("   </table>\n");

        return sb.toString();
    }


    public String tableFooter(double feesTotal, double purchaseCost, double totalProfit) {
        StringBuilder sb = new StringBuilder();

        sb.append("    <tr>\n");

        sb.append("     <td>Total</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",feesTotal));
        sb.append("</td>\n");
        
        sb.append("     <td>&nbsp;</td>\n");
        sb.append("     <td>&nbsp;</td>\n");
        
        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",purchaseCost));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",totalProfit));
        sb.append("</td>\n");

        sb.append("    </tr>\n");

        sb.append("    </tbody>\n");
        sb.append("   </table>\n");

        // Ready for the trnasaction detail
        sb.append("<h1 id=\"transaction-detail\">Transaction Detail</h1>\n");

        return sb.toString();
    }
}
