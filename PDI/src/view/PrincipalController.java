package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class PrincipalController {

	@FXML
	ImageView imageView1, imageView2, imageView3;

	@FXML
	Label labelR, labelG, labelB;

	@FXML
	Slider sliderLimiar, sliderR, sliderG, sliderB;

	@FXML
	RadioButton vizinhoC, vizinhoX, vizinho3;

	@FXML
	private Slider sliderIMG1, sliderIMG2;

	private Image img1, img2, img3;

	int x1, y1, x2, y2;

	@FXML
	public void mouseClick(MouseEvent event) {
		ImageView img = (ImageView) event.getTarget();
		if (img.getImage() != null) {
			x1 = (int) event.getX();
			y1 = (int) event.getY();
		}
	}

	@FXML
	public void mouseUnclick(MouseEvent event) {
		ImageView img = (ImageView) event.getTarget();
		if (img.getImage() != null) {
			x2 = (int) event.getX();
			y2 = (int) event.getY();
			Image imgX = img.getImage();
			// MARCAÇÕES FIXAS EM CADA IMAGEM
//			img.setImage(
//			Pdi.marcarImagem(imgX, Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2),
//			Math.max(y1, y2)));
//		}

			// MARCAÇÕES SEPARADAS EM CADA IMAGEM
			// Devido comparação com getWidth, imagens de mesmo tamanho podem ser substituídas
			if (imgX.getWidth() == img1.getWidth()) {
				img.setImage(
						Pdi.marcarImagem(img1, Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2)));
			}
			if (imgX.getWidth() == img2.getWidth()) {
				img.setImage(
						Pdi.marcarImagem(img2, Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2)));
			}
			if (imgX.getWidth() == img3.getWidth()) {
				img.setImage(
						Pdi.marcarImagem(img3, Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2)));
			}
		}
	}

	@FXML
	public void aplicarAdicao() {
		img3 = Pdi.adicao(img1, img2, sliderIMG1.getValue() / 100, sliderIMG2.getValue() / 100);
		atualizaImagem3();
	}

	@FXML
	public void aplicarSubtracao() {
		img3 = Pdi.subtracao(img1, img2);
		atualizaImagem3();
	}

	@FXML
	public void eliminarRuidos() {

		int op = 3;

		if (vizinho3.isSelected())
			op = 1;
		if (vizinhoC.isSelected())
			op = 2;
		if (vizinhoX.isSelected())
			op = 3;

		img3 = Pdi.ruidos(img1, op);
		atualizaImagem3();
	}

	@FXML
	public void negativa() {
		img3 = Pdi.negativa(img1);
		atualizaImagem3();
	}

	@FXML
	public void limiarizar() {
		img3 = Pdi.limiarizacao(img1, sliderLimiar.getValue() / 100);
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
	public void CinzaAritmetica() {
		img3 = Pdi.cinzaMediaAritmetica(img1, 0, 0, 0);
		atualizaImagem3();
	}

	@FXML
	public void rasterImg(MouseEvent evt) {
		ImageView iv = (ImageView) evt.getTarget();
		if (iv.getImage() != null) {
			verificaCor(iv.getImage(), (int) evt.getX(), (int) evt.getY());
		}
	}

	private void verificaCor(Image img, int x, int y) {
		try {
			Color cor = img.getPixelReader().getColor(x - 1, y - 1);
			labelR.setText("RED: " + (int) (cor.getRed() * 255));
			labelG.setText("GREEN: " + (int) (cor.getGreen() * 255));
			labelB.setText("BLUE: " + (int) (cor.getBlue() * 255));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

		fileChooser.setInitialDirectory(
				new File("C:\\Users\\Franc\\OneDrive - UNISUL\\8º Semestre\\Processamento Digital de Imagens\\imgs"));

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
	public void salvarImagem() {
		if (img3 != null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagem", "*.png"));
			File file = fileChooser.showSaveDialog(null);

			if (file != null) {
				BufferedImage bImg = SwingFXUtils.fromFXImage(img3, null);
//				fileChooser.setInitialDirectory(new File(
//						"C:/Users/Franc/OneDrive - UNISUL/8º Semestre/Processamento Digital de Imagens/Resultados de Imagens"));
				try {
					ImageIO.write(bImg, "PNG", file);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}