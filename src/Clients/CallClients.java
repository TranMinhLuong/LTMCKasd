package Clients;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import Models.CallingMessenger;

import javax.swing.JLabel;
import java.awt.Dimension;

public class CallClients extends JFrame {

	private JPanel contentPane;
	JLabel lbldifClient;
	Webcam webCam;
	Socket socket;
	WebcamPanel webcamPanel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CallClients frame = new CallClients();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws Exception 
	 * @throws UnknownHostException 
	 */
	public CallClients() throws Exception {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 617, 506);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		lbldifClient = new JLabel("");
		lbldifClient.setPreferredSize(new Dimension(250, 250));
		contentPane.add(lbldifClient, BorderLayout.EAST);
		
		webCam = Webcam.getDefault();
		webCam.setViewSize(WebcamResolution.VGA.getSize());

		webcamPanel = new WebcamPanel(webCam);
		webcamPanel.setImageSizeDisplayed(true);
		webcamPanel.setFPSDisplayed(true);
		webcamPanel.setMirrored(true);
		webcamPanel.setDisplayDebugInfo(true);
		add(webcamPanel,BorderLayout.CENTER);
		
//		socket = new Socket("",3000);
		
	}
	
	public ImageIcon reIcon(byte[] path) {
		ImageIcon img = new ImageIcon(path);
		Image im = img.getImage().getScaledInstance(lbldifClient.getWidth(), lbldifClient.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon anh = new ImageIcon(im);
		return anh;
	}
	
	class ReceiveThread extends Thread {

		@Override
		public void run() {
			CallingMessenger calling = null;
			try {
				while (true) {
					InputStream inputStream = socket.getInputStream();
					ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
					calling = (CallingMessenger) objectInputStream.readObject();
					lbldifClient.setIcon(reIcon(calling.getImg()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class SendClient extends Thread{
		Socket socket;
		
		public SendClient(Socket socket) {
			super();
			this.socket = socket;
			start();
		}

		@Override
		public void run() {
			boolean loop = true;
			BufferedImage bi;
			while (loop) {
				try {
					bi = webcamPanel.getImage();
					ByteArrayOutputStream bStream = new ByteArrayOutputStream();
					ImageIO.write(bi, "jpg", bStream);
					sendMessage(socket,bStream.toByteArray());
				} catch (Exception e) {
					if (socket == null) {
						loop = false;
						e.printStackTrace();
					}
				}
			}
		}
		
		public void sendMessage(Socket socket,byte[] img) throws Exception {
	        OutputStream ops = socket.getOutputStream();
	        ObjectOutputStream ots = new ObjectOutputStream(ops);
	        ots.writeObject(new CallingMessenger(img));
	        ots.flush();
	    }
	}
}
