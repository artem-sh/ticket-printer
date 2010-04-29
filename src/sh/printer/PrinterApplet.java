package sh.printer;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

public class PrinterApplet extends JApplet {
    
    public void test() {
        System.out.println("TEST!");
        JOptionPane.showMessageDialog(null, "TEST!"); 
    }

}
