package sh.app.ticket_printer.ticket.model;

import java.security.InvalidParameterException;

public class Form implements TicketPart {
    
    private Float width;
    private Float height;
    private Float paddingLeft;
    private Float paddingRight;
    private Float paddingTop;
    private Float paddingBottom;
    private Float paperWidth;
    private Float paperHeight;
    private int border;
    public enum PaperOrientation {PORTRAIT, LANDSCAPE};
    private PaperOrientation paperOrientation = PaperOrientation.PORTRAIT;
    
    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
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
    
    public Float getPaperWidth() {
        return paperWidth;
    }
    
    public void setPaperWidth(Float paperWidth) {
        this.paperWidth = paperWidth;
    }

    public Float getPaperHeight() {
        return paperHeight;
    }

    public void setPaperHeight(Float paperHeight) {
        this.paperHeight = paperHeight;
    }
    
    public Float getPaddingLeft() {
        return paddingLeft;
    }
    
    public void setPaddingLeft(Float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }
    
    public Float getPaddingRight() {
        return paddingRight;
    }
    
    public void setPaddingRight(Float paddingRight) {
        this.paddingRight = paddingRight;
    }

    public Float getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(Float paddingTop) {
        this.paddingTop = paddingTop;
    }
    
    public Float getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(Float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public PaperOrientation getPaperOrientation() {
        return paperOrientation;
    }

    public void setPaperOrientation(PaperOrientation paperOrientation) {
        this.paperOrientation = paperOrientation;
    }

    @Override
    public TicketPartType getType() {
        return TicketPartType.FORM;
    }

    @Override
    public String toString() {
        return "Form [paperWidth=" + paperWidth + ", paperHeight=" + paperHeight + ", border="
                + border + ", paperOrientation=" + paperOrientation + ", width=" + width
                + ", height=" + height + ", paddingLeft=" + paddingLeft + ", paddingRight="
                + paddingRight + ", paddingTop=" + paddingTop + ", paddingBottom=" + paddingBottom + "]";
    }
}