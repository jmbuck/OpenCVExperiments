
import java.io.File;
import java.io.FileFilter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class SmileDetector {


	private final static String SMILE_CASCADE = "haarcascade_smile.xml";
	private final static String FACE_CASCADE = "haarcascade_frontalface_alt.xml";
	//Takes an image filename as input and will sort it into a directory
	//depending on whether there is a person smiling or not in the image.
	public static void main(String[] args) 
	{
		File[] files = new File("C:\\Users\\Jordan\\git\\SmileDetector").listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				return arg0.getName().endsWith("jpg");
			}
		});

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CascadeClassifier faceClassifier = new CascadeClassifier(FACE_CASCADE);
		CascadeClassifier smileClassifier = new CascadeClassifier(SMILE_CASCADE);
		//System.out.println(args[0]);
		for(File file : files) {
			Mat image = Imgcodecs.imread(file.getName());
			MatOfRect faceDetection = new MatOfRect();
			faceClassifier.detectMultiScale(image, faceDetection);
			String newFileName = "";

			if(faceDetection.toArray().length != 0) {
				MatOfRect smileDetection = new MatOfRect();
				for (Rect rect : faceDetection.toArray()) {
					Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
							new Scalar(0, 0, 255));
					Mat roi = new Mat(image, rect);
					int xOffset = rect.x;
					int yOffset = rect.y
					
					
					smileClassifier.detectMultiScale(roi, smileDetection);
					for (Rect smileRect : smileDetection.toArray()) {
						Imgproc.rectangle(image, new Point(smileRect.x, smileRect.y), new Point(smileRect.x + smileRect.width, smileRect.y + smileRect.height),
								new Scalar(0, 255, 0));
					}
					if(smileDetection.toArray().length == 0) {
						System.out.println("No smile found!");
						newFileName = "negative/"+file.getName();
					} else {
						System.out.println("Positive sample!");
						newFileName = "positive/"+file.getName();
						break;
					}
				}
			} else {
				System.out.println("No face found!");
				newFileName = "negative/"+file.getName();
			}

			Imgcodecs.imwrite(newFileName, image);
		}
	}
}
