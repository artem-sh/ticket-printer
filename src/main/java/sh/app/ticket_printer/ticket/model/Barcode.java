package sh.app.ticket_printer.ticket.model;

public class Barcode extends AbstractTicketElement {
    
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
    @Override
    public TicketPartType getType() {
        return TicketPartType.BARCODE;
    }
    
    @Override
    public String toString() {
        return "Barcode [data=" + data + ", super.toString()=" + super.toString() + "]";
    }
}