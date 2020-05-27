package view;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.opencv.core.Point;

import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Utils;

public class VideoController {

	@FXML
	private Button cameraButton;
	@FXML
	private ImageView originalFrame;
	@FXML
	private RadioButton canny;
	@FXML
	private RadioButton sobel;
	@FXML
	private Slider threshold;

	private ScheduledExecutorService timer;
	private VideoCapture capture = new VideoCapture();
	private boolean cameraActive;

	Point clickedPoint = new Point(0, 0);
	Mat oldFrame;

	protected void init() {
		this.threshold.setShowTickLabels(true);
	}

	@FXML
	protected void startCamera() {
		// set a fixed width for the frame
		originalFrame.setFitWidth(380);
		// preserve image ratio
		originalFrame.setPreserveRatio(true);

		// mouse listener
		originalFrame.setOnMouseClicked(e -> {
			System.out.println("[" + e.getX() + ", " + e.getY() + "]");
			clickedPoint.x = e.getX();
			clickedPoint.y = e.getY();
		});

		if (!this.cameraActive) {
			// disable setting checkboxes
			this.canny.setDisable(true);

			// start the video capture
			this.capture.open(0);

			// is the video stream available?
			if (this.capture.isOpened()) {
				this.cameraActive = true;

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run() {
						// effectively grab and process a single frame
						Mat frame = grabFrame();
						// convert and show the frame
						Image imageToShow = Utils.matImage(frame);
						updateImageView(originalFrame, imageToShow);
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				// update the button content
				this.cameraButton.setText("Stop Camera");
			} else {
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		} else {
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.cameraButton.setText("Start Camera");
			// enable setting checkboxes
			this.canny.setDisable(false);

			// stop the timer
			this.stopAcquisition();
		}
	}

	private Mat grabFrame() {
		Mat frame = new Mat();
		if (this.capture.isOpened()) {
			try {
				this.capture.read(frame);
				if (!frame.empty()) {
					if (this.canny.isSelected()) {
						frame = this.doCanny(frame);
					}
					if (this.sobel.isSelected()) {
						// frame = this.doCanny(frame);
						frame = this.doSobel(frame);
					}
				}
			} catch (Exception e) {
				System.err.print("Exception in the image elaboration...");
				e.printStackTrace();
			}
		}

		return frame;
	}

	/**
	 * Get the average hue value of the image starting from its Hue channel
	 * histogram
	 * 
	 * @param hsvImg    the current frame in HSV
	 * @param hueValues the Hue component of the current frame
	 * @return the average Hue value
	 * 
	 *         private double getHistAverage(Mat hsvImg, Mat hueValues) { // init
	 *         double average = 0.0; Mat hist_hue = new Mat(); // 0-180: range of
	 *         Hue values MatOfInt histSize = new MatOfInt(180); List<Mat> hue = new
	 *         ArrayList<>(); hue.add(hueValues);
	 * 
	 *         // compute the histogram Imgproc.calcHist(hue, new MatOfInt(0), new
	 *         Mat(), hist_hue, histSize, new MatOfFloat(0, 179));
	 * 
	 *         // get the average Hue value of the image //
	 *         (sum(bin(h)*h))/(image-height*image-width) // ----------------- //
	 *         equivalent to get the hue of each pixel in the image, add them, and
	 *         // divide for the image size (height and width) for (int h = 0; h <
	 *         180; h++) { // for each bin, get its value and multiply it for the
	 *         corresponding // hue average += (hist_hue.get(h, 0)[0] * h); }
	 * 
	 *         // return the average hue of the image return average = average /
	 *         hsvImg.size().height / hsvImg.size().width; }
	 **/

	private Mat doCanny(Mat frame) {
		// init
		Mat grayImage = new Mat();
		Mat detectedEdges = new Mat();

		// convert to grayscale
		Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

		// reduce noise with a 3x3 kernel
		Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));

		// canny detector, with ratio of lower:upper threshold of 3:1
		Imgproc.Canny(detectedEdges, detectedEdges, this.threshold.getValue(), this.threshold.getValue() * 3);

		// using Canny's output as a mask, display the result
		Mat dest = new Mat();
		frame.copyTo(dest, detectedEdges);

		return dest;
	}

	private Mat doSobel(Mat frame) {

		Mat grayImage = new Mat();
		Mat detectedEdges = new Mat();
		// int scale = 1;
		// int delta = 0;
		int ddepth = CvType.CV_16S;
		Mat grad_x = new Mat();
		Mat grad_y = new Mat();
		Mat abs_grad_x = new Mat();
		Mat abs_grad_y = new Mat();

		// reduce noise with a 3x3 kernel
		Imgproc.GaussianBlur(frame, frame, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);

		// convert to grayscale
		Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

		// Gradient X
		// Imgproc.Sobel(grayImage, grad_x, ddepth, 1, 0, 3, scale,
		// this.threshold.getValue(), Core.BORDER_DEFAULT );
		Imgproc.Sobel(grayImage, grad_x, ddepth, 1, 0);
		Core.convertScaleAbs(grad_x, abs_grad_x);

		// Gradient Y
		// Imgproc.Sobel(grayImage, grad_y, ddepth, 0, 1, 3, scale,
		// this.threshold.getValue(), Core.BORDER_DEFAULT );
		Imgproc.Sobel(grayImage, grad_y, ddepth, 0, 1);
		Core.convertScaleAbs(grad_y, abs_grad_y);

		// Total Gradient (approximate)
		Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, detectedEdges);
		// Core.addWeighted(grad_x, 0.5, grad_y, 0.5, 0, detectedEdges);

		return detectedEdges;

	}

	@FXML
	protected void isSelected() {
		if (this.canny.isSelected())
			this.threshold.setDisable(false);
		else
			this.threshold.setDisable(true);
		this.cameraButton.setDisable(false);
	}

	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}

		if (this.capture.isOpened()) {
			this.capture.release();
		}
	}

	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	protected void setClosed() {
		this.stopAcquisition();
	}

}