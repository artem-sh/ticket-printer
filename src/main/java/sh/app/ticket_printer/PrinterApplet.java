package sh.app.ticket_printer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
        
        try {
            TicketParser.parse(new FileInputStream("ticket1.xml"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.out.println(service);
    }

    public void test() {
        System.out.println("TEST!");
        JOptionPane.showMessageDialog(null, "TEST!");
    }

}
