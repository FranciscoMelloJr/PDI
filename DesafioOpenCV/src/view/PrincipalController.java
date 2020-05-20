package view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Utils;

public class PrincipalController {

	@FXML
	private Button button;
	@FXML
	private ImageView currentFrame;
	@FXML
	private ImageView histogram;
	@FXML
	private CheckBox grayscale;
	@FXML
	private CheckBox logoCheckBox;

	private Mat logo;

	// contador do video
	private ScheduledExecutorService timer;
	// captura de video do OpenCV
	private VideoCapture capture;
	// flag para mudar a ativação do botão
	private boolean cameraActive;

	public void initialize() {
		this.capture = new VideoCapture();
		this.cameraActive = false;
	}

	@FXML
	protected void startCamera() {

		this.currentFrame.setFitWidth(600);
		this.currentFrame.setPreserveRatio(true);

		// inicia a gravação
		if (!this.cameraActive) {
			this.capture.open(0);
			// gravação disponivel s/n
			if (this.capture.isOpened()) {
				this.cameraActive = true;
				// 30fps - 1 frame a cada 33ms
				Runnable frameGrabber = new Runnable() {
					@Override
					public void run() {
						// coleta o frame
						Mat frame = grabFrame();
						// converte e mostra
						Image imageToShow = Utils.matImage(frame);
						updateImageView(currentFrame, imageToShow);
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				// Atualiza a label do botão
				this.button.setText("Parar Camera");
			} else {
				System.err.println("Não foi possível abrir a camera...");
			}
		} else {
			// Sem camera
			this.cameraActive = false;
			// Atualiza a label do botão
			this.button.setText("iniciar Camera");
			// Parar o contador
			this.stopAcquisition();
		}
	}

	// Pausa histrograma
	@FXML
	protected void loadLogo() {
		if (logoCheckBox.isSelected()) {
			this.logo = Imgcodecs.imread("resources/Poli.png");
		}

	}

	private Mat grabFrame() {
		Mat frame = new Mat();

		if (this.capture.isOpened()) {
			try {
				this.capture.read(frame);
				if (!frame.empty()) {
					// adiciona uma logo
					if (logoCheckBox.isSelected() && this.logo != null) {
						Rect roi = new Rect(frame.cols() - logo.cols(), frame.rows() - logo.rows(), logo.cols(),
								logo.rows());
						Mat imageROI = frame.submat(roi);
						logo.copyTo(imageROI, logo);
						// Core.addWeighted(imageROI, 1.0, logo, 0.8, 0.0, imageROI);
					}
					if (grayscale.isSelected()) {
						Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
					}
					this.showHistogram(frame, grayscale.isSelected());
				}
			} catch (Exception e) {
				System.err.println("Erro -> " + e);
			}
		}

		return frame;
	}

	private void showHistogram(Mat frame, boolean gray) {
		// Criar uma lsita de imagens
		List<Mat> images = new ArrayList<Mat>();
		Core.split(frame, images);

		// Tamanho do histograma
		MatOfInt histSize = new MatOfInt(256);
		// Um canal
		MatOfInt channels = new MatOfInt(0);
		// Setar o alcance
		MatOfFloat histRange = new MatOfFloat(0, 256);

		// Setar RGB
		Mat hist_b = new Mat();
		Mat hist_g = new Mat();
		Mat hist_r = new Mat();

		// B
		Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);

		// G e R caso não for cinza
		if (!gray) {
			Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);
			Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);
		}

		// Escrever histograma
		int hist_w = 150;
		int hist_h = 150;
		int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);

		Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0, 0, 0));
		Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());

		if (!gray) {
			Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
			Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		}

		// Finalização do histograma
		for (int i = 1; i < histSize.get(0, 0)[0]; i++) {
			// Azul ou Cinza
			Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
					new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])), new Scalar(255, 0, 0), 2, 8, 0);
			// G R caso não for cinza
			if (!gray) {
				Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])), new Scalar(0, 255, 0), 2, 8,
						0);
				Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
						new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])), new Scalar(0, 0, 255), 2, 8,
						0);
			}
		}
		Image histImg = Utils.matImage(histImage);
		updateImageView(histogram, histImg);
	}

	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.err.println("Erro na captura, reincie a camera... " + e);
			}
		}

		if (this.capture.isOpened()) {
			// release the camera
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
