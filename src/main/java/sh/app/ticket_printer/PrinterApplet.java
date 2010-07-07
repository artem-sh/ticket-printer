package sh.app.ticket_printer;

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import sh.app.ticket_printer.ticket.TicketPrinter;
import sh.app.ticket_printer.ticket.Ticket;
import sh.app.ticket_printer.ticket.TicketParser;

public class PrinterApplet extends Applet {

    private final static String DEBUG_APPLET_PARAM = "debug";
    private final static String MAIN_ERROR_MSG = "Ошибка при печати билета";
    private static boolean logEnabled;

    final static int maxCharHeight = 15;
    final static int minFontSize = 6;

    final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);

    final static float dash1[] = { 10.0f };
    final static BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);

    public static boolean isLogEnabled() {
        return logEnabled;
    }

    public void init() {
        if (getParameter(DEBUG_APPLET_PARAM) != null) {
            logEnabled = true;
        }
        
        if (isLogEnabled()) {
            System.out.println("Entering PrinterApplet.init()");
        }

        if (!TicketPrinter.checkDefaultPrinterAvailable()) {
            if (isLogEnabled()) {
                System.err.println("WARNING: no default printer found");
            }

            if (!TicketPrinter.checkPrintersAvailable()) {
                if (isLogEnabled()) {
                    System.err.println("WARNING: no printer found");
                }
            }
        }
        
        // TODO: only for running in applet viewer
        // REMOVE in production!!!
        print();
    }

    @Override
    public void start() {
    }

    @Override
    public void paint(Graphics g) {
    }

    public void print() {
        if (isLogEnabled()) {
            System.out.println("Entering PrintApplet.print()");
        }

        if (!TicketPrinter.checkPrintersAvailable()) {
            if (isLogEnabled()) {
                System.err.println("WARNING: no printer found");
            }

            JOptionPane.showMessageDialog(null, MAIN_ERROR_MSG
                    + ". Не найдены установленные принтеры", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        InputStream is = null;
        try {
            String xmlFileName = "/META-INF/new1.xml";
            is = PrinterApplet.class.getResourceAsStream(xmlFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Ticket ticket = TicketParser.parse(sb.toString());
            TicketPrinter.printTicket(ticket);
        } catch (Exception x) {
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
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    // TODO: remove in production    
    public static void setLogEnabled() {
        logEnabled = true;
    }
    
    // TODO: remove in production
    public static void main(String[] args) {
        setLogEnabled();
        new PrinterApplet().print();
    }
}