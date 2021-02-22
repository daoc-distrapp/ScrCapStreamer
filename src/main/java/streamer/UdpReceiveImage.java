
package streamer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author dordonez@ute.edu.ec
 */
public class UdpReceiveImage extends Thread {
	private final String grupoMc;
	private final int puerto;  
    private final JLabel monitor;
    private final int bufferSize;
    
    public UdpReceiveImage(String grupoMc, int puerto, JLabel monitor, int bufferSize) {
    	this.grupoMc = grupoMc;
        this.monitor = monitor;
        this.puerto = puerto;
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
        //Captura las imágenes enviadas por el sistema remoto y las muestra en el monitor (JLabel)
        try {
            MulticastSocket socket = new MulticastSocket(puerto);
            InetAddress mcGroupIp = InetAddress.getByName(grupoMc);
            socket.joinGroup(mcGroupIp);
            
            byte[] buffer = new byte[bufferSize];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true) {
                socket.receive(packet);
                showImage(packet);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void showImage(DatagramPacket packet) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            BufferedImage image = ImageIO.read(bais);
            monitor.setIcon(new ImageIcon(image));
            monitor.repaint();
            System.out.println("Recibida imagen: " + packet.getLength());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
