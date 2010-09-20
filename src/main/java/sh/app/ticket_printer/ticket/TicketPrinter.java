package sh.app.ticket_printer.ticket;

import static sh.app.ticket_printer.ticket.TicketParser.checkPaperPaddingPresentedInTicket;
import static sh.app.ticket_printer.ticket.TicketParser.checkPaperSizePresentedInTicket;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.security.InvalidParameterException;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;

import sh.app.ticket_printer.PrinterApplet;
import sh.app.ticket_printer.exception.IncorrectTicketDescriptionException;
import sh.app.ticket_printer.ticket.model.Form;
import sh.app.ticket_printer.ticket.model.Form.PaperOrientation;

public class TicketPrinter implements Printable {
    
    public static final float TRANSFORM_MM_TO_POINT = 72f / 25.4f;
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
        boolean pageFormatChanged = false;
        if (checkPaperSizePresentedInTicket(ticket.getForm())) {
            preparePageFormatForPaperSize(ticket.getForm(), format);
            pageFormatChanged = true;
            
            format = printJob.validatePage(format);
            
            preparePageFormatForPaperPadding(ticket.getForm(), format, printJob);
        }

        PaperOrientation paperOrientation = ticket.getForm().getPaperOrientation();
        if (paperOrientation != null) {
            format.setOrientation(getPageOrientationValue(paperOrientation));
            pageFormatChanged = true;
        }

        if (pageFormatChanged) {
            printJob.setPrintable(new TicketPrinter(ticket), format);
        } else {
            printJob.setPrintable(new TicketPrinter(ticket));
        }
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

    private static void preparePageFormatForPaperSize(Form form, PageFormat format) {
        Paper paper = new Paper();
        double paperWidth = form.getPaperWidth() * TRANSFORM_MM_TO_POINT;
        double paperHeight = form.getPaperHeight() * TRANSFORM_MM_TO_POINT;
        paper.setSize(paperWidth, paperHeight);
        paper.setImageableArea(0, 0, paperWidth, paperHeight);
        format.setPaper(paper);
    }
    
    private static void preparePageFormatForPaperPadding(Form form, PageFormat format, PrinterJob printJob)
            throws IncorrectTicketDescriptionException {
        if (!checkPaperPaddingPresentedInTicket(form)) {
            return;
        }

        Paper paper = format.getPaper();
        boolean pageFormatChanged = false;

        double paddingX = paper.getImageableX();
        if (form.getPaddingLeft() != null) {
            float demandedPaddingX = form.getPaddingLeft() * TRANSFORM_MM_TO_POINT;
            if (demandedPaddingX < paddingX) {
                throw new IncorrectTicketDescriptionException(
                        "Demanded paper left padding is less than physical minimum (set by printer) padding");
            }
            pageFormatChanged = true;
            paddingX = demandedPaddingX;
        }

        double paddingY = paper.getImageableY();
        if (form.getPaddingTop() != null) {
            float demandedPaddingY = form.getPaddingTop() * TRANSFORM_MM_TO_POINT;
            if (demandedPaddingY < paddingY) {
                throw new IncorrectTicketDescriptionException(
                        "Demanded paper top padding is less than physical minimum (set by printer) padding");
            }
            pageFormatChanged = true;
            paddingY = demandedPaddingY;
        }

        double imageableWidth = paper.getImageableWidth() - (paddingX - paper.getImageableX());
        if (form.getPaddingRight() != null) {
            double demandedWidth = (form.getPaperWidth() - form.getPaddingLeft() - form.getPaddingRight())
                    * TRANSFORM_MM_TO_POINT;
            if (demandedWidth > imageableWidth) {
                throw new IncorrectTicketDescriptionException(
                        "Demanded page width is bigger than imageable (printable) page width");
            }
            imageableWidth = demandedWidth;
            pageFormatChanged = true;
        }

        double imageableHeight = paper.getImageableHeight() - (paddingY - paper.getImageableY());
        if (form.getPaddingBottom() != null) {
            double demandedHeight = (form.getPaperHeight() - form.getPaddingTop() - form.getPaddingBottom())
                    * TRANSFORM_MM_TO_POINT;
            if (demandedHeight > imageableHeight) {
                throw new IncorrectTicketDescriptionException(
                        "Demanded page height is bigger than imageable (printable) page height");
            }
            imageableHeight = demandedHeight;
            pageFormatChanged = true;
        }

        if (pageFormatChanged) {
            paper.setImageableArea(paddingX, paddingY, imageableWidth, imageableHeight);
            format.setPaper(paper);
        }
    }
    
    private static int getPageOrientationValue(Form.PaperOrientation orientation) {
        int orientationValue;
        switch (orientation) {
        case LANDSCAPE:
            orientationValue = PageFormat.LANDSCAPE;
            break;
        case PORTRAIT:
            orientationValue = PageFormat.PORTRAIT;
            break;
        default:
            throw new InvalidParameterException("Incorrect paper orientation parameter has been specified");
        }
        
        return orientationValue;
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
}