package sh.app.ticket_printer.ticket.model;

public class Image extends Element {
    
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
