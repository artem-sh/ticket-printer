package sh.app.ticket_printer.ticket;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.LinkedList;

import sh.app.ticket_printer.ticket.model.AbstractTicketAttribute;
import sh.app.ticket_printer.ticket.model.Form;

public class Ticket implements Printable {

    private Form form;
    private final LinkedList<AbstractTicketAttribute> elemets = new LinkedList<AbstractTicketAttribute>();

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public LinkedList<AbstractTicketAttribute> getElemets() {
        return elemets;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String toString() {
        return "Ticket [form=" + form + ", elemets=" + elemets + "]";
    }
}
