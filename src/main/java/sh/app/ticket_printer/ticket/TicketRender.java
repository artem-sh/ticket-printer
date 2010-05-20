package sh.app.ticket_printer.ticket;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.security.InvalidParameterException;

import sh.app.ticket_printer.ticket.model.AbstractTicketAttribute;
import sh.app.ticket_printer.ticket.model.Barcode;
import sh.app.ticket_printer.ticket.model.Form;
import sh.app.ticket_printer.ticket.model.Image;
import sh.app.ticket_printer.ticket.model.Text;
import sh.app.ticket_printer.ticket.model.TicketPart;

public class TicketRender {

    public static void render(Ticket ticket, Graphics2D g) {
        renderTicketPart(ticket.getForm(), g);

        for (AbstractTicketAttribute attr : ticket.getElemets()) {
            renderTicketPart(attr, g);
        }
    }

    private static void renderTicketPart(TicketPart part, Graphics2D g) {
        switch (part.getType()) {
        case FORM:
            renderForm((Form) part, g);
            break;
        case TEXT:
            renderText((Text) part, g);
            break;
        case IMAGE:
            renderImage((Image) part, g);
            break;
        case BARCODE:
            renderBarcode((Barcode) part, g);
            break;
        default:
            throw new InvalidParameterException("Unknown ticket part type");
        }
    }

    private static void renderForm(Form form, Graphics2D g) {
        g.draw(new Rectangle2D.Double(0, 0, form.getWidth(), form.getHeight()));
    }

    private static void renderBarcode(Barcode barcode, Graphics2D g) {
    }

    private static void renderImage(Image image, Graphics2D g) {
    }

    private static void renderText(Text text, Graphics2D g) {
    }
}