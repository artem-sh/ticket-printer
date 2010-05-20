package sh.app.ticket_printer.ticket.model;

public class Text extends AbstractTicketAttribute {

    private String fontName = "Verdana";
    private Integer fontSize = Integer.valueOf(8);
    private String data;
    private String style;

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

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }
    
    @Override
    public TicketPartType getType() {
        return TicketPartType.TEXT;
    }

    @Override
    public String toString() {
        return "Text [data=" + data + ", fontName=" + fontName + ", fontSize=" + fontSize + ", style=" + style
                + ", super.toString()=" + super.toString() + "]";
    }
}