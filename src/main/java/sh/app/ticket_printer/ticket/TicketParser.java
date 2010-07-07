package sh.app.ticket_printer.ticket;

import static sh.app.ticket_printer.PrinterApplet.isLogEnabled;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

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

import sh.app.ticket_printer.exception.CustomTicketParserException;
import sh.app.ticket_printer.exception.IncorrectTicketFormatException;
import sh.app.ticket_printer.exception.TicketParserException;
import sh.app.ticket_printer.ticket.model.AbstractTicketAttribute;
import sh.app.ticket_printer.ticket.model.Barcode;
import sh.app.ticket_printer.ticket.model.Form;
import sh.app.ticket_printer.ticket.model.Image;
import sh.app.ticket_printer.ticket.model.Text;

public class TicketParser {

    private static final String TICKET_XML_ENCODING = "UTF-8";
    private static final String TICKET_XSD_FILE_NAME = "/META-INF/ticket.xsd";
    private static final String TEXT_STYLE_BOLD = "B";
    private static final String TEXT_STYLE_ITALIC = "I";
    private static final String TEXT_STYLE_UNDERLINE = "U";
    private static final QName QNAME_FORM = QName.valueOf("form");
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

    private static Validator validator;

    public static Ticket parse(String xml) throws IncorrectTicketFormatException, TicketParserException {
        if (isLogEnabled()) {
            System.out.println("DEBUG: TicketParser.start(): got xml to parse: " + xml);
        }

        byte[] xmlBytes;
        try {
            xmlBytes = xml.getBytes(TICKET_XML_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new CustomTicketParserException("Problem with encoding");
        }
        
        validate(xmlBytes);

        Ticket ticket = new Ticket();

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            // XMLEventReader reader = factory.createXMLEventReader(new StringReader(xml));
            XMLEventReader reader;
            reader = factory.createXMLEventReader(new ByteArrayInputStream(xmlBytes));

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
        } catch (CustomTicketParserException e) {
            throw new TicketParserException(e);
        }

        if (isLogEnabled()) {
            System.out.println(ticket);
        }

        return ticket;
    }

    private static synchronized void validate(byte[] xmlBytes) throws IncorrectTicketFormatException {
        try {
            if (validator == null) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(new StreamSource(TicketParser.class
                        .getResourceAsStream(TICKET_XSD_FILE_NAME)));
                validator = schema.newValidator();
            }

            validator.validate(new StreamSource(new ByteArrayInputStream(xmlBytes)));
        } catch (Exception e) {
            throw new IncorrectTicketFormatException("Некорректный формат описания билета", e);
        }
    }

    private static void processFormElement(StartElement element, Ticket targetTicket) {
        Form form = new Form();
        form.setHeight(Integer.valueOf(element.getAttributeByName(QNAME_HEIGHT).getValue()));
        form.setWidth(Integer.valueOf(element.getAttributeByName(QNAME_WIDTH).getValue()));

        targetTicket.setForm(form);
    }

    private static void processTextElement(XMLEventReader reader, StartElement element, Ticket targetTicket)
            throws CustomTicketParserException, XMLStreamException {
        Text text = new Text();

        processAbstractTicketAttr(text, element);

        // TODO: check whether we know given font
        Attribute attr = element.getAttributeByName(QNAME_FONT_NAME);
        if (attr != null) {
            text.setFontName(attr.getValue());
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

        targetTicket.getElemets().add(text);
    }
    
    private static void processTextStyle(Text text, String style) throws CustomTicketParserException {
        if ((style == null) || style.isEmpty()) {
            return;
        }
        if (style.length() > 3) {
            throw new CustomTicketParserException("Incorrect value for text 'style' attribute of 'text' element");
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
            throws CustomTicketParserException, XMLStreamException {
        Image image = new Image();

        processAbstractTicketAttr(image, element);

        image.setData(parseText(reader).getBytes());

        targetTicket.getElemets().add(image);
    }

    private static void processBarcodeElement(XMLEventReader reader, StartElement element, Ticket targetTicket)
            throws CustomTicketParserException, XMLStreamException {
        Barcode barcode = new Barcode();

        processAbstractTicketAttr(barcode, element);

        barcode.setData(parseText(reader));

        targetTicket.getElemets().add(barcode);
    }

    private static void processAbstractTicketAttr(AbstractTicketAttribute ticketAttr, StartElement element) {
        ticketAttr.setPosX(Integer.valueOf(element.getAttributeByName(QNAME_POSX).getValue()));
        ticketAttr.setPosY(Integer.valueOf(element.getAttributeByName(QNAME_POSY).getValue()));
        Attribute rotationAttr = element.getAttributeByName(QNAME_ROTATION);
        if (rotationAttr != null) {
            ticketAttr.setRotation(Integer.valueOf(rotationAttr.getValue()));
        }
    }

    private static String parseText(XMLEventReader reader) throws CustomTicketParserException,
            XMLStreamException {
        if (!reader.hasNext()) {
            throw new CustomTicketParserException("No data event!");
        }

        XMLEvent event = reader.nextEvent();
        if (event.isCharacters()) {
            Characters characters = (Characters) event;
            return characters.getData();
        }

        throw new CustomTicketParserException("Unexpected event!");
    }
}
