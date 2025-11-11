package com.s4apps.htmlOutput;

/**
 *
 * @author mat
 */
public class ContentsTable {
    
    public static String getTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1 id=\"toc\">Table of Contents</h1>\n");
        sb.append("<table class=\"table table-bordered table-sm table-dark\">\n");
        
        sb.append("<tr><td>");
        sb.append("<a href=\"#tax-summary\">Tax Summary</a>");
        sb.append("</td></tr>\n");
        
        sb.append("<tr><td>");
        sb.append("<a href=\"#cash-transactions\">Cash Transactions</a>");
        sb.append("</td></tr>\n");

        sb.append("<tr><td>");
        sb.append("<a href=\"#closing-positions\">Closing Positions</a>");
        sb.append("</td></tr>\n");

        sb.append("<tr><td>");
        sb.append("<a href=\"#transaction-detail\">Transaction Detail</a>");
        sb.append("</td></tr>\n");
        
        sb.append("<tr><td>");
        sb.append("<a href=\"#remaining-transactions\">Remaining Transactions</a>");
        sb.append("</td></tr>\n");
        
        sb.append("</table>\n");
        
        return sb.toString();
    }
}
