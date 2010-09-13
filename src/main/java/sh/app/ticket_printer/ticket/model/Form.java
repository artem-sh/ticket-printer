package sh.app.ticket_printer.ticket.model;

import java.security.InvalidParameterException;

public class Form implements TicketPart {
    
    private float width;
    private float height;
    private float paddingLeft;
    private float paddingRight;
    private float paddingTop;
    private float paddingBottom;
    private float paperWidth;
    private float paperHeight;
    private int border;
    public enum PaperOrientation {PORTRAIT, LANDSCAPE};
    private PaperOrientation paperOrientation;
    
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
    
    public float getPaddingLeft() {
        return paddingLeft;
    }
    
    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }
    
    public float getPaddingRight() {
        return paddingRight;
    }
    
    public void setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
    }

    public float getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }
    
    public float getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(float paddingBottom) {
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
                + border + ", paperOrientation=" + paperOrientation + "width=" + width
                + ", height=" + height + ", paddingLeft=" + paddingLeft + ", paddingRight="
                + paddingRight + ", paddingTop=" + paddingTop + ", paddingBottom=" + paddingBottom + "]";
    }
}