package view;

import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Pdi {

	public static Image cinzaMediaAritmetica(Image imagem, int pcR, int pcG, int pcB) {
		try {
			int w = (int) imagem.getWidth();
			int h = (int) imagem.getHeight();

			PixelReader pr = imagem.getPixelReader();
			WritableImage wi = new WritableImage(w, h);
			PixelWriter pw = wi.getPixelWriter();

			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color corA = pr.getColor(i, j);
					double media = (corA.getRed() + corA.getGreen() + corA.getBlue()) / 3;
					if (pcR != 0 || pcG != 0 || pcB != 0)
						media = (corA.getRed() * pcR + corA.getGreen() * pcG + corA.getBlue() * pcB) / 100;
					Color corN = new Color(media, media, media, corA.getOpacity());
					pw.setColor(i, j, corN);
				}
			}
			return wi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Image limiarizacao(Image imagem, double limiar) {
		try {
			int w = (int) imagem.getWidth();
			int h = (int) imagem.getHeight();

			PixelReader pr = imagem.getPixelReader();
			WritableImage wi = new WritableImage(w, h);
			PixelWriter pw = wi.getPixelWriter();

			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color corA = pr.getColor(i, j);
					Color corN;

					if (corA.getRed() >= limiar) {
						corN = new Color(1, 1, 1, corA.getOpacity());
					} else {
						corN = new Color(0, 0, 0, corA.getOpacity());
					}

					pw.setColor(i, j, corN);
				}
			}
			return wi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Image negativa(Image imagem) {
		try {
			int w = (int) imagem.getWidth();
			int h = (int) imagem.getHeight();

			PixelReader pr = imagem.getPixelReader();
			WritableImage wi = new WritableImage(w, h);
			PixelWriter pw = wi.getPixelWriter();

			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color corA = pr.getColor(i, j);
					Color corN = new Color(1 - corA.getRed(), 1 - corA.getGreen(), 1 - corA.getBlue(),
							corA.getOpacity());
					pw.setColor(i, j, corN);
				}
			}
			return wi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Image ruidos(Image imagem, int tipoVizinhos) {

		try {
			int w = (int) imagem.getWidth();
			int h = (int) imagem.getHeight();

			PixelReader pr = imagem.getPixelReader();
			WritableImage wi = new WritableImage(w, h);
			PixelWriter pw = wi.getPixelWriter();

			for (int i = 1; i < w - 1; i++) {
				for (int j = 1; j < h - 1; j++) {

					Color corA = pr.getColor(i, j);

					Pixel p = new Pixel(corA.getRed(), corA.getGreen(), corA.getBlue(), i, j);

					buscaVizinhos(imagem, p);

					Pixel vetor[] = null;

					if (tipoVizinhos == Constantes.VIZINHOS3X3)
						;

					vetor = p.viz3;

					if (tipoVizinhos == Constantes.VIZINHOSCRUZ)

						vetor = p.vizC;
					if (tipoVizinhos == Constantes.VIZINHOSX)

						vetor = p.vizX;

					double red = mediana(vetor, Constantes.CANALR);

					double green = mediana(vetor, Constantes.CANALG);

					double blue = mediana(vetor, Constantes.CANALB);

					Color corN = new Color(red, green, blue, corA.getOpacity());

					pw.setColor(i, j, corN);
				}
			}

			return wi;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private static void buscaVizinhos(Image imagem, Pixel p) {
		p.vizX = buscaVizinhosX(imagem, p);
		p.vizC = buscaVizinhosC(imagem, p);
		p.viz3 = buscaVizinhos3(imagem, p);
	}

	private static Pixel[] buscaVizinhosX(Image imagem, Pixel p) {

		Pixel[] vizX = new Pixel[5];

		PixelReader pr = imagem.getPixelReader();

		Color cor = pr.getColor(p.x - 1, p.y + 1);
		vizX[0] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x - 1, p.y + 1);

		cor = pr.getColor(p.x + 1, p.y - 1);
		vizX[1] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x + 1, p.y - 1);

		cor = pr.getColor(p.x - 1, p.y - 1);
		vizX[2] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x - 1, p.y - 1);

		cor = pr.getColor(p.x + 1, p.y + 1);
		vizX[3] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x + 1, p.y + 1);

		vizX[4] = p;

		return vizX;
	}

	private static Pixel[] buscaVizinhosC(Image imagem, Pixel p) {

		Pixel[] vizC = new Pixel[5];

		PixelReader pr = imagem.getPixelReader();

		Color cor = pr.getColor(p.x, p.y - 1);
		vizC[0] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x, p.y - 1);

		cor = pr.getColor(p.x, p.y + 1);
		vizC[1] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x, p.y + 1);

		cor = pr.getColor(p.x - 1, p.y);
		vizC[2] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x - 1, p.y);

		cor = pr.getColor(p.x + 1, p.y);
		vizC[3] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x + 1, p.y);

		vizC[4] = p;

		return vizC;

	}

	private static Pixel[] buscaVizinhos3(Image imagem, Pixel p) {

		Pixel[] viz3 = new Pixel[9];
		PixelReader pr = imagem.getPixelReader();
		Color cor = pr.getColor(p.x, p.y - 1);

		viz3[0] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x, p.y - 1);

		cor = pr.getColor(p.x, p.y + 1);
		viz3[1] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x, p.y + 1);

		cor = pr.getColor(p.x - 1, p.y);
		viz3[2] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x - 1, p.y);

		cor = pr.getColor(p.x + 1, p.y);
		viz3[3] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x + 1, p.y);

		cor = pr.getColor(p.x - 1, p.y + 1);
		viz3[4] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x - 1, p.y + 1);

		cor = pr.getColor(p.x + 1, p.y - 1);

		viz3[5] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x + 1, p.y - 1);

		cor = pr.getColor(p.x - 1, p.y - 1);
		viz3[6] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x - 1, p.y - 1);

		cor = pr.getColor(p.x + 1, p.y + 1);
		viz3[7] = new Pixel(cor.getRed(), cor.getGreen(), cor.getBlue(), p.x + 1, p.y + 1);

		viz3[8] = p;

		return viz3;

	}

	public static double mediana(Pixel[] pixels, int canal) {

		double v[] = new double[pixels.length];

		for (int i = 0; i < pixels.length; i++) {

			if (canal == Constantes.CANALR) {
				v[i] = pixels[i].r;
			}
			if (canal == Constantes.CANALG) {
				v[i] = pixels[i].g;
			}
			if (canal == Constantes.CANALB) {
				v[i] = pixels[i].b;
			}

		}
		v = ordenaVetor(v);
		return v[v.length / 2];
	}

	private static double[] ordenaVetor(double[] v) {
		Arrays.sort(v);
		return v;
	}

	public static Image adicao(Image imagem1, Image imagem2, double ti1, double ti2) {

		int w1 = (int) imagem1.getWidth();
		int h1 = (int) imagem1.getHeight();
		int w2 = (int) imagem2.getWidth();
		int h2 = (int) imagem2.getHeight();

		int w = Math.min(w1, w2);
		int h = Math.min(h1, h2);

		PixelReader pr1 = imagem1.getPixelReader();
		PixelReader pr2 = imagem2.getPixelReader();

		WritableImage wi = new WritableImage(w, h);

		PixelWriter pw = wi.getPixelWriter();

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {

				Color corImagem1 = pr1.getColor(i, j);
				Color corImagem2 = pr2.getColor(i, j);

				double r = (corImagem1.getRed() * ti1) + (corImagem2.getRed() * ti2);

				double g = (corImagem1.getGreen() * ti1) + (corImagem2.getGreen() * ti2);

				double b = (corImagem1.getBlue() * ti1) + (corImagem2.getBlue() * ti2);

				r = r > 1 ? 1 : r;
				g = g > 1 ? 1 : g;
				b = b > 1 ? 1 : b;

				Color newCor = new Color(r, g, b, 1);
				pw.setColor(i, j, newCor);
			}
		}
		return wi;
	}

	public static Image subtracao(Image imagem1, Image imagem2) {

		int w1 = (int) imagem1.getWidth();
		int h1 = (int) imagem1.getHeight();
		int w2 = (int) imagem2.getWidth();
		int h2 = (int) imagem2.getHeight();

		int w = Math.min(w1, w2);
		int h = Math.min(h1, h2);

		PixelReader pr1 = imagem1.getPixelReader();
		PixelReader pr2 = imagem2.getPixelReader();

		WritableImage wi = new WritableImage(w, h);

		PixelWriter pw = wi.getPixelWriter();

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {

				Color oldCor1 = pr1.getColor(i, j);
				Color oldCor2 = pr2.getColor(i, j);

				double r = (oldCor1.getRed()) - (oldCor2.getRed());

				double g = (oldCor1.getGreen()) - (oldCor2.getGreen());

				double b = (oldCor1.getBlue()) - (oldCor2.getBlue());

				r = r < 0 ? 0 : r;
				g = g < 0 ? 0 : g;
				b = b < 0 ? 0 : b;

				Color newCor = new Color(r, g, b, oldCor1.getOpacity());
				pw.setColor(i, j, newCor);
			}
		}
		return wi;
	}

	public static Image marcarImagem(Image img, int minX, int minY, int maxX, int maxY) {

		int w = (int) img.getWidth();
		int h = (int) img.getHeight();

		PixelReader pr = img.getPixelReader();
		WritableImage wi = new WritableImage(w, h);
		PixelWriter pw = wi.getPixelWriter();

		Color newCor = new Color(0, 0, 1, 1);

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				pw.setColor(i, j, pr.getColor(i, j));
			}
		}

		// LINHA X
		for (int x = minX; x <= maxX; x++) {
			pw.setColor(x, minY, newCor);
			pw.setColor(x, minY - 1, newCor);
			pw.setColor(x, minY + 1, newCor);
			pw.setColor(x, maxY, newCor);
			pw.setColor(x, maxY - 1, newCor);
			pw.setColor(x, maxY + 1, newCor);
		}
		// LINHA Y
		for (int y = minY; y <= maxY; y++) {
			pw.setColor(minX, y, newCor);
			pw.setColor(minX - 1, y, newCor);
			pw.setColor(minX + 1, y, newCor);
			pw.setColor(maxX, y, newCor);
			pw.setColor(maxX - 1, y, newCor);
			pw.setColor(maxX + 1, y, newCor);
		}

		return wi;
	}
}