package sh.app.ticket_printer.ticket.model;

import java.security.InvalidParameterException;

public class Form implements TicketPart {
    
    private Integer width;
    private Integer height;
    private int border;
    
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
    
    public boolean hasBorder() {
        return border == 1;
    }
    
    public void setBorder(int borderValue) {
        if ((borderValue < 0) || (borderValue > 1)) {
            throw new InvalidParameterException("Border value could be 0 or 1");
        }
        
        border = borderValue;
    }
    
    @Override
    public TicketPartType getType() {
        return TicketPartType.FORM;
    }
    
    @Override
    public String toString() {
        return "Form [height=" + height + ", width=" + width + ", border=" + border + "]";
    }
}