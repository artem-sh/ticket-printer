package sh.app.ticket_printer.ticket.model;

public class Text extends Element {

    private String fontName;
    private String fontSize;
    private String data;

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}