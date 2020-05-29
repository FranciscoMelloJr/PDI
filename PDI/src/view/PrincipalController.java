package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.Utils;

public class PrincipalController {

	@FXML
	ImageView imageView1, imageView2, imageView3;

	@FXML
	Label labelR, labelG, labelB, quadrado;

	@FXML
	Slider sliderLimiar, sliderR, sliderG, sliderB, threshold;

	@FXML
	RadioButton vizinhoC, vizinhoX, vizinho3, canny, sobel, laplace;

	@FXML
	Slider sliderIMG1, sliderIMG2;

	@FXML
	TextField colunas, segundoQ, primeiroQ;

	private Image img1, img2, img3;

	int x1, y1, x2, y2;

	@FXML
	public void identificaQuadrado() {

		if (Pdi.identificarQuebra(img1)) {
			quadrado.setText("Quebrado");
		} else {
			quadrado.setText("Inteiro");
		}
	}

	@FXML
	public void meiaEqualizacao() {
		img3 = Pdi.halfEqualizacaoHistograma(img1, true);
		atualizaImagem3();
	}

	@FXML
	public void dividirQuatro() {
		img3 = Pdi.dividirQuadrantes(img1, Double.parseDouble(primeiroQ.getText()),
				Double.parseDouble(segundoQ.getText()));
		atualizaImagem3();
	}

	@FXML
	public void equalizacao() {
		img3 = Pdi.equalizacaoHistograma(img1, true);
		atualizaImagem3();
	}

	@FXML
	public void equalizaoValidos() {
		img3 = Pdi.equalizacaoHistograma(img1, false);
		atualizaImagem3();
	}

	@FXML
	public void abreHistograma(ActionEvent event) {
		try {
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Histograma.fxml"));
			Parent root = loader.load();
			stage.setScene(new Scene(root));
			stage.setTitle("Histograma");
			// stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(((Node) event.getSource()).getScene().getWindow());
			stage.show();
			HistogramaController controller = (HistogramaController) loader.getController();

			if (img1 != null)
				Pdi.getGrafico(img1, controller.grafico1);
			if (img2 != null)
				Pdi.getGrafico(img2, controller.grafico2);
			if (img3 != null)
				Pdi.getGrafico(img3, controller.grafico3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void abreVideo(ActionEvent event) {
		try {
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Video.fxml"));
			Parent root = loader.load();
			stage.setScene(new Scene(root, 600, 500));
			stage.setTitle("Video");
			// stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(((Node) event.getSource()).getScene().getWindow());
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
			// Devido comparação com getWidth, imagens de mesmo tamanho podem ser
			// substituídas
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
	public void morphologyEx() {

		Mat matriz = Utils.imageMat(img1);

		Mat erosao = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));
		Mat dilatacao = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));

		Mat abreElemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3), new Point(1, 1));
		Mat fechaElemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7), new Point(3, 3));

	//	Imgproc.morphologyEx(matriz, matriz, Imgproc.MORPH_OPEN, erosao);
		Imgproc.morphologyEx(matriz, matriz, Imgproc.MORPH_OPEN, dilatacao);
	//	Imgproc.morphologyEx(matriz, matriz, Imgproc.MORPH_OPEN, abreElemento);
	//	Imgproc.morphologyEx(matriz, matriz, Imgproc.MORPH_CLOSE, fechaElemento);

		Imgproc.dilate(matriz, dilatacao, dilatacao);

		updateCurrentImage(Utils.matImage(matriz));
		updateCurrentImage(imageView3, img3);
	}

	@FXML
	public void doDilate() {
		Mat img = Utils.imageMat(img1);
		img = dilate(img);
		updateCurrentImage(Utils.matImage(img));
		updateCurrentImage(imageView3, img3);
	}

	public Mat dilate(Mat matriz) {
		Mat matrizDilate = new Mat();
		Imgproc.dilate(matriz, matrizDilate, matrizDilate);
		return matrizDilate;
	}

	// FILTRO MORFOLOGICO
	public Mat erode(Mat matriz) {
		Mat matrizErode = new Mat();
		Imgproc.erode(matriz, matrizErode, matrizErode);
		return matrizErode;
	}

	@FXML
	public void sobelSelecionado() {
		isSelected();
		if (this.sobel.isSelected()) {
			fazerSobel(Utils.imageMat(img1));
		}
		updateCurrentImage(imageView3, img3);
	}

	@FXML
	public void cannySelecionado() {
		isSelected();
		if (this.canny.isSelected()) {
			fazerCanny(Utils.imageMat(img1));
		}
		updateCurrentImage(imageView3, img3);
	}

	@FXML
	public void laplaceSelecionado() {
		isSelected();
		if (this.laplace.isSelected()) {
			fazerLaplace(Utils.imageMat(img1));
		}
		updateCurrentImage(imageView3, img3);
	}

	private void fazerSobel(Mat frame) {
		Mat grayMat = new Mat();
		Mat sobel = new Mat();

		Mat grad_x = new Mat();
		Mat abs_grad_x = new Mat();

		Mat grad_y = new Mat();
		Mat abs_grad_y = new Mat();

		Imgproc.cvtColor(frame, grayMat, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);
		Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);
		Core.convertScaleAbs(grad_x, abs_grad_x);
		Core.convertScaleAbs(grad_y, abs_grad_y);
		Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);

		updateCurrentImage(Utils.matImage(sobel));
	}

	public void fazerCanny(Mat frame) {

		Mat grayImage = new Mat();
		Mat detectedEdges = new Mat();

		Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
		Imgproc.Canny(detectedEdges, detectedEdges, this.threshold.getValue(), this.threshold.getValue() * 3);

		Mat dest = new Mat();
		frame.copyTo(dest, detectedEdges);
		updateCurrentImage(Utils.matImage(detectedEdges));

	}

	public void fazerLaplace(Mat frame) {

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

		/**
		 * Mostrar em uma nova aba. HighGui.imshow(window_name, abs_dst);
		 * HighGui.waitKey(0);
		 **/
		updateCurrentImage(Utils.matImage(abs_dst));
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
	public void zebrar() {
		img3 = Pdi.cinzaMediaAritmeticaZebrada(img1, 0, 0, 0, Integer.parseInt(colunas.getText()));
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

	private void updateCurrentImage(ImageView imageView, Image image) {
		imageView.setImage(image);
	}

	private void updateCurrentImage(Image image) {
		img3 = image;
	}

	public void isSelected() {
		if (this.canny.isSelected())
			this.threshold.setDisable(false);
		else
			this.threshold.setDisable(true);
	}

	@FXML
	public void limpar() {

		this.canny.setSelected(false);
		this.sobel.setSelected(false);
		this.laplace.setSelected(false);

	}

}