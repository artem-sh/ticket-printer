package sh.app.ticket_printer.ticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import sh.app.ticket_printer.PrinterApplet;
import sh.app.ticket_printer.ticket.TicketPrinter;
import sh.app.ticket_printer.ticket.Ticket;
import sh.app.ticket_printer.ticket.TicketParser;

public class TicketPrinterTest {

    public static void main(String[] args) {
        InputStream is = null;
        try {
            String xmlFileName = "/META-INF/ticket1.xml";
            is = PrinterApplet.class.getResourceAsStream(xmlFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Ticket ticket = TicketParser.parse(sb.toString());
            TicketPrinter.printTicket(ticket);
        } catch (Exception x) {
            System.err.println(x);
            x.printStackTrace();

            if (x.getCause() != null) {
                System.err.println("Cause is: " + x.getCause());
                x.getCause().printStackTrace();
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
