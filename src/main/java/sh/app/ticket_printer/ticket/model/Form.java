package sh.app.ticket_printer.ticket.model;

import java.security.InvalidParameterException;

public class Form implements TicketPart {
    
    private float width;
    private float height;
    private float paperWidth;
    private float paperHeight;
    private int border;
    
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
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
    
    public float getPaperWidth() {
        return paperWidth;
    }
    
    public void setPaperWidth(float paperWidth) {
        this.paperWidth = paperWidth;
    }

    public float getPaperHeight() {
        return paperHeight;
    }

    public void setPaperHeight(float paperHeight) {
        this.paperHeight = paperHeight;
    }

    @Override
    public TicketPartType getType() {
        return TicketPartType.FORM;
    }
    
    @Override
    public String toString() {
        return "Form [paperWidth=" + paperWidth + ", paperHeight=" + paperHeight + ", height="
                + height + ", width=" + width + ", border=" + border + "]";
    }
}