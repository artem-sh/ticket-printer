package sh.app.ticket_printer.exception;


public class CustomTicketParserException extends TicketParserException {

    private static final long serialVersionUID = 1380565502945056972L;

    public CustomTicketParserException(String text) {
        super(text);
    }
}
