package sh.app.ticket_printer.ticket.model;

public class Text extends AbstractTicketAttribute {

    private String fontName = "Verdana";
    private Integer fontSize = Integer.valueOf(8);
    private String data;
    private boolean bold;
    private boolean italic;
    private boolean underline;

    @Override
    public TicketPartType getType() {
        return TicketPartType.TEXT;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    @Override
    public String toString() {
        return "Text [fontName=" + fontName + ", fontSize=" + fontSize + ", data=" + data + ", bold=" + bold
                + ", italic=" + italic + ", underline=" + underline + ", super.toString()="
                + super.toString() + "]";
    }
}