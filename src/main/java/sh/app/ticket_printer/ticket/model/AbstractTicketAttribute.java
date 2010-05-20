package sh.app.ticket_printer.ticket.model;

public abstract class AbstractTicketAttribute implements TicketPart {
    
    private Integer posX;
    private Integer posY;
    private Integer rotation = Integer.valueOf(0);

    public Integer getPosX() {
        return posX;
    }

    public void setPosX(Integer posX) {
        this.posX = posX;
    }

    public Integer getPosY() {
        return posY;
    }

    public void setPosY(Integer posY) {
        this.posY = posY;
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }
    
    @Override
    public String toString() {
        return "AbstractTicketAttribute [posX=" + posX + ", posY=" + posY + ", rotation=" + rotation + "]";
    }
}