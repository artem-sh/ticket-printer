package sh.app.ticket_printer.ticket;

import static java.awt.print.PageFormat.LANDSCAPE;
import static java.awt.print.PageFormat.PORTRAIT;
import static java.awt.print.PageFormat.REVERSE_LANDSCAPE;
import static java.lang.Math.round;
import static sh.app.ticket_printer.util.PrimitiveTypeExctractor.asFloatSafely;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;

import sh.app.ticket_printer.PrinterApplet;
import sh.app.ticket_printer.exception.IncorrectTicketDescriptionException;
import sh.app.ticket_printer.ticket.model.Form;

public class TicketPrinter implements Printable {
    
    public static final float MM_TO_POINT = 72f / 25.4f;
    public static final float UNIT_TO_POINT = 0.1f * MM_TO_POINT;
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

    public static void printTicket(Ticket ticket) throws PrinterException, IncorrectTicketDescriptionException {
        if (PrinterApplet.isLogEnabled()) {
            System.out.println("Entering TicketPrinter.printTicket()");
        }
        
        PrinterJob printJob = PrinterJob.getPrinterJob();
        PageFormat format = printJob.defaultPage();
        
        preparePageFormat(ticket.getForm(), format);
        if (PrinterApplet.isLogEnabled()) {
            System.out.println("DEBUG: prepared page format is:");
            printPageFormatInfo(format);
        }
        
        if (TicketValidator.checkPaperSizePresentedInTicket(ticket.getForm())) {
            format = printJob.validatePage(format);
            if (PrinterApplet.isLogEnabled()) {
                System.out.println("Page format after validating by current print job:");
                printPageFormatInfo(format);
            }
        }
        
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
        
        if (PrinterApplet.isLogEnabled()) {
            System.out.println("DEBUG: real page format is: ");
            printPageFormatInfo(pageFormat);
        }
        
        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        
        TicketRender.render(currentTicket, g2d);
        
        return Printable.PAGE_EXISTS;
    }
    
    private static void preparePageFormat(Form form, PageFormat defaultFormat) {
        Paper paper = defaultFormat.getPaper();
        double paperWidth, paperHeight;
        float paddingLeft = asFloatSafely(form.getPaddingLeft()) * MM_TO_POINT;
        float paddingTop = asFloatSafely(form.getPaddingTop()) * MM_TO_POINT;

        switch (form.getPaperOrientation()) {
        case LANDSCAPE:
            paperWidth = (form.getPaperWidth() != null) ? form.getPaperWidth().floatValue() * MM_TO_POINT : paper.getHeight();
            paperHeight = (form.getPaperHeight() != null) ? form.getPaperHeight().floatValue() * MM_TO_POINT : paper.getWidth();
            defaultFormat.setOrientation(PageFormat.LANDSCAPE);
            paper.setSize(paperHeight, paperWidth);
            paper.setImageableArea(
                    paddingTop,
                    0,
                    form.getHeight().floatValue() * UNIT_TO_POINT,
                    paperWidth - paddingLeft);
            break;
        case PORTRAIT:
        default:
            paperWidth = (form.getPaperWidth() != null) ? form.getPaperWidth().floatValue() * MM_TO_POINT : paper.getWidth();
            paperHeight = (form.getPaperHeight() != null) ? form.getPaperHeight().floatValue() * MM_TO_POINT : paper.getHeight();
            defaultFormat.setOrientation(PageFormat.PORTRAIT);
            paper.setSize(paperWidth, paperHeight);
            paper.setImageableArea(
                    paddingLeft,
                    paddingTop,
                    form.getWidth().floatValue() * UNIT_TO_POINT,
                    form.getHeight().floatValue() * UNIT_TO_POINT);
            break;
        }

        defaultFormat.setPaper(paper);
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
    
    private static void printPageFormatInfo(PageFormat pageFormat) {
        PageFormat pf = pageFormat; 
        
        String orientationStr;
        switch (pageFormat.getOrientation()) {
        case LANDSCAPE:
            orientationStr = "LANDSCAPE";
            break;
        case PORTRAIT:
            orientationStr = "PORTRAIT";
            break;
        case REVERSE_LANDSCAPE:
            orientationStr = "REVERSE_LANDSCAPE";
            break;
        default:
            orientationStr = "UNKNOWN!";
            break;
        }
        
        System.out.println("Page format toString(), all data specified in mm, for " + orientationStr + " page orientation. " +
        		"Below 'width' is measured horizontally, 'height' - vertically.");
        System.out.println("page width: " + round(pf.getWidth() / MM_TO_POINT) +
                           ", page height: " + round(pf.getHeight() / MM_TO_POINT) +
                           ", imageable X: " + round(pf.getImageableX() / MM_TO_POINT) +
                           ", imageable Y: " + round(pf.getImageableY() / MM_TO_POINT) +
                           ", imageable width: " + round(pf.getImageableWidth() / MM_TO_POINT) +
                           ", imageable height: " + round(pf.getImageableHeight() / MM_TO_POINT));
    }
}