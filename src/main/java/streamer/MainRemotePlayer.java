package streamer;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author dordonez@ute.edu.ec
 */
public class MainRemotePlayer {
   
    public static void main(String[] args) {
    	new MainRemotePlayer();
    }
    
    public MainRemotePlayer() {
        JFrame frame = new JFrame("VideoStreamerPlayer");
        PanelCamara panel = new PanelCamara();
        frame.add(panel, BorderLayout.WEST);
        frame.pack();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        UdpReceiveImage udpReceive = new UdpReceiveImage(Constants.UDP_MC_IP, Constants.UDP_PORT, panel.getMonitor(), Constants.BUFFER_SIZE);
        udpReceive.start();
    }
    
    private static class PanelCamara extends JPanel {
		private static final long serialVersionUID = 1L;
		private final JLabel monitor;

        public PanelCamara() {
            monitor = new JLabel(new ImageIcon(Constants.NOIMG_FILE));
            add(monitor);
        }
        
        public JLabel getMonitor() {
        	return monitor;
        }
    }    

}
