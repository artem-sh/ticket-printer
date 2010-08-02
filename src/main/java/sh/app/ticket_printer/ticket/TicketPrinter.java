package sh.app.ticket_printer.ticket;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;

import sh.app.ticket_printer.PrinterApplet;


public class TicketPrinter implements Printable {
	
	private static Boolean isAnyPrinterAvailable;
    private Ticket currentTicket;
    
    private TicketPrinter(Ticket currentTicket) {
        this.currentTicket = currentTicket;
    }
    
    public static boolean checkPrintersAvailable() {
    	if (isAnyPrinterAvailable != null) {
    		return isAnyPrinterAvailable;
    	}
    	
    	isAnyPrinterAvailable = Boolean.FALSE;
    	
        PrintService[] services = java.awt.print.PrinterJob.lookupPrintServices();
        if (services == null) {
            return isAnyPrinterAvailable;
        }

        for (PrintService ps : services) {
            if (isPrinterServiceReady(ps)) {
            	isAnyPrinterAvailable = Boolean.TRUE;
            	break;
            }
        }

        return isAnyPrinterAvailable;
    }

    public static boolean checkDefaultPrinterAvailable() {
        return isPrinterServiceReady(PrintServiceLookup.lookupDefaultPrintService());
    }

    private static boolean isPrinterServiceReady(PrintService ps) {
        if (ps == null) {
            return false;
        }

        Attribute[] attributes = ps.getAttributes().toArray();
        if (attributes == null) {
            return false;
        }

        for (Attribute a : attributes) {
            if (a.equals(PrinterIsAcceptingJobs.ACCEPTING_JOBS)) {
                return true;
            }
        }

        return false;
    }

    public static void printTicket(Ticket ticket) throws PrinterException {
    	if (PrinterApplet.isLogEnabled()) {
            System.out.println("Entering TicketPrinter.printTicket()");
        }
    	
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new TicketPrinter(ticket));
        printJob.print();
    }
    
    /* WARN: method is called twice (see javadoc or
    http://docstore.mik.ua/orelly/java-ent/jfc/ch05_02.htm
    for details) */
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
    	if (PrinterApplet.isLogEnabled()) {
            System.out.println("Entering TicketPrinter.print()");
        }
    	
        if (pageIndex != 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        
        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        
        TicketRender.render(currentTicket, g2d);
        
        return Printable.PAGE_EXISTS;
    }
}