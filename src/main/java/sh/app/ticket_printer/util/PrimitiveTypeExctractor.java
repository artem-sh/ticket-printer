package sh.app.ticket_printer.util;


public abstract class PrimitiveTypeExctractor {
    
    public static float floatValue(Float f) {
        return f == null ? 0f : f.floatValue();
    }
}
