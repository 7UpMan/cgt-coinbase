package com.s4apps.htmlOutput;

import java.time.format.DateTimeFormatter;

import com.s4apps.TableRow;

public class StakingIncomeTransactions {

    public StakingIncomeTransactions() {


    }

    

    public String addRow(TableRow row) {
        StringBuilder sb = new StringBuilder();

        sb.append("    <tr>\n");

        sb.append("     <td>");
        sb.append(row.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sb.append("</td>\n");

        sb.append("     <td>");
        sb.append(row.getTransactionType());
        sb.append("</td>\n");

        sb.append("     <td>");
        sb.append(row.getAsset());
        sb.append("</td>\n");

        sb.append("     <td>");
        sb.append(row.getPriceCurrency());
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("%,.4f",row.getQuantityTransacted()));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",row.getPriceAtTransaction()));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",row.getFeesAndSpreads()));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",row.getTotal()));
        sb.append("</td>\n");

        sb.append("    </tr>\n");
        return sb.toString();
    }

    public String tableHead() {
        StringBuilder sb = new StringBuilder();

        sb.append("<h1 id=\"staking-Income\">Staking Income</h1>\n");
        sb.append("   <table class=\"table table-bordered table-sm table-dark\">\n");
        sb.append("    <thead>\n");
        sb.append("     <tr>\n");
        sb.append("      <th>Date</th>\n");
        sb.append("      <th>Description</th>\n");
        sb.append("      <th>From Currency</th>\n");
        sb.append("      <th>To Currency</th>\n");
        sb.append("      <th>Quantity</th>\n");
        sb.append("      <th>Unit Cost</th>\n");
        sb.append("      <th>Fees</th>\n");
        sb.append("      <th>Total</th>\n");
        sb.append("     </tr>\n");
        sb.append("    </thead>\n");
        sb.append("    <tbody>\n");

        return sb.toString();
    }

    public String tableFooter(double feesTotal, double total) {
        StringBuilder sb = new StringBuilder();

        sb.append("    <tr>\n");

        sb.append("     <td colspan=\"3\">Total</td>\n");
        // sb.append("     <td>&nbsp;</td>\n");
        // sb.append("     <td>&nbsp;</td>\n");
        sb.append("     <td>&nbsp;</td>\n");
        sb.append("     <td>&nbsp;</td>\n");
        sb.append("     <td>&nbsp;</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",feesTotal));
        sb.append("</td>\n");

        sb.append("     <td class=\"text-right\">");
        sb.append(String.format("$%,.2f",total));
        sb.append("</td>\n");

        sb.append("    </tr>\n");

        sb.append("    </tbody>\n");
        sb.append("   </table>\n");

        return sb.toString();
    }
}
