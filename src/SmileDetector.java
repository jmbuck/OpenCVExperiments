
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
	private final static String EYE_CASCADE = "haarcascade_eye.xml";
	//Takes an image filename as input and will sort it into a directory
	//depending on whether there is a person smiling or not in the image.
	public static void main(String[] args) 
	{
		File[] files = new File("C:\\Users\\Jordan\\Desktop\\database 2\\lfw").listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith("jpg");
			}
		});

		//Load OpenCV and create classifiers using OpenCV's pre-trained classifiers.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CascadeClassifier faceClassifier = new CascadeClassifier(FACE_CASCADE);
		CascadeClassifier smileClassifier = new CascadeClassifier(SMILE_CASCADE);
		CascadeClassifier eyeClassifier = new CascadeClassifier(EYE_CASCADE);
		
		
		//perform detection on every jpg in directory
		for(File file : files) {
			
			//put image onto a mat and detect face
			Mat image = Imgcodecs.imread(file.getName());
			MatOfRect faceDetection = new MatOfRect();
			faceClassifier.detectMultiScale(image, faceDetection);
			String newFileName = "";

			if(faceDetection.toArray().length != 0) {
				MatOfRect smileDetection = new MatOfRect();
				//MatOfRect eyeDetection = new MatOfRect();
				for (Rect rect : faceDetection.toArray()) {
					//draw rectangle around face
					Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
							new Scalar(0, 0, 255));
					
					//Mat eyeRoi = new Mat(image, rect);
					
					int xOffset = rect.x;
					Rect mouthRect = new Rect(rect.x, rect.y+(rect.height/2), rect.width, rect.height/2);
					int yOffset = mouthRect.y;
					
					//Detect eyes
					/*eyeClassifier.detectMultiScale(eyeRoi, eyeDetection);
					for(Rect eyeRect : eyeDetection.toArray()) {
						Imgproc.rectangle(image, new Point(xOffset + eyeRect.x, yOffset + eyeRect.y), new Point(xOffset + eyeRect.x + eyeRect.width, yOffset + eyeRect.y + eyeRect.height),
								new Scalar(255, 0, 0));
						
						if(eyeRect.y + eyeRect.height > yOffset) {
							yOffset = eyeRect.y + eyeRect.height;
						}	
					}*/
					
					//detect smiles in face region
					Mat smileRoi = new Mat(image, mouthRect);	
					smileClassifier.detectMultiScale(smileRoi, smileDetection);
					for (Rect smileRect : smileDetection.toArray()) {
						//draw rectangle around smile
						Imgproc.rectangle(image, new Point(xOffset + smileRect.x, yOffset + smileRect.y), new Point(xOffset + smileRect.x + smileRect.width, yOffset + smileRect.y + smileRect.height),
								new Scalar(0, 255, 0));
					}
					
					//add to negative or positive folder depending on result
					if(smileDetection.toArray().length == 0) {
						System.out.println("Negative sample");
						newFileName = "negative/"+file.getName();
					} else {
						System.out.println("Positive sample");
						newFileName = "positive/"+file.getName();
						break;
					}
				}
			} else {
				System.out.println("Negative sample");
				newFileName = "negative/"+file.getName();
			}

			Imgcodecs.imwrite(newFileName, image);
		}
	}
}
