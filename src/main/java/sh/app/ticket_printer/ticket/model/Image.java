package sh.app.ticket_printer.ticket.model;


public class Image extends AbstractTicketAttribute {
    
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    @Override
    public TicketPartType getType() {
        return TicketPartType.IMAGE;
    }
    
    @Override
    public String toString() {
        return "Image [data=" + ((data != null) ? "not null" : "null") + ", super.toString()=" + super.toString() + "]";
    }
}