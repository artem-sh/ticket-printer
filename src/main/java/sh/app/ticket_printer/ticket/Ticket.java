package sh.app.ticket_printer.ticket;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.LinkedList;

import sh.app.ticket_printer.ticket.model.Element;
import sh.app.ticket_printer.ticket.model.Form;

public class Ticket implements Printable {
    
    private Form form;
    private LinkedList<Element> elemets;

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        // TODO Auto-generated method stub
        return 0;
    }

}
