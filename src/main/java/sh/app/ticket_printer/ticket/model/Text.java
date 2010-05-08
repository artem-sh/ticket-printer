package sh.app.ticket_printer.ticket.model;

public class Text extends AbstractTicketAttribute {

    private String fontName;
    private Integer fontSize;
    private String data;

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

    @Override
    public String toString() {
        return "Text [data=" + data + ", fontName=" + fontName + ", fontSize=" + fontSize + ", toString()="
                + super.toString() + "]";
    }
}