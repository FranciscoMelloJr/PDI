package view;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import org.opencv.imgproc.Imgproc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;

public class PrincipalController {

	@FXML
	private Button button;
	@FXML
	private ImageView currentFrame;
	@FXML
	private ImageView histograma;

	@FXML
	private CheckBox grayscale, logoCheckBox;

	private Mat frame, logo;

	Rect roi = new Rect(frame.cols() - logo.cols(), frame.rows() - logo.rows(), logo.cols(), logo.rows());
	Mat imageROI = frame.submat(roi);

	@FXML
	protected void startCamera(ActionEvent event) {
	}

	@FXML
	public void cinza() {
		if (grayscale.isSelected()) {
			Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
		}
	}

	@FXML
	protected void loadLogo() {
		if (logoCheckBox.isSelected() && this.logo != null) {
			Rect roi = new Rect(frame.cols() - logo.cols(), frame.rows() - logo.rows(), logo.cols(), logo.rows());
			Mat imageROI = frame.submat(roi);
			// add the logo: method #1

			Core.addWeighted(imageROI, 1.0, logo, 0.7, 0.0, imageROI);
			// add the logo: method #2
			// Mat mask = logo.clone();
			// logo.copyTo(imageROI, mask);
		}
	}
}
