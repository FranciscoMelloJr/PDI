package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class PrincipalController {

	@FXML
	ImageView imageview1;
	@FXML
	ImageView imageview2;
	@FXML
	ImageView imageview3;

	@FXML
	Label lblR;
	@FXML
	Label lblG;
	@FXML
	Label lblB;

	@FXML
	Slider slidA;
	@FXML
	Slider slidImg1;
	@FXML
	Slider slidImg2;

	@FXML
	private TextField txtR;

	@FXML
	private TextField txtG;

	@FXML
	private TextField txtB;

	@FXML
	private RadioButton rdCruz;

	@FXML
	private RadioButton rdX;

	@FXML
	private RadioButton rd3X3;

	private Image img1;
	private Image img2;
	private Image img3;

	@FXML
	private Slider limiarizacao;

	@FXML
	private Label lblLimiarizacao;

	@FXML
	public void atualizaLabel() {
		Double v = limiarizacao.getValue();
		lblLimiarizacao.setText(v.toString());
	}

	@FXML
	public void cinzaAritmetica() {
		img3 = Pdi.cinzaMediaAritmetica(img1, 0, 0, 0);
		atualizaImagem3();

	}

	@FXML
	public void cinzaPonderada() {
		int ponderadaR, ponderadaG, ponderadaB;
		ponderadaR = Integer.parseInt(txtR.getText().toString()) / 3;
		ponderadaG = Integer.parseInt(txtG.getText().toString()) / 3;
		ponderadaB = Integer.parseInt(txtB.getText().toString()) / 3;

		img3 = Pdi.cinzaMediaAritmetica(img1, ponderadaR, ponderadaG, ponderadaB);
		atualizaImagem3();
	}

	@FXML
	public void limiarizarImagem() {
		img3 = Pdi.limiarizacao(img1, limiarizacao.getValue());
		atualizaImagem3();
	}

	@FXML
	public void negativa() {
		img3 = Pdi.negativa(img1);
		atualizaImagem3();
	}

	@FXML
	public void ruidos() {

		int op = 3;

		if (rd3X3.isSelected())
			op = 1;
		if (rdCruz.isSelected())
			op = 2;
		if (rdX.isSelected())
			op = 3;

		img3 = Pdi.ruidos(img1, op);
		atualizaImagem3();
	}

	@FXML
	public void rasterImg(MouseEvent evt) {
		ImageView iw = (ImageView) evt.getTarget();
		if (iw.getImage() != null)
			verificaCor(iw.getImage(), (int) evt.getX(), (int) evt.getY());
	}

	private void verificaCor(Image img, int x, int y) {
		try {
			Color cor = img.getPixelReader().getColor(x - 1, y - 1);

			lblR.setText("R: " + (int) (cor.getRed() * 255));
			lblG.setText("G: " + (int) (cor.getGreen() * 255));
			lblB.setText("B: " + (int) (cor.getBlue() * 255));
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	@FXML
	public void abreImagem1() {
		img1 = abreImagem(imageview1, img1);
	}

	@FXML
	public void abreImagem2() {
		img2 = abreImagem(imageview2, img2);
	}

	private void atualizaImagem3() {
		imageview3.setImage(img3);
		imageview3.setFitWidth(img3.getWidth());
		imageview3.setFitHeight(img3.getHeight());
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

		// ABRE IMAGEM DO DIRETORIO 
		fileChooser.setInitialDirectory(
				new File("C:/Users/davic/OneDrive/Documentos/Unisul/8-Semestre/Processamento de Imagens"));
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

	@FXML
	public void Adicao() {

		double slid01;
		double slid02;

		slid01 = slidImg1.getValue() / 100;
		slid02 = slidImg2.getValue() / 100;

		img3 = Pdi.adicao(img1, img2, slid01, slid02);
		atualizaImagem3();
	}

	@FXML
	public void Subtracao() {

		double slid01;
		double slid02;

		slid01 = slidImg1.getValue() / 100;
		slid02 = slidImg2.getValue() / 100;

		img3 = Pdi.subtracao(img1, img2, slid01, slid02);
		atualizaImagem3();
	}

	@FXML
	public void salvar() {
		if (img3 != null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagem", "*.png"));
			fileChooser.setInitialDirectory(
					new File("C:\\Users\\davic\\OneDrive\\Documentos\\Unisul\\8-Semestre\\Processamento de Imagens"));

			File file = fileChooser.showSaveDialog(null);
			if (file != null) {
				BufferedImage bImg = SwingFXUtils.fromFXImage(img3, null);
				try {
					ImageIO.write(bImg, "PNG", file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {

		}
	}

}
