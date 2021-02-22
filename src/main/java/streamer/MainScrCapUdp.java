package streamer;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class MainScrCapUdp {

	public static void main(String[] args) {
		UdpSendImage udpSend = new UdpSendImage(Constants.UDP_MC_IP, Constants.UDP_PORT);
		udpSend.start();
		
		Robot robot = null;
		Rectangle area = null;
		Image cursor = null;
		
		try {
		    robot = new Robot(); 
		    area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		    cursor = ImageIO.read(new File(Constants.CURSOR_FILE));
		} catch(AWTException | IOException e) {
			e.printStackTrace();
		}
	    
		while(true) {
			byte[] image = getScreenCapture(robot, area, cursor);
			udpSend.queueImage(image);
		}
	}
	
    private static byte[] getScreenCapture(Robot robot, Rectangle area, Image cursor) {
        try {        
        	// Configuramos el formato (JPEG) y la compresión
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("JPEG").next();
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(Constants.JPEG_COMP_QUAL);
            // Definimos el buffer en memoria que contendrá la imagen
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream imgOutputStream = ImageIO.createImageOutputStream(baos);
            jpgWriter.setOutput(imgOutputStream);
            // Capturamos pantalla y añadimos cursor
            BufferedImage image = robot.createScreenCapture(area);
            int x = MouseInfo.getPointerInfo().getLocation().x;
            int y = MouseInfo.getPointerInfo().getLocation().y;
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.drawImage(cursor, x, y, 32, 32, null);
            // Redimensionamos imagen
            Image tmpImg = image.getScaledInstance(Constants.SCR_IMG_WIDTH, Constants.SCR_IMG_HEIGTH, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(Constants.SCR_IMG_WIDTH, Constants.SCR_IMG_HEIGTH, BufferedImage.TYPE_INT_RGB);
            resized.getGraphics().drawImage(tmpImg, 0, 0 , null);
            // Pasamos la imagen al buffer
            jpgWriter.write(null, new IIOImage(resized, null, null), jpgWriteParam);            
            jpgWriter.dispose();
            byte[] buffer = baos.toByteArray();
            baos.close();
            return buffer;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
