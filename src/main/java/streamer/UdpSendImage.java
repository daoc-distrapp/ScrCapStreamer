package streamer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
*
* @author dordonez@ute.edu.ec
*/
public class UdpSendImage extends Thread {
    private final BlockingQueue<byte[]> imageQueue;
    private final String host;
    private final int puerto;

    public UdpSendImage(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
        imageQueue = new LinkedBlockingQueue<>();
    }
    
    @Override
    public void run() {
        //Recupere las imágenes que se encuentran en la cola y envíelas al cliente remoto
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress direccion = InetAddress.getByName(host);
            DatagramPacket packet;
            byte[] buffer;
            
            while (true) {  
                buffer = imageQueue.take();
                packet = new DatagramPacket(buffer, buffer.length, direccion, puerto);
                socket.send(packet);
                System.out.println("Imagen enviada: " + buffer.length);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    //Alguien más debe llamar este método y poner las imágenes en la cola
    public void queueImage(byte[] image) {
        try {
            imageQueue.put(image);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
}
