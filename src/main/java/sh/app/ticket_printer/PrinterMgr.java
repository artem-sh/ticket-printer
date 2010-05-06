package sh.app.ticket_printer;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;

public class PrinterMgr {

    public static boolean checkPrintersAvailable() {
        PrintService[] services = java.awt.print.PrinterJob.lookupPrintServices();
        if (services == null) {
            return false;
        }

        for (PrintService ps : services) {
            if (isPrinterServiceReady(ps)) {
                return true;
            }
        }

        return false;
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
}