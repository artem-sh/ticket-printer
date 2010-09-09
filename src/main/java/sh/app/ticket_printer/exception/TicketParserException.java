package sh.app.ticket_printer.exception;


public class TicketParserException extends Exception {

    private static final long serialVersionUID = -2813194672030977519L;
    private static final String ERROR_MSG = "Error while parsing ticket data";

    public TicketParserException(String text) {
        super(ERROR_MSG + ": " + text);
    }
    
    public TicketParserException(Throwable cause) {
        super(ERROR_MSG, cause);
    }
    
    public TicketParserException(String text, Throwable cause) {
        super(ERROR_MSG + ": " + text, cause);
    }
}
