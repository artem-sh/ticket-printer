package sh.app.ticket_printer;

import static sh.app.ticket_printer.AppletVersionManager.compareInternalToExternalVersions;

import java.applet.Applet;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import sh.app.ticket_printer.exception.TicketParserException;
import sh.app.ticket_printer.ticket.Ticket;
import sh.app.ticket_printer.ticket.TicketParser;
import sh.app.ticket_printer.ticket.TicketPrinter;

public class PrinterApplet extends Applet {

    private static final long serialVersionUID = -3097005524395815096L;
    private static final String DEBUG_APPLET_PARAM = "debug";
    private static final String DEBUG_ENABLED_VALUE = "true";
    private static final String VERSION_APPLET_PARAM = "appletVersion";
    private static final int RESULT_SUCCESS = 0;
    private static final int RESULT_UNKNOWN_ERROR = 1;
    private static final int RESULT_PRINTER_IS_UNAVAILABLE = 2;
    private static final int RESULT_INCORRECT_PARAMS = 3;
    private static boolean logEnabled;

    public static boolean isLogEnabled() {
        return logEnabled;
    }
    
    public void init() {
        if (DEBUG_ENABLED_VALUE.equalsIgnoreCase(getParameter(DEBUG_APPLET_PARAM))) {
            logEnabled = true;
        }
        
        if (isLogEnabled()) {
            System.out.println("Entering PrinterApplet.init()");
        }
        
        checkVersions();
    }

    @Override
    public void start() {}

    @Override
    public void paint(Graphics g) {}

    public int sendDataToPrinter(String ticketString) {
        if (isLogEnabled()) {
            System.out.println("Entering PrintApplet.printTicket()");
        }
        
        if (!TicketPrinter.checkPrinterReady()) {
            System.err.println("Can't print ticket cause no available printers has been found, exiting.");
            return RESULT_PRINTER_IS_UNAVAILABLE;
        }

        InputStream is = null;
        try {
        	// TODO: remove later STARTS!
        	if (ticketString == null) {
        	    System.out.println("Print test ticket for debug purpose");
	            String xmlFileName = "/ticket1.xml";
	            is = PrinterApplet.class.getResourceAsStream(xmlFileName);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	            String line = null;
	            StringBuilder sb = new StringBuilder();
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            ticketString = sb.toString();
        	}
        	// TODO: remove later ENDS!

            Ticket ticket = TicketParser.parse(ticketString);
            TicketPrinter.printTicket(ticket);
        } catch (TicketParserException e) {
            printExceptionAndCause(e);
            return RESULT_INCORRECT_PARAMS;
        } catch (Throwable t) {
            printExceptionAndCause(t);
            return RESULT_UNKNOWN_ERROR;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {}
            }
        }
        
        return RESULT_SUCCESS;
    }

    private void checkVersions() {
        if (isLogEnabled()) {
            System.out.println("Entering PrinterApplet.checkVersions()");
        }
        
        try {
            if (compareInternalToExternalVersions(getParameter(VERSION_APPLET_PARAM)) < 0) {
                System.err.println("WARNING: Old applet version has been loaded.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void printExceptionAndCause(Throwable t) {
        t.printStackTrace();
        if (t.getCause() != null) {
            System.err.println("Cause is: ");
            t.getCause().printStackTrace();
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    // TODO: remove in production    
    public static void setLogEnabled() {
        logEnabled = true;
    }
    
    // TODO: remove in production
    public static void main(String[] args) {
        setLogEnabled();
        new PrinterApplet().sendDataToPrinter(null);
    }
}