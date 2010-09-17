package sh.app.ticket_printer.ticket;

import static sh.app.ticket_printer.PrinterApplet.isLogEnabled;
import static sh.app.ticket_printer.util.PrimitiveTypeExctractor.floatValue;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.codec.binary.Base64;

import sh.app.ticket_printer.PrinterApplet;
import sh.app.ticket_printer.exception.IncorrectTicketDescriptionException;
import sh.app.ticket_printer.exception.TicketParserException;
import sh.app.ticket_printer.ticket.model.AbstractTicketElement;
import sh.app.ticket_printer.ticket.model.Barcode;
import sh.app.ticket_printer.ticket.model.Form;
import sh.app.ticket_printer.ticket.model.Form.PaperOrientation;
import sh.app.ticket_printer.ticket.model.Image;
import sh.app.ticket_printer.ticket.model.Text;

public class TicketParser {

    private static final float UNIT_TO_MM = 0.1f;
    private static final String TICKET_XML_ENCODING = "UTF-8";
    private static final String TICKET_XSD_FILE_NAME = "/META-INF/ticket.xsd";
    private static final String TEXT_STYLE_BOLD = "B";
    private static final String TEXT_STYLE_ITALIC = "I";
    private static final String TEXT_STYLE_UNDERLINE = "U";
    private static final String FORM_PAPER_ORIENTATION_PORTRAIT = "portrait";
    private static final String FORM_PAPER_ORIENTATION_LANDSCAPE = "landscape";
    private static final QName QNAME_FORM = QName.valueOf("form");
    private static final QName QNAME_BORDER = QName.valueOf("border");
    private static final QName QNAME_PAPER_ORIENTATION = QName.valueOf("paper-orientation");
    private static final QName QNAME_PAPER_WIDTH = QName.valueOf("paper-width");
    private static final QName QNAME_PAPER_HEIGTH = QName.valueOf("paper-height");
    private static final QName QNAME_PADDING = QName.valueOf("padding");
    private static final QName QNAME_PADDING_LEFT = QName.valueOf("padding-left");
    private static final QName QNAME_PADDING_RIGHT = QName.valueOf("padding-right");
    private static final QName QNAME_PADDING_TOP = QName.valueOf("padding-top");
    private static final QName QNAME_PADDING_BOTTOM = QName.valueOf("padding-bottom");
    private static final QName QNAME_TEXT = QName.valueOf("text");
    private static final QName QNAME_IMAGE = QName.valueOf("image");
    private static final QName QNAME_BARCODE = QName.valueOf("barcode");
    private static final QName QNAME_HEIGHT = QName.valueOf("height");
    private static final QName QNAME_WIDTH = QName.valueOf("width");
    private static final QName QNAME_POSX = QName.valueOf("x");
    private static final QName QNAME_POSY = QName.valueOf("y");
    private static final QName QNAME_ROTATION = QName.valueOf("rotation");
    private static final QName QNAME_FONT_NAME = QName.valueOf("font");
    private static final QName QNAME_FONT_SIZE = QName.valueOf("size");
    private static final QName QNAME_TEXT_STYLE = QName.valueOf("style");

    private static final Set<String> fontFamilies;
    private static Validator validator;

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontFamilies = new HashSet<String>(Arrays.asList(ge.getAvailableFontFamilyNames()));
    }

    public static Ticket parse(String xml) throws TicketParserException, IncorrectTicketDescriptionException {
        if (isLogEnabled()) {
            System.out.println("DEBUG: TicketParser.parse(): got xml to parse: " + xml);
        }

        byte[] xmlBytes;
        try {
            xmlBytes = xml.getBytes(TICKET_XML_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new TicketParserException("Problem with encoding");
        }

        validate(xmlBytes);

        Ticket ticket = new Ticket();

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new ByteArrayInputStream(xmlBytes));

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement element = (StartElement) event;
                    if (QNAME_FORM.equals(element.getName())) {
                        processFormElement(element, ticket);
                    } else if (QNAME_TEXT.equals(element.getName())) {
                        processTextElement(reader, element, ticket);
                    } else if (QNAME_IMAGE.equals(element.getName())) {
                        processImageElement(reader, element, ticket);
                    } else if (QNAME_BARCODE.equals(element.getName())) {
                        processBarcodeElement(reader, element, ticket);
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new TicketParserException(e);
        }

        if (isLogEnabled()) {
            System.out.println("Unparsed ticket is: " + ticket);
        }

        return ticket;
    }

    private static synchronized void validate(byte[] xmlBytes) throws TicketParserException {
        try {
            if (validator == null) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(new StreamSource(TicketParser.class
                        .getResourceAsStream(TICKET_XSD_FILE_NAME)));
                validator = schema.newValidator();
            }

            validator.validate(new StreamSource(new ByteArrayInputStream(xmlBytes)));
        } catch (Exception e) {
            throw new TicketParserException("Incorrect ticket description format", e);
        }
    }

    private static void processFormElement(StartElement element, Ticket targetTicket)
            throws IncorrectTicketDescriptionException {
        Form form = new Form();
        form.setHeight(Float.valueOf(element.getAttributeByName(QNAME_HEIGHT).getValue()));
        form.setWidth(Float.valueOf(element.getAttributeByName(QNAME_WIDTH).getValue()));

        processFormPaddingAttributes(form, element);

        Attribute attr = element.getAttributeByName(QNAME_PAPER_ORIENTATION);
        if (attr != null) {
            if (FORM_PAPER_ORIENTATION_PORTRAIT.equals(attr.getValue())) {
                form.setPaperOrientation(PaperOrientation.PORTRAIT);
            } else if (FORM_PAPER_ORIENTATION_LANDSCAPE.equals(attr.getValue())) {
                form.setPaperOrientation(PaperOrientation.LANDSCAPE);
            }
        }
        attr = element.getAttributeByName(QNAME_BORDER);
        if (attr != null) {
            form.setBorder(Integer.valueOf(attr.getValue()));
        }
        attr = element.getAttributeByName(QNAME_PAPER_WIDTH);
        if (attr != null) {
            form.setPaperWidth(Float.valueOf(attr.getValue()));
        }
        attr = element.getAttributeByName(QNAME_PAPER_HEIGTH);
        if (attr != null) {
            form.setPaperHeight(Float.valueOf(attr.getValue()));
        }

        validateFormElement(form);
        targetTicket.setForm(form);
    }

    private static void processFormPaddingAttributes(Form form, StartElement element) {
        Attribute attr = element.getAttributeByName(QNAME_PADDING);
        if (attr != null) {
            Float padding = Float.valueOf(attr.getValue());
            form.setPaddingTop(padding);
            form.setPaddingRight(padding);
            form.setPaddingBottom(padding);
            form.setPaddingLeft(padding);
        }

        attr = element.getAttributeByName(QNAME_PADDING_TOP);
        if (attr != null) {
            form.setPaddingTop(Float.valueOf(attr.getValue()));
        }
        attr = element.getAttributeByName(QNAME_PADDING_RIGHT);
        if (attr != null) {
            form.setPaddingRight(Float.valueOf(attr.getValue()));
        }
        attr = element.getAttributeByName(QNAME_PADDING_BOTTOM);
        if (attr != null) {
            form.setPaddingBottom(Float.valueOf(attr.getValue()));
        }
        attr = element.getAttributeByName(QNAME_PADDING_LEFT);
        if (attr != null) {
            form.setPaddingLeft(Float.valueOf(attr.getValue()));
        }
    }

    private static void processTextElement(XMLEventReader reader, StartElement element, Ticket targetTicket)
            throws TicketParserException, XMLStreamException {
        Text text = new Text();

        processAbstractTicketAttr(text, element);

        Attribute attr = element.getAttributeByName(QNAME_FONT_NAME);
        if (attr != null) {
            text.setFontName(checkIfFontAvailable(attr.getValue()));
        }
        attr = element.getAttributeByName(QNAME_FONT_SIZE);
        if (attr != null) {
            text.setFontSize(Integer.valueOf(attr.getValue()));
        }
        attr = element.getAttributeByName(QNAME_TEXT_STYLE);
        if (attr != null) {
            processTextStyle(text, attr.getValue());
        }

        text.setData(parseText(reader));

        targetTicket.getElements().add(text);
    }

    private static void processTextStyle(Text text, String style) throws TicketParserException {
        if ((style == null) || style.isEmpty()) {
            return;
        }
        if (style.length() > 3) {
            throw new TicketParserException("Incorrect value for text 'style' attribute of 'text' element");
        }

        style = style.toUpperCase(Locale.ENGLISH);
        if (style.contains(TEXT_STYLE_BOLD)) {
            text.setBold(true);
        }
        if (style.contains(TEXT_STYLE_ITALIC)) {
            text.setItalic(true);
        }
        if (style.contains(TEXT_STYLE_UNDERLINE)) {
            text.setUnderline(true);
        }
    }

    private static void processImageElement(XMLEventReader reader, StartElement element, Ticket targetTicket)
            throws TicketParserException, XMLStreamException {
        Image image = new Image();

        processAbstractTicketAttr(image, element);

        byte[] encodedImg = null;
        try {
            encodedImg = parseText(reader).getBytes(TICKET_XML_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new TicketParserException("Problem with encoding");
        }

        if (!Base64.isArrayByteBase64(encodedImg)) {
            throw new TicketParserException("Value for the 'image' element is not in Base64 format");
        }

        image.setData(Base64.decodeBase64(encodedImg));

        targetTicket.getElements().add(image);
    }

    private static void processBarcodeElement(XMLEventReader reader, StartElement element, Ticket targetTicket)
            throws TicketParserException, XMLStreamException {
        Barcode barcode = new Barcode();

        processAbstractTicketAttr(barcode, element);

        barcode.setData(parseText(reader));

        targetTicket.getElements().add(barcode);
    }

    private static void processAbstractTicketAttr(AbstractTicketElement ticketAttr, StartElement element)
            throws TicketParserException {
        ticketAttr.setPosX(Integer.valueOf(element.getAttributeByName(QNAME_POSX).getValue()));
        ticketAttr.setPosY(Integer.valueOf(element.getAttributeByName(QNAME_POSY).getValue()));
        Attribute rotationAttr = element.getAttributeByName(QNAME_ROTATION);
        if (rotationAttr != null) {
            ticketAttr.setRotation(Integer.valueOf(rotationAttr.getValue()));
        }
    }

    private static String parseText(XMLEventReader reader) throws XMLStreamException, TicketParserException {
        if (!reader.hasNext()) {
            throw new TicketParserException("No data event!");
        }

        XMLEvent event = reader.nextEvent();
        if (event.isCharacters()) {
            Characters characters = (Characters) event;
            return characters.getData();
        }

        throw new TicketParserException("Unexpected event!");
    }

    private static String checkIfFontAvailable(String fontFamily) {
        if (fontFamilies.contains(fontFamily)) {
            return fontFamily;
        }

        if (PrinterApplet.isLogEnabled()) {
            System.out.println("WARNING: can't find desired dont " + fontFamily + " in OS. Use default instead.");
        }
        return Font.DIALOG;
    }

    private static void validateFormElement(Form form) throws IncorrectTicketDescriptionException {
        boolean paperSizePresented = checkPaperSizePresentedInTicket(form);
        boolean paperPaddingPresented = checkPaperPaddingPresentedInTicket(form);
        float paperWidth = floatValue(form.getPaperWidth());
        float paperHeight = floatValue(form.getPaperHeight());
        float formWidth = floatValue(form.getWidth()) * UNIT_TO_MM;
        float formHeight = floatValue(form.getHeight()) * UNIT_TO_MM;
        if (paperSizePresented) {
            if (paperWidth < formWidth) {
                throw new IncorrectTicketDescriptionException("Ticket width is bigger than specified paper width");
            }
            if (paperHeight < formHeight) {
                throw new IncorrectTicketDescriptionException("Ticket height is bigger than specified paper height");
            }
        } else if (paperPaddingPresented) {
            throw new IncorrectTicketDescriptionException(
                    "Values for both paper-width and paper-height should be specified to render paper padding");
        }

        if (paperSizePresented && paperPaddingPresented) {
            float paddingTop = floatValue(form.getPaddingTop());
            float paddingRight = floatValue(form.getPaddingRight());
            float paddingLeft = floatValue(form.getPaddingLeft());
            float paddingBottom = floatValue(form.getPaddingBottom());
            float demandedImageablePaperWidth = paperWidth - paddingLeft - paddingRight;
            float demandedImageablePaperHeight = paperHeight - paddingTop - paddingBottom;
            if (demandedImageablePaperWidth < formWidth) {
                throw new IncorrectTicketDescriptionException("Demanded paper width is less than specified form width");
            }
            if (demandedImageablePaperHeight < formHeight) {
                throw new IncorrectTicketDescriptionException(
                        "Demanded paper height is less than specified form height");
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
}