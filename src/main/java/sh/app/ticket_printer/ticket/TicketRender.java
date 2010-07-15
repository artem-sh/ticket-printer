package sh.app.ticket_printer.ticket;

import static sh.app.ticket_printer.PrinterApplet.isLogEnabled;
import static sh.app.ticket_printer.ticket.model.AbstractTicketAttribute.DEFAULT_ROTATION_VALUE;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import sh.app.ticket_printer.ticket.model.AbstractTicketAttribute;
import sh.app.ticket_printer.ticket.model.Barcode;
import sh.app.ticket_printer.ticket.model.Form;
import sh.app.ticket_printer.ticket.model.Image;
import sh.app.ticket_printer.ticket.model.Text;
import sh.app.ticket_printer.ticket.model.TicketPart;

public class TicketRender {

	private static final float transformToPxs = 28f / 100f;

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
		if (isLogEnabled()) {
			g.draw(new Rectangle2D.Double(0, 0, form.getHeight()
					* transformToPxs, form.getWidth() * transformToPxs));
		}
	}

	private static void renderText(Text text, Graphics2D g) {
		int textStyle = Font.PLAIN;
		if (text.isBold()) {
			textStyle |= Font.BOLD;
		}
		if (text.isItalic()) {
			textStyle |= Font.ITALIC;
		}

		Font font = new Font(text.getFontName(), textStyle, text.getFontSize());
		
		if (text.isUnderline()) {
			Map<TextAttribute, Integer> attributes = new HashMap<TextAttribute, Integer>();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			font = font.deriveFont(attributes);
		}
		
		if (!DEFAULT_ROTATION_VALUE.equals(text.getRotation())) {
			font = font.deriveFont(AffineTransform.getRotateInstance(-Math.toRadians(90 * text.getRotation().intValue())));
		}

		g.setFont(font);
		g.drawString(text.getData(), text.getPosX() * transformToPxs,
				text.getPosY() * transformToPxs + text.getFontSize());
	}

	private static void renderBarcode(Barcode barcode, Graphics2D g) {
	}

	private static void renderImage(Image image, Graphics2D g) {
	}
}