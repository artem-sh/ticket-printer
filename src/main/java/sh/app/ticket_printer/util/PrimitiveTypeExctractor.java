package sh.app.ticket_printer.util;

public abstract class PrimitiveTypeExctractor {

    public static float asFloatSafely(Float f) {
        return f == null ? 0f : f.floatValue();
    }

    public static int asInt(String intStr) {
        return Integer.valueOf(intStr).intValue();
    }
}
