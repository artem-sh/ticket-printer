package sh.app.ticket_printer.ticket.model;

public class Form {
    
    private Integer width;
    private Integer height;
    
    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
    
    @Override
    public String toString() {
        return "Form [height=" + height + ", width=" + width + "]";
    }
}