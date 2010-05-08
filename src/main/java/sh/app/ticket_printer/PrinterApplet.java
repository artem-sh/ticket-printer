package sh.app.ticket_printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import sh.app.ticket_printer.ticket.TicketParser;

public class PrinterApplet extends JApplet {

    @Override
    public void start() {
        if (!PrinterMgr.checkDefaultPrinterAvailable()) {
            System.out.println("WARNING: no default printer found");

            if (!PrinterMgr.checkPrintersAvailable()) {
                System.out.println("ERROR: no printer found");
                JOptionPane.showMessageDialog(null, "No printer found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String... p) {
        System.out.println();

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

            TicketParser.parse(sb.toString());
        } catch (Exception x) {
            System.err.println(x);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // System.out.println(service);
    }

    public void test() {
        System.out.println("TEST!");
        new Thread() {

            public void run() {
                main();
            };
        }.start();
        // JOptionPane.showMessageDialog(null, "TEST!");
    }

}
