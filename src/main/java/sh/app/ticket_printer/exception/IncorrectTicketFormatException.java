package sh.app.ticket_printer.exception;


public class IncorrectTicketFormatException extends Exception {

    private static final long serialVersionUID = -3418748361881841875L;
    
    public IncorrectTicketFormatException(String text, Throwable cause) {
        super(text, cause);
    }
}
