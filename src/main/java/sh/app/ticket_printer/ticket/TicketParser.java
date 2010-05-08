package sh.app.ticket_printer.ticket;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import sh.app.ticket_printer.ticket.model.AbstractTicketAttribute;
import sh.app.ticket_printer.ticket.model.Barcode;
import sh.app.ticket_printer.ticket.model.Form;
import sh.app.ticket_printer.ticket.model.Image;
import sh.app.ticket_printer.ticket.model.Text;

public class TicketParser {

    private static final String TICKET_XSD_FILE_NAME = "/META-INF/ticket.xsd";
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

    private static Validator validator;

    public static Ticket parse(String xml) throws Exception {
        System.out.println("DEBUG: TicketParser.start(): parsing xml: " + xml);
        validate(xml);

        Ticket ticket = new Ticket();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(new StringReader(xml));

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

        System.out.println(ticket);

        return ticket;
    }

    private static synchronized void validate(String xml) throws SAXException, IOException {
        if (validator == null) {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new StreamSource(TicketParser.class
                    .getResourceAsStream(TICKET_XSD_FILE_NAME)));
            validator = schema.newValidator();
        }

        validator.validate(new StreamSource(new StringReader(xml)));
    }

    private static void processFormElement(StartElement element, Ticket targetTicket) {
        Form form = new Form();
        form.setHeight(Integer.valueOf(element.getAttributeByName(QNAME_HEIGHT).getValue()));
        form.setWidth(Integer.valueOf(element.getAttributeByName(QNAME_WIDTH).getValue()));

        targetTicket.setForm(form);
    }

    private static void processTextElement(XMLEventReader reader, StartElement element, Ticket targetTicket)
            throws Exception {
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

        text.setData(parseText(reader));

        targetTicket.getElemets().add(text);
    }

    private static void processImageElement(XMLEventReader reader, StartElement element, Ticket targetTicket)
            throws Exception {
        Image image = new Image();

        processAbstractTicketAttr(image, element);

        image.setData(parseText(reader).getBytes());

        targetTicket.getElemets().add(image);
    }

    private static void processBarcodeElement(XMLEventReader reader, StartElement element, Ticket targetTicket)
            throws Exception {
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

    private static String parseText(XMLEventReader reader) throws Exception {
        if (!reader.hasNext()) {
            throw new Exception("No data event!");
        }

        XMLEvent event = reader.nextEvent();
        if (event.isCharacters()) {
            Characters characters = (Characters) event;
            return characters.getData();
        } else {
            throw new Exception("Unexpected event!");
        }
    }
}
