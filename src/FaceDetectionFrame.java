import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class FaceDetectionFrame extends JFrame 
{
	private final static String SMILE_CASCADE = "haarcascade_smile.xml";
	private final static String FACE_CASCADE = "haarcascade_frontalface_alt.xml";
	private final static String EYE_CASCADE = "haarcascade_eye.xml";
	private CascadeClassifier faceClassifier = new CascadeClassifier(FACE_CASCADE);
	private CascadeClassifier smileClassifier = new CascadeClassifier(SMILE_CASCADE);
	private CascadeClassifier eyeClassifier = new CascadeClassifier(EYE_CASCADE);
	private JPanel contentPane;
	private static VideoCapture capture;

	public static void main(String[] args) 
	{
		 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		 capture = new VideoCapture();
		 capture.open(0);
		 EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                try {
	                    FaceDetectionFrame frame = new FaceDetectionFrame();
	                    frame.setVisible(true);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        });
		 
	}

	public BufferedImage matToImage(Mat mat) {

		BufferedImage img = null;
		int w = mat.cols(), h = mat.rows();
		byte[] dat = new byte[w * h * 3]; 
		if (img == null || img.getWidth() != w || img.getHeight() != h
				|| img.getType() != BufferedImage.TYPE_3BYTE_BGR)
			img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		mat.get(0, 0, dat);
		img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), dat);

		return img;
	}

	public BufferedImage readFrame() {
		Mat mat = new Mat();
		capture.read(mat);
		MatOfRect faceDetection = new MatOfRect();
		faceClassifier.detectMultiScale(mat, faceDetection);

		if(faceDetection.toArray().length != 0) {
			MatOfRect smileDetection = new MatOfRect();
			MatOfRect eyeDetection = new MatOfRect();
			for (Rect rect : faceDetection.toArray()) {
				//draw rectangle around face
				Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
						new Scalar(0, 0, 255));
				int xOffset = rect.x;
				int eyeYOffset = rect.y;
				Rect eyeROIRect = new Rect(rect.x, rect.y, rect.width, rect.height/2);
				Rect mouthROIRect = new Rect(rect.x, rect.y+(rect.height/2), rect.width, rect.height/2);
				int mouthYOffset = mouthROIRect.y;

				//Draw rectangles around eyes
				eyeClassifier.detectMultiScale(new Mat(mat, eyeROIRect), eyeDetection);
				for(Rect eyeRect : eyeDetection.toArray()) {
					Imgproc.rectangle(mat, new Point(xOffset + eyeRect.x, eyeYOffset + eyeRect.y), new Point(xOffset + eyeRect.x + eyeRect.width, eyeYOffset + eyeRect.y + eyeRect.height),
							new Scalar(255, 0, 0));
				}

				//draw rectangle around smile
				Mat mouthROI = new Mat(mat, mouthROIRect);	
				smileClassifier.detectMultiScale(mouthROI, smileDetection);
				for (Rect smileRect : smileDetection.toArray()) {
					//draw rectangle around smile
					Imgproc.rectangle(mat, new Point(xOffset + smileRect.x, mouthYOffset + smileRect.y), new Point(xOffset + smileRect.x + smileRect.width, mouthYOffset + smileRect.y + smileRect.height),
							new Scalar(0, 255, 0));
				}
			}
		}
		return matToImage(mat);
	}
	
	public FaceDetectionFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 490);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		new DrawThread().start();
	}

	public void paint(Graphics g){
		g = contentPane.getGraphics();
		g.drawImage(readFrame(), 0, 0, this);
	}

	class DrawThread extends Thread{
		@Override
		public void run() {
			while (true){
				repaint();
				try { Thread.sleep(30);
				} catch (InterruptedException e) {    }
			}  
		} 
	}
}
