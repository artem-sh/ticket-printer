package sh.app.ticket_printer.ticket;

import static sh.app.ticket_printer.ticket.TicketValidator.validateFormElement;

import org.junit.Before;
import org.junit.Test;

import sh.app.ticket_printer.exception.IncorrectTicketDescriptionException;
import sh.app.ticket_printer.ticket.model.Form;
import sh.app.ticket_printer.ticket.model.Form.PaperOrientation;


public class TicketValidatorTest {
    private Form formPO;
    private Form formLO;
    
    @Before
    public void setUpBefore() {
        formPO = new Form();
        formPO.setPaperWidth(210f);
        formPO.setPaperHeight(297f);
        formPO.setWidth(convertMmToUnit(100f));
        formPO.setHeight(convertMmToUnit(200f));
        
        formLO = new Form();
        formLO.setPaperOrientation(PaperOrientation.LANDSCAPE);
        formLO.setPaperWidth(297f);
        formLO.setPaperHeight(210f);
        formLO.setWidth(convertMmToUnit(100f));
        formLO.setHeight(convertMmToUnit(200f));
    }

    @Test
    public void validateFormElement_portraitOrientation_everythingIsOk() throws Exception {
        validateFormElement(formPO);
        
        formPO.setPaddingBottom(80f);
        formPO.setPaddingTop(15f);
        formPO.setPaddingLeft(20f);
        formPO.setPaddingRight(80f);
        validateFormElement(formPO);
    }
    
    @Test
    public void validateFormElement_landscapeOrientation_everythingIsOk() throws Exception {
        validateFormElement(formLO);
        
        formLO.setPaddingBottom(5f);
        formLO.setPaddingTop(5f);
        formLO.setPaddingLeft(100f);
        formLO.setPaddingRight(95f);
        validateFormElement(formLO);
    }
    
    @Test
    public void validateFormElement_everythingIsOk_paperWidthIsNull() throws Exception {
        formPO.setPaperWidth(null);
        validateFormElement(formPO);
    }
    
    @Test
    public void validateFormElement_everythingIsOk_paperHeightIsNull() throws Exception {
        formPO.setPaperHeight(null);
        validateFormElement(formPO);
    }
    
    @Test(expected=IncorrectTicketDescriptionException.class)
    public void validateFormElement_portraitOrientation_formWidthBiggerPaperWidth() throws Exception {
        formPO.setWidth(convertMmToUnit(211f));
        validateFormElement(formPO);
    }
    
    @Test(expected=IncorrectTicketDescriptionException.class)
    public void validateFormElement_landscapeOrientation_formWidthBiggerPaperWidth() throws Exception {
        formLO.setWidth(convertMmToUnit(298f));
        validateFormElement(formLO);
    }
    
    @Test(expected=IncorrectTicketDescriptionException.class)
    public void validateFormElement_portraitOrientation_formHeightBiggerPaperHeight() throws Exception {
        formPO.setHeight(convertMmToUnit(298f));
        validateFormElement(formPO);
    }
    
    @Test(expected=IncorrectTicketDescriptionException.class)
    public void validateFormElement_landscapeOrientation_formHeightBiggerPaperHeight() throws Exception {
        formLO.setHeight(convertMmToUnit(220f));
        validateFormElement(formLO);
    }
    
    private static float convertMmToUnit(float sizeInUnits) {
        return sizeInUnits / TicketValidator.UNIT_TO_MM;
    }
}