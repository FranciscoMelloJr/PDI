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
	private RadioButton canny, sobel, laplace;
	@FXML
	private Slider threshold;

	private ScheduledExecutorService timer;
	private VideoCapture capture = new VideoCapture();
	private boolean cameraActive;

	Point clickedPoint = new Point(0, 0);
	Mat oldFrame;

	@FXML
	protected void startCamera() {

		originalFrame.setFitWidth(380);
		originalFrame.setPreserveRatio(true);

		originalFrame.setOnMouseClicked(e -> {
			System.out.println("[" + e.getX() + ", " + e.getY() + "]");
			clickedPoint.x = e.getX();
			clickedPoint.y = e.getY();
		});

		if (!this.cameraActive) {

			this.capture.open(0);

			if (this.capture.isOpened()) {
				this.cameraActive = true;

				Runnable frameGrabber = new Runnable() {

					@Override
					public void run() {

						Mat frame = grabFrame();
						Image imageToShow = Utils.matImage(frame);
						updateImageView(originalFrame, imageToShow);
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				this.cameraButton.setText("Parar câmera");
			} else {

				System.err.println("Falha de conexão com a câmera...");
			}
		} else {

			this.cameraActive = false;
			this.cameraButton.setText("Iniciar vídeo");
			this.canny.setDisable(false);

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
						frame = this.doSobel(frame);
					}
					if (this.laplace.isSelected()) {
						frame = this.doLaplace(frame);
					}
				}
			} catch (Exception e) {
				System.err.print("Não foi possível coletar a imagem...");
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

	public Mat doLaplace(Mat frame) {

		Mat src = frame, src_gray = new Mat(), dst = new Mat();
		int kernel_size = 3;
		int scale = 1;
		int delta = 0;
		int ddepth = CvType.CV_16S;

		if (src.empty()) {
			System.out.println("Erro ao abrir imagem");
		}

		Imgproc.GaussianBlur(src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);
		Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_RGB2GRAY);

		Mat abs_dst = new Mat();
		Imgproc.Laplacian(src_gray, dst, ddepth, kernel_size, scale, delta, Core.BORDER_DEFAULT);
		Core.convertScaleAbs(dst, abs_dst);

		return abs_dst;
	}

	private Mat doCanny(Mat frame) {
		
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
				System.err.println("Falha em parar a camptura de frame, tentando para a câmera... " + e);
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