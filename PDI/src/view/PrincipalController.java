package view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class PrincipalController {
	
	@FXML ImageView imageView1;
	@FXML ImageView imageView2;
	@FXML ImageView imageView3;

	@FXML Label labelR;
	@FXML Label labelG;
	@FXML Label labelB;
	
	@FXML Slider slidA;
	
	private Image img1;
	private Image img2;
	private Image img3;
	
	
	@FXML
	private void abreImage1() {
		img1 = abreImage(imageView1, img1);
	}

	@FXML
	private void abreImage2() {
		img2 = abreImage(imageView2, img2);
	}

	@FXML
	private void atualizaImage3() {
		imageView3.setImage(img3);
		imageView3.setFitHeight(img3.getHeight());
		imageView3.setFitWidth(img3.getWidth());
	}
	
	@FXML
	public void rasterImg(MouseEvent evt) {
		ImageView iv = (ImageView) evt.getTarget();
		if(iv.getImage() != null) {
			verificaCor(iv.getImage(), (int) evt.getX(), (int) evt.getY());
		}
	}
	
	@FXML
	public void converCinzaArietmetica() {
		img3 = Pdi.cinzaMediaAritmetica(img1, 0, 0, 0);
		atualizaImage3();
	}
	
	@FXML
	public void fazerLimiarizacao() {
		double value;
		
		value = slidA.getValue() / 250;
		img3 = Pdi.limiarizacao(img1, value);
		
		atualizaImage3();
	}
	
	@FXML
	public void fazerNegativa() {
		img3 = Pdi.negativa(img1);
		atualizaImage3();
	}
	
	private void verificaCor(Image img, int x, int y){
		try {
			Color cor = img.getPixelReader().getColor(x-1, y-1);
			labelR.setText("R: "+(int) (cor.getRed()*255));
			labelG.setText("G: "+(int) (cor.getGreen()*255));
			labelB.setText("B: "+(int) (cor.getBlue()*255));
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	private Image abreImage(ImageView imageView, Image image) {

		File file;
		
		file = selecionaImagem();
		if(file != null) {
			image = new Image(file.toURI().toString());
			imageView.setImage(image);
			imageView.setFitWidth(image.getWidth());
			imageView.setFitHeight(image.getHeight());
			return image;
		}
		
		return null;
	}

	private File selecionaImagem() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter(
						"Imagens", "*.jpg", "*.JPG", "*.png",
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
	
}
