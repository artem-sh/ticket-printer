package sh.app.ticket_printer.ticket;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.codec.binary.Base64;

public class ImageToBase64EncoderTest {

    public static void main(String[] args) {
        try {
            FileInputStream fileinputstream = new FileInputStream("test.jpg");
            int numberBytes = fileinputstream.available();
            byte contentArray[] = new byte[numberBytes];
            fileinputstream.read(contentArray);
            fileinputstream.close();

            long start = System.currentTimeMillis();
            byte[] encodeBase64 = org.apache.commons.codec.binary.Base64.encodeBase64(contentArray);
            long t1 = System.currentTimeMillis() - start;
            
            System.out.println(new String(encodeBase64));

            start = System.currentTimeMillis();
            byte[] decodeBase64 = org.apache.commons.codec.binary.Base64.decodeBase64(encodeBase64);
            long t2 = System.currentTimeMillis() - start;

            FileOutputStream outputStream0 = new FileOutputStream("test_new_0.jpg");
            outputStream0.write(decodeBase64);
            outputStream0.close();
            
            
            System.out.println(t1);
            System.out.println(t2);
            
            
//            final Image img = ImageIO.read(new File("test_new_0.jpg"));
            final Image img = ImageIO.read(new ByteArrayInputStream(decodeBase64));
            
            JFrame frame = new JFrame("Display image");
            Panel panel = new Panel() {
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    g.drawImage( img, 0, 0, null);
                }
            };
            frame.getContentPane().add(panel);
            frame.setSize(500, 500);
            frame.setVisible(true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
