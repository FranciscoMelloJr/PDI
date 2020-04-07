package view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class PrincipalController {

	@FXML
	ImageView imageView1;
	@FXML
	ImageView imageView2;
	@FXML
	ImageView imageView3;

	@FXML
	Label labelR;
	@FXML
	Label labelG;
	@FXML
	Label labelB;

	@FXML
	TextField textFieldR;
	@FXML
	TextField textFieldG;
	@FXML
	TextField textFieldB;

	@FXML
	Slider sliderLimiar;
	@FXML
	Slider sliderR;
	@FXML
	Slider sliderG;
	@FXML
	Slider sliderB;

	private Image img1;
	private Image img2;
	private Image img3;

	@FXML
	private Label labelValorLimiar;

	@FXML
	public void abrirImagem1() {
		img1 = abreImagem(imageView1, img1);
	}

	@FXML
	public void abrirImagem2() {
		img2 = abreImagem(imageView2, img2);
	}

	private void atualizaImagem3() {
		imageView3.setImage(img3);
		imageView3.setFitWidth(img3.getWidth());
		imageView3.setFitHeight(img3.getHeight());
	}

	@FXML
	public void CinzaAritmetica() {
		img3 = Pdi.cinzaMediaAritmetica(img1, 0, 0, 0);
		atualizaImagem3();
	}

	@FXML
	public void cinzaPonderada() {
		int pR, pG, pB;
		pR = (int) (sliderR.getValue() / 3);
		pG = (int) (sliderG.getValue() / 3);
		pB = (int) (sliderB.getValue() / 3);
		img3 = Pdi.cinzaMediaAritmetica(img1, pR, pG, pB);
		atualizaImagem3();
	}

	@FXML
	public void Limiarizacao() {

		double value = sliderLimiar.getValue() / 250;
		img3 = Pdi.limiarizacao(img1, value);
		atualizaImagem3();
	}

	@FXML
	public void valorLimiar() {
		Double valor = sliderLimiar.getValue();
		labelValorLimiar.setText(valor.toString());
	}

	@FXML
	public void limiarizarImagem() {
		img3 = Pdi.limiarizacao(img1, sliderLimiar.getValue());
		atualizaImagem3();
	}

	@FXML
	public void negativa() {
		img3 = Pdi.negativa(img1);
		atualizaImagem3();
	}

	private Image abreImagem(ImageView imageView, Image image) {
		File f = selecionaImagem();
		if (f != null) {
			image = new Image(f.toURI().toString());
			imageView.setImage(image);
			imageView.setFitWidth(image.getWidth());
			imageView.setFitHeight(image.getHeight());
			return image;
		}
		return null;
	}

	private File selecionaImagem() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.jpg", "*.JPG", "*.png",
				"*.PNG", "*.gif", "*.GIF", "*.bmp", "*.BMP"));

		fileChooser.setInitialDirectory(new File("C:\\Users\\CPP\\Pictures\\img"));

		File imgSelec = fileChooser.showOpenDialog(null);
		try {
			if (imgSelec != null) {
				return imgSelec;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void verificaCor(Image img, int x, int y) {
		try {
			Color cor = img.getPixelReader().getColor(x - 1, y - 1);
			labelR.setText("R: " + (int) (cor.getRed() * 255));
			labelG.setText("G: " + (int) (cor.getGreen() * 255));
			labelB.setText("B: " + (int) (cor.getBlue() * 255));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void rasterImg(MouseEvent evt) {
		ImageView iv = (ImageView) evt.getTarget();
		if (iv.getImage() != null) {
			verificaCor(iv.getImage(), (int) evt.getX(), (int) evt.getY());
		}
	}

}
