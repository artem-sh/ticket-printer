package sh.app.ticket_printer.ticket;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class TicketParser {

    private static final String TICKET_CHARSET = "UTF-8";

    public static void parse(InputStream src) {
        XMLInputFactory xmlif = null;

        try {
            xmlif = XMLInputFactory.newInstance();
            xmlif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.TRUE);
            xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
        }

        try {
//            XMLStreamReader xmlr = xmlif.createXMLStreamReader("TicketStream", new ByteArrayInputStream(src
//                    .getBytes(TICKET_CHARSET)));
            XMLStreamReader xmlr = xmlif.createXMLStreamReader("TicketStream", src);
            
            int eventType;
            while (xmlr.hasNext()) {
                eventType = xmlr.next();
            }

        } catch (XMLStreamException ex) {
            System.out.println(ex.getMessage());

            if (ex.getNestedException() != null) {
                ex.getNestedException().printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
