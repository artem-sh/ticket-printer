package sh.app.ticket_printer;

import static sh.app.ticket_printer.AppletVersionManager.compareInternalToExternalVersions;

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import sh.app.ticket_printer.ticket.Ticket;
import sh.app.ticket_printer.ticket.TicketParser;
import sh.app.ticket_printer.ticket.TicketPrinter;

public class PrinterApplet extends Applet {

    private static final long serialVersionUID = -3097005524395815096L;
    private static final String DEBUG_APPLET_PARAM = "debug";
    private static final String VERSION_APPLET_PARAM = "appletVersion";
    private static final String MAIN_ERROR_MSG = "Ошибка при печати билета";
    private static boolean logEnabled;
    private boolean isPrinterReady;

    final static int maxCharHeight = 15;
    final static int minFontSize = 6;

    final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);

    final static float dash1[] = { 10.0f };
    final static BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);

    public static boolean isLogEnabled() {
        return logEnabled;
    }
    
    public void init() {
        if (getParameter(DEBUG_APPLET_PARAM) != null) {
            logEnabled = true;
        }
        
        if (isLogEnabled()) {
            System.out.println("Entering PrinterApplet.init()");
        }
        
        checkVersions();

        isPrinterReady = TicketPrinter.checkDefaultPrinterAvailable();
        if (!isPrinterReady) {
            if (isLogEnabled()) {
                System.err.println("WARNING: no default printer found");
            }

            isPrinterReady = TicketPrinter.checkPrintersAvailable();
            if (!isPrinterReady) {
                if (isLogEnabled()) {
                    System.err.println("WARNING: no printer found");
                }
            }
        }
    }

    @Override
    public void start() {}

    @Override
    public void paint(Graphics g) {}

    public void printTicket(String ticketString) {
        if (isLogEnabled()) {
            System.out.println("Entering PrintApplet.printTicket()");
        }
        
        if (!isPrinterReady) {
            System.err.println("Can't print ticket cause no available printers has been found, exitnig.");
            return;
        }

        InputStream is = null;
        try {
        	if (!TicketPrinter.checkPrintersAvailable()) {
        		if (isLogEnabled()) {
        			System.err.println("WARNING: no printer found");
        		}
        		
        		JOptionPane.showMessageDialog(null, MAIN_ERROR_MSG
        				+ ". Не найдены установленные принтеры", "Error", JOptionPane.ERROR_MESSAGE);
        		return;
        	}
        	
        	// TODO: remove later!
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

            Ticket ticket = TicketParser.parse(ticketString);
            TicketPrinter.printTicket(ticket);
        } catch (Throwable t) {
            t.printStackTrace();

            if (t.getCause() != null) {
                System.err.println("Cause is: ");
                t.getCause().printStackTrace();
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {}
            }
        }
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
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    // TODO: remove in production    
    public static void setLogEnabled() {
        logEnabled = true;
    }
    
    // TODO: remove in production
    public static void main(String[] args) {
        setLogEnabled();
        new PrinterApplet().printTicket(null);
    }
}