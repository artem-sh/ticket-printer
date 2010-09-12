package sh.app.ticket_printer.ticket;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;

import sh.app.ticket_printer.PrinterApplet;

public class TicketPrinter implements Printable {
    
    private static final float transformMmToPoint = 72f / 25.4f;
    private Ticket currentTicket;

    private TicketPrinter(Ticket currentTicket) {
        this.currentTicket = currentTicket;
    }

    public static boolean checkPrinterReady() {
        if (PrinterApplet.isLogEnabled()) {
            System.out.println("Entering TicketPrinter.checkPrinterReady()");
        }

        boolean isPrinterReady = false;
        try {
            isPrinterReady = checkDefaultPrinterAvailable();
            if (!isPrinterReady) {
                if (PrinterApplet.isLogEnabled()) {
                    System.err.println("WARNING: no default printer found");
                }

                isPrinterReady = checkPrintersAvailable();
                if (!isPrinterReady) {
                    if (PrinterApplet.isLogEnabled()) {
                        System.err.println("WARNING: no printer found");
                    }
                }
            }
        } catch (Throwable e) {
            if (PrinterApplet.isLogEnabled()) {
                System.err.println("Error while checking if printer is ready: ");
                e.printStackTrace();
            }
        }

        return isPrinterReady;
    }

    public static void printTicket(Ticket ticket) throws PrinterException {
        if (PrinterApplet.isLogEnabled()) {
            System.out.println("Entering TicketPrinter.printTicket()");
        }

        PageFormat format = new PageFormat();
        Paper paper = new Paper();
        paper.setImageableArea(0, 0, Double.MAX_VALUE, Double.MAX_VALUE);
        paper.setSize(transformMmToPoint * ticket.getForm().getPaperWidth(),
                transformMmToPoint * ticket.getForm().getPaperHeight());
        format.setPaper(paper);
 
        PrinterJob printJob = PrinterJob.getPrinterJob();
//        PrintService defaultPs = PrintServiceLookup.lookupDefaultPrintService();
//        defaultPs.getSupportedDocFlavors();
//        DocPrintJob pj = defaultPs.createPrintJob();
//        TicketPrinter ticketPrinter = new TicketPrinter(ticket);
//        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//        aset.add(OrientationRequested.LANDSCAPE);
//        try {
//            pj.print(new PrintableDoc(ticketPrinter), aset);
//        } catch (PrintException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        format = printJob.validatePage(format);
        printJob.setPrintable(new TicketPrinter(ticket), format);
        printJob.print();
    }

    /*
     * WARN: method is called twice (see javadoc or
     * http://docstore.mik.ua/orelly/java-ent/jfc/ch05_02.htm for details)
     */
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

    private static boolean checkPrintersAvailable() {
        boolean isAnyPrinterAvailable = false;

        PrintService[] services = java.awt.print.PrinterJob.lookupPrintServices();
        if (services == null) {
            return isAnyPrinterAvailable;
        }

        for (PrintService ps : services) {
            if (isPrinterServiceReady(ps)) {
                isAnyPrinterAvailable = true;
                break;
            }
        }

        return isAnyPrinterAvailable;
    }

    private static boolean checkDefaultPrinterAvailable() {
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

//    private static class PrintableDoc implements Doc {
//        private Printable printable;
//
//        public PrintableDoc(Printable printable) {
//            this.printable = printable;
//        }
//
//        public DocFlavor getDocFlavor() {
//            return DocFlavor.SERVICE_FORMATTED.PRINTABLE;
//        }
//
//        public DocAttributeSet getAttributes() {
//            return null;
//        }
//
//        public Object getPrintData() throws IOException {
//            return printable;
//        }
//
//        public Reader getReaderForText() throws IOException {
//            return null;
//        }
//
//        public InputStream getStreamForBytes() throws IOException {
//            return null;
//        }
//    }
}