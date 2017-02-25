
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

public class SmileDetector {

	
	private final static String CASCADE_NAME = "haarcascade_smile.xml";
	
	//Takes an image filename as input and will sort it into a directory
	//depending on whether there is a person smiling or not in the image.
	public static void main(String[] args) 
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CascadeClassifier classifier = new CascadeClassifier(CASCADE_NAME);
		
		Mat image = Imgcodecs.imread(args[0]);
		MatOfRect faceDetection = new MatOfRect();
		classifier.detectMultiScale(image, faceDetection);
		String newFileName = "";
		if(faceDetection.toArray().length == 0) {
			 newFileName = "../negative/"+args[0];
		} else if(faceDetection.toArray().length > 0) {
			newFileName = "../positive/"+args[0];
		} else {
			System.out.println("Error");
		}
		
		Imgcodecs.imwrite(newFileName, image);
	}
}
