package sh.app.ticket_printer.ticket;

import static sh.app.ticket_printer.util.PrimitiveTypeExctractor.asFloatSafely;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import sh.app.ticket_printer.PrinterApplet;
import sh.app.ticket_printer.exception.IncorrectTicketDescriptionException;
import sh.app.ticket_printer.ticket.model.Form;


public class TicketValidator {
    static final float UNIT_TO_MM = 0.1f;
    static final Set<String> fontFamilies;
    
    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontFamilies = new HashSet<String>(Arrays.asList(ge.getAvailableFontFamilyNames()));
    }
    

    public static void validateFormElement(Form form) throws IncorrectTicketDescriptionException {
        boolean paperSizePresented = checkPaperSizePresentedInTicket(form);
        boolean paperPaddingPresented = checkPaperPaddingPresentedInTicket(form);
        if (paperPaddingPresented && !paperSizePresented) {
            throw new IncorrectTicketDescriptionException("Values for both paper-width and paper-height should be specified to render paper padding");
        }
        if (!paperSizePresented) {
            return;
        }
        
        float paperWidth = asFloatSafely(form.getPaperWidth());
        float paperHeight = asFloatSafely(form.getPaperHeight());
        float formWidth = asFloatSafely(form.getWidth()) * UNIT_TO_MM;
        float formHeight = asFloatSafely(form.getHeight()) * UNIT_TO_MM;
        if (paperWidth < formWidth) {
            throw new IncorrectTicketDescriptionException("Ticket width is bigger than specified paper width");
        }
        if (paperHeight < formHeight) {
            throw new IncorrectTicketDescriptionException("Ticket height is bigger than specified paper height");
        }
        
        if (paperPaddingPresented) {
            float paddingTop = asFloatSafely(form.getPaddingTop());
            float paddingRight = asFloatSafely(form.getPaddingRight());
            float paddingLeft = asFloatSafely(form.getPaddingLeft());
            float paddingBottom = asFloatSafely(form.getPaddingBottom());
            float demandedImageablePaperWidth = paperWidth - paddingLeft - paddingRight;
            float demandedImageablePaperHeight = paperHeight - paddingTop - paddingBottom;
            if (demandedImageablePaperWidth < formWidth) {
                throw new IncorrectTicketDescriptionException("Form width is bigger then paper width available for printing");
            }
            if (demandedImageablePaperHeight < formHeight) {
                throw new IncorrectTicketDescriptionException("Form height is bigger then paper height available for printing");
            }
        }
    }
    
    static boolean checkPaperSizePresentedInTicket(Form form) {
        if ((form.getPaperWidth() != null) && (form.getPaperHeight() != null)) {
            return true;
        }
        return false;
    }

    static boolean checkPaperPaddingPresentedInTicket(Form form) {
        if ((form.getPaddingTop() != null) || (form.getPaddingRight() != null) || (form.getPaddingBottom() != null)
                || (form.getPaddingLeft() != null)) {
            return true;
        }
        return false;
    }

    static String checkIfFontAvailable(String fontFamily) {
        if (fontFamilies.contains(fontFamily)) {
            return fontFamily;
        }
    
        if (PrinterApplet.isLogEnabled()) {
            System.out.println("WARNING: can't find desired dont " + fontFamily + " in OS. Use default instead.");
        }
        return Font.DIALOG;
    }
}