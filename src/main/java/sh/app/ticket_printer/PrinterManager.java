package sh.app.ticket_printer;

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

import sh.app.ticket_printer.ticket.Ticket;
import sh.app.ticket_printer.ticket.TicketRender;

public class PrinterManager implements Printable {
	
	private static Boolean isAnyPrinterAvailable;
    
    private Ticket currentTicket;
    
    private PrinterManager(Ticket currentTicket) {
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
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new PrinterManager(ticket));
        printJob.print();
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        TicketRender.render(currentTicket, (Graphics2D) graphics);
        
        return Printable.PAGE_EXISTS;
    }
    
}