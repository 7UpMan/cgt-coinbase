package com.s4apps.htmlOutput;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author mat
 */
public class Page {
    private ArrayList<String> segments;

    // Create the output report
    public Page() {
        segments = new ArrayList<>();
    }

    public void addSegment(String s) {
        segments.add(s);
    }

    public String getPage() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(pageTop());
        for(String s: segments) {
            sb.append(s);
        }
        
        sb.append(pageBottom());
        
        return sb.toString();
        
    }


    private String pageTop() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append(" <head>\n");
        sb.append("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sb.append("  <meta charset=\"utf-8\">\n");
        sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
        sb.append("  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">\n");
        sb.append("  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n");
        sb.append("  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js\"></script>\n");
        sb.append("  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js\"></script>\n");
        sb.append("  <title>Trading Summary</title>\n");
        sb.append(" </head>\n");
        sb.append(" <body>\n");
        sb.append("  <div class=\"jumbotron text-center\">\n");
        sb.append("   <h1>Transaction History Report</h1>\n");
        sb.append("  </div>\n");
        sb.append("  <div class=\"container\">\n");
        
        return sb.toString();
    }

    /**
     * Adds the end of a page to the buffer.
     *
     * @param sb
     */
    private String pageBottom() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("  </div><!-- container -->\n");
        sb.append("  <div class=\"jumbotron\" style=\"margin-bottom:0\">");
        sb.append("Generated on ");
        sb.append(new Date().toString());
        sb.append("</div>\n");
        sb.append(" </body>\n");
        sb.append("</html>\n");
        
        return sb.toString();
    }
}
