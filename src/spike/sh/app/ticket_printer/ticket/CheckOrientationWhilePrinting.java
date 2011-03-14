package sh.app.ticket_printer.ticket;

import static java.awt.print.PageFormat.LANDSCAPE;
import static java.awt.print.PageFormat.PORTRAIT;
import static java.awt.print.PageFormat.REVERSE_LANDSCAPE;
import static java.lang.Math.round;
import static sh.app.ticket_printer.ticket.TicketPrinter.MM_TO_POINT;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;

public class CheckOrientationWhilePrinting implements Printable {

    public static void main(String[] args) {
        CheckOrientationWhilePrinting spike = new CheckOrientationWhilePrinting();

        // spike.checkIfPageFormatWorks(); NO!
        // spike.checkIfPrintRequestAttributesWorks(); NO!
//        spike.checkIfPrintRequestAttributesWorks_withDialog(); NO!
    }

    private void checkIfPrintRequestAttributesWorks_withDialog() {
        try {
            PrinterJob pj = PrinterJob.getPrinterJob();
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(OrientationRequested.LANDSCAPE);
            aset.add(new Copies(1));
            pj.setPrintable(this);
            pj.pageDialog(aset);
            if (pj.printDialog(aset)) {
                pj.print(aset);
            }
        } catch (PrinterException pe) {
            System.err.println(pe);
        }
    }

    private void checkIfPrintRequestAttributesWorks() {
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(OrientationRequested.LANDSCAPE);
        aset.add(new Copies(1));
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        try {
            printJob.print(aset);
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    private void checkIfPageFormatWorks() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        PageFormat format = printJob.defaultPage();
        format.setOrientation(PageFormat.LANDSCAPE);
        // format.setOrientation(PageFormat.PORTRAIT);
        try {
            printJob.setPrintable(this, format);
            printJob.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        printPageFormatInfo(pageFormat);
        return pageIndex != 0 ? NO_SUCH_PAGE : Printable.PAGE_EXISTS;
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

        System.out.println("Page format toString(), all data specified in mm, for " + orientationStr
                + " page orientation:");
        System.out.println("page width: " + round(pf.getWidth() / MM_TO_POINT) + ", page height: "
                + round(pf.getHeight() / MM_TO_POINT) + ", imageable X: "
                + round(pf.getImageableX() / MM_TO_POINT) + ", imageable Y: "
                + round(pf.getImageableY() / MM_TO_POINT));
    }
}
