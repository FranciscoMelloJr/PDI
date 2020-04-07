package Principal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Principal.Pdi;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

public class PrincipalController {

	@FXML
	ImageView imageview1;
	@FXML
	ImageView imageview2;
	@FXML
	ImageView imageview3;

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
	RadioButton rbVizC;
	@FXML
	RadioButton rbVizX;
	@FXML
	RadioButton rbViz3;
	
	@FXML
	Button butAd;
	@FXML
	Button butSub;

	private Image imagem1;
	private Image imagem2;
	private Image imagem3;

	@FXML
	private Slider limiarizacaoSlider;

	@FXML
	private Button limiarizarB;

	@FXML
	private Slider slider1;

	@FXML
	private Slider slider2;
	
	@FXML
	public void aplicarAdicao() {
		imagem3 = Pdi.adicao(imagem1, imagem2, slider1.getValue()/100, slider2.getValue()/100);
		atualizaImagem3();
	}
	
	@FXML
	public void aplicarSubtracao() {
		imagem3 = Pdi.subtracao(imagem1, imagem2);
		atualizaImagem3();
	}
	

	@FXML
	public void aplicarNegativa() {
		imagem3 = Pdi.aplicarNegativa(imagem1);
		atualizaImagem3();
	}

	/*
	 * @FXML private Label labelValorLimiar;
	 * 
	 * @FXML public void valorLimiar() { Double valor =
	 * limiarizacaoSlider.getValue(); labelValorLimiar.setText(valor.toString()); }
	 */

	@FXML
	public void limiarizarImagem() {
		imagem3 = Pdi.limiarizacao(imagem1, limiarizacaoSlider.getValue()/100);
		atualizaImagem3();
	}

	@FXML
	public void abrirImagem1() {
		imagem1 = abreImagem(imageview1, imagem1);
	}

	@FXML
	public void abrirImagem2() {
		imagem2 = abreImagem(imageview2, imagem2);
	}

	@FXML
	public void cinzaA() {
		imagem3 = Pdi.cinzaMediaAritmetica(imagem1, 0, 0, 0);
		atualizaImagem3();
	}

	@FXML
	public void cinzaP() {
		int pR, pG, pB;
		pR = Integer.parseInt(textFieldR.getText().toString()) / 3;
		pG = Integer.parseInt(textFieldG.getText().toString()) / 3;
		pB = Integer.parseInt(textFieldB.getText().toString()) / 3;

		imagem3 = Pdi.cinzaMediaAritmetica(imagem1, pR, pG, pB);
		atualizaImagem3();
	}

	private void atualizaImagem3() {
		imageview3.setImage(imagem3);
		imageview3.setFitWidth(imagem3.getWidth());
		imageview3.setFitHeight(imagem3.getHeight());
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

		fileChooser.setInitialDirectory(new File("C:/Users/Cliente/Desktop/imgs"));
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

		ImageView iw = (ImageView) evt.getTarget();
		if (iw.getImage() != null)
			verificaCor(iw.getImage(), (int) evt.getX(), (int) evt.getY());
	}

	@FXML
	public void eliminarRuidos() {

		int op = 3;

		if (rbViz3.isSelected())
			op = 1;
		if (rbVizC.isSelected())
			op = 2;
		if (rbVizX.isSelected())
			op = 3;

		imagem3 = Pdi.ruidos(imagem1, op);
		atualizaImagem3();
	}

	@FXML
	public void salvarImagem() {
		if (imagem3 != null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagem", "*.png"));
			File file = fileChooser.showSaveDialog(null);

			if (file != null) {
				BufferedImage bImg = SwingFXUtils.fromFXImage(imagem3, null);

				try {
					ImageIO.write(bImg, "PNG", file);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	@FXML 
	public void abreHistograma(ActionEvent event) {
		try {
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Histogramas.fxml"));
			Parent root = loader.load();
			stage.setScene(new Scene(root));
			stage.setTitle("Histograma");
			stage.initOwner(((Node)event.getSource()).getScene().getWindow());
			stage.show();
			HistogramasController controller = (HistogramasController)loader.getController();
			
			if(imagem1!=null) 
				Pdi.getGrafico(imagem1, controller.grafico1);
			if(imagem2!=null)
				Pdi.getGrafico(imagem2, controller.grafico2);
			if(imagem3!=null)
				Pdi.getGrafico(imagem3, controller.grafico3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
