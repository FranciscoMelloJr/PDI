package view;

import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Pdi {

	public static Mat dilatar(Mat matriz) {

		// Aumentar o tamanho para ter uma maior alcance conforme necessário
		Mat dilatacao = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));

		Imgproc.morphologyEx(matriz, matriz, Imgproc.MORPH_OPEN, dilatacao);
		Imgproc.cvtColor(matriz, matriz, Imgproc.COLOR_BGRA2BGR);

		return matriz;

	}

	public static Mat erodir(Mat matriz) {

		// Aumentar o tamanho para ter uma maior profundidade conforme necessário
		Mat erosao = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));

		Imgproc.morphologyEx(matriz, matriz, Imgproc.MORPH_CLOSE, erosao);
		Imgproc.cvtColor(matriz, matriz, Imgproc.COLOR_BGRA2BGR);

		return matriz;
	}

	public static Mat abrir(Mat matriz) {

		Mat abreElemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3), new Point(1, 1));

		Imgproc.morphologyEx(matriz, matriz, Imgproc.MORPH_OPEN, abreElemento);
		Imgproc.cvtColor(matriz, matriz, Imgproc.COLOR_BGRA2BGR);

		return matriz;
	}

	public static Mat fechar(Mat matriz) {

		Mat fechaElemento = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7), new Point(3, 3));

		Imgproc.morphologyEx(matriz, matriz, Imgproc.MORPH_CLOSE, fechaElemento);
		Imgproc.cvtColor(matriz, matriz, Imgproc.COLOR_BGRA2BGR);

		return matriz;
	}

	public static Mat fazerSobel(Mat frame) {
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

		return sobel;

	}

	public static Mat fazerLaplace(Mat frame) {

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

		return abs_dst;

	}

	public static boolean identificarQuebra(Image img1) {

		int w = (int) img1.getWidth();
		int h = (int) img1.getHeight();

		PixelReader pr = img1.getPixelReader();

		Color preto = new Color(0, 0, 0, 1);
		Color branco = new Color(1, 1, 1, 1);

		int[] sesq = new int[2];
		int[] sdir = new int[2];
		int[] iesq = new int[2];
		int[] idir = new int[2];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {

				try {
					if (pr.getColor(i, j).equals(preto) && pr.getColor(i + 1, j).equals(preto)
							&& pr.getColor(i, j + 1).equals(preto)) {
						sesq[0] = i;
						sesq[1] = j;
					} else if (pr.getColor(i, j).equals(preto) && pr.getColor(i + 1, j).equals(preto)
							&& pr.getColor(i, j - 1).equals(preto)) {
						iesq[0] = i;
						iesq[1] = j;
					} else if (pr.getColor(i, j).equals(preto) && pr.getColor(i - 1, j).equals(preto)
							&& pr.getColor(i, j + 1).equals(preto)) {
						sdir[0] = i;
						sdir[1] = j;
					} else if (pr.getColor(i, j).equals(preto) && pr.getColor(i - 1, j).equals(preto)
							&& pr.getColor(i, j - 1).equals(preto)) {
						idir[0] = i;
						idir[1] = j;
					}
				} catch (NullPointerException e) {
					continue;
				}
			}
		}
		for (int i = sesq[0]; i < sdir[0]; i++) {
			if (pr.getColor(i, sesq[1]).equals(branco)) {
				return true;
			}
		}
		for (int i = iesq[0]; i < idir[0]; i++) {
			if (pr.getColor(i, iesq[1]).equals(branco)) {
				return true;
			}
		}
		for (int j = sesq[1]; j < iesq[1]; j++) {
			if (pr.getColor(sesq[0], j).equals(branco)) {
				return true;
			}
		}
		for (int j = sdir[1]; j < idir[1]; j++) {
			if (pr.getColor(sdir[0], j).equals(branco)) {
				return true;
			}
		}
		return false;
	}

	public static Image halfEqualizacaoHistograma(Image imagem, boolean todos) {

		int w = (int) imagem.getWidth();
		int h = (int) imagem.getHeight();

		PixelReader pr = imagem.getPixelReader();
		WritableImage wi = new WritableImage(w, h);
		PixelWriter pw = wi.getPixelWriter();

		int[] hR = histograma(imagem, Constantes.RED);
		int[] hG = histograma(imagem, Constantes.GREEN);
		int[] hB = histograma(imagem, Constantes.BLUE);

		int[] histAcR = histogramaAc(hR);
		int[] histAcG = histogramaAc(hG);
		int[] histAcB = histogramaAc(hB);

		int qtTonsRed = qtTons(hR);
		int qtTonsGreen = qtTons(hG);
		int qtTonsBlue = qtTons(hB);

		double minR = pontoMin(hR);
		double minG = pontoMin(hG);
		double minB = pontoMin(hB);

		if (todos) {
			qtTonsRed = 255;
			qtTonsGreen = 255;
			qtTonsBlue = 255;

			minR = 0;
			minG = 0;
			minB = 0;
		}

		double n = w * h;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				pw.setColor(i, j, pr.getColor(i, j));
			}
		}

		Color blackColor = new Color(0, 0, 0, 1);

		for (int i = 1; i < w; i++) {
			for (int j = 1; j < h; j++) {
				if (i > j) {
					Color oldCor = pr.getColor(i, j);

					double acR = histAcR[(int) (oldCor.getRed() * 255)];
					double acG = histAcG[(int) (oldCor.getGreen() * 255)];
					double acB = histAcB[(int) (oldCor.getBlue() * 255)];

					double pxR = ((qtTonsRed - 1) / n) * acR;
					double pxG = ((qtTonsGreen - 1) / n) * acG;
					double pxB = ((qtTonsBlue - 1) / n) * acB;

					double corR = (minR + pxR) / 255;
					double corG = (minG + pxG) / 255;
					double corB = (minB + pxB) / 255;

					Color newCor = new Color(corR, corG, corB, oldCor.getOpacity());
					pw.setColor(i, j, newCor);

				}
				if (i == j) {
					pw.setColor(i, j, blackColor);
				}
			}
		}
		return wi;

	}

	public static Image dividirQuadrantes(Image img1, Double primeiroQ, Double segundoQ) {

		int largura = (int) img1.getWidth();
		int altura = (int) img1.getHeight();

		int divLargura = largura / 2;
		int divAltura = altura / 2;

		ArrayList<Color> quadrante1 = new ArrayList<>();
		ArrayList<Color> quadrante2 = new ArrayList<>();
		ArrayList<Color> quadrante3 = new ArrayList<>();
		ArrayList<Color> quadrante4 = new ArrayList<>();

		PixelReader pr = img1.getPixelReader();
		WritableImage wi = new WritableImage(largura, altura);
		PixelWriter pw = wi.getPixelWriter();

		for (int i = 0; i < largura; i++) {
			for (int j = 0; j < altura; j++) {
				pw.setColor(i, j, pr.getColor(i, j));
			}
		}

		if (primeiroQ == 1.0 || segundoQ == 1.0) {
			for (int i = 0; i < divLargura; i++) {
				for (int j = 0; j < divAltura; j++) {
					quadrante1.add(pr.getColor(i, j));
				}
			}

			int count = 0;
			for (int i = (divLargura) - 1; i > 0; i--) {
				for (int j = (divAltura) - 1; j > 0; j--) {
					pw.setColor(i, j, quadrante1.get(count));
					count++;
				}
				count++;
			}
		}

		if (primeiroQ == 2.0 || segundoQ == 2.0) {
			for (int i = divLargura; i < largura; i++) {
				for (int j = 0; j < divAltura; j++) {
					quadrante2.add(pr.getColor(i, j));
				}
			}

			int count = 0;
			for (int i = largura - 1; i > (divLargura) - 1; i--) {
				for (int j = (divAltura) - 1; j > 0; j--) {
					pw.setColor(i, j, quadrante2.get(count));
					count++;
				}
				count++;
			}
		}

		if (primeiroQ == 3.0 || segundoQ == 3.0) {
			for (int i = 0; i < divLargura; i++) {
				for (int j = divAltura; j < altura; j++) {
					quadrante3.add(pr.getColor(i, j));
				}
			}

			int count = 0;
			for (int i = (divLargura) - 1; i > 0; i--) {
				for (int j = altura - 2; j > (divAltura) - 1; j--) {
					pw.setColor(i, j, quadrante3.get(count));
					count++;
				}
				count++;
			}
		}

		if (primeiroQ == 4.0 || segundoQ == 4.0) {
			for (int i = divLargura; i < largura; i++) {
				for (int j = divAltura; j < altura; j++) {
					quadrante4.add(pr.getColor(i, j));
				}
			}

			int count = 0;
			for (int i = largura - 1; i > (divLargura) - 1; i--) {
				for (int j = altura - 2; j > (divAltura) - 1; j--) {
					pw.setColor(i, j, quadrante4.get(count));
					count++;
				}
				count++;
			}
		}

		return wi;

	}

	public static Image cinzaMediaAritmeticaZebrada(Image imagem, int pcR, int pcG, int pcB, int colunas) {
		try {
			int w = (int) imagem.getWidth();
			int h = (int) imagem.getHeight();

			PixelReader pr = imagem.getPixelReader();
			WritableImage wi = new WritableImage(w, h);
			PixelWriter pw = wi.getPixelWriter();

			int largura = w / colunas;
			int esp = 0;

			while (esp <= (w - largura)) {
				for (int i = esp; i < esp + largura; i++) {
					for (int j = 0; j < h; j++) {
						Color corA = pr.getColor(i, j);
						double media = (corA.getRed() + corA.getGreen() + corA.getBlue()) / 3;
						if (pcR != 0 || pcG != 0 || pcB != 0)
							media = (corA.getRed() * pcR + corA.getGreen() * pcG + corA.getBlue() * pcB) / 100;
						Color corN = new Color(media, media, media, corA.getOpacity());
						pw.setColor(i, j, corN);
					}
				}
				esp += largura;
				for (int i = esp; i < esp + largura; i++) {
					for (int j = 0; j < h; j++) {
						Color corA = pr.getColor(i, j);
						Color corN = new Color(corA.getRed(), corA.getGreen(), corA.getBlue(), corA.getOpacity());
						pw.setColor(i, j, corN);
					}
				}
				esp += largura;
			}
			return wi;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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

//	private static int[] histogramaUnico(Image imagem) {
//		int[] qt = new int[256];
//		PixelReader pr = imagem.getPixelReader();
//		int w = (int) imagem.getWidth();
//		int h = (int) imagem.getHeight();
//		for (int i = 0; i < w; i++) {
//			for (int j = 0; j < h; j++) {
//				qt[(int) (pr.getColor(i, j).getRed() * 255)]++;
//				qt[(int) (pr.getColor(i, j).getGreen() * 255)]++;
//				qt[(int) (pr.getColor(i, j).getBlue() * 255)]++;
//			}
//		}
//		return qt;
//	}

	private static int[] histograma(Image imagem, int i) {
		int[] valorCanal = new int[256];
		PixelReader pr = imagem.getPixelReader();
		double w = (int) imagem.getWidth();
		double h = (int) imagem.getHeight();

		if (i == 1) {
			for (int j = 1; j < w; j++) {
				for (int k = 1; k < h; k++) {
					valorCanal[(int) (pr.getColor(j, k).getRed() * 255)]++;
				}

			}
		}

		if (i == 2) {
			for (int j = 1; j < w; j++) {
				for (int k = 1; k < h; k++) {
					valorCanal[(int) (pr.getColor(j, k).getGreen() * 255)]++;
				}

			}
		}

		if (i == 3) {
			for (int j = 1; j < w; j++) {
				for (int k = 1; k < h; k++) {
					valorCanal[(int) (pr.getColor(j, k).getBlue() * 255)]++;
				}

			}
		}
		return valorCanal;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void getGrafico(Image imagem, BarChart<String, Number> grafico) {

		/* int[] hist = histogramaUnico(imagem); */

		int[] histR = histograma(imagem, 1);
		int[] histG = histograma(imagem, 2);
		int[] histB = histograma(imagem, 3);

		/* XYChart.Series vlr = new XYChart.Series(); */

		XYChart.Series vlrR = new XYChart.Series();
		XYChart.Series vlrG = new XYChart.Series();
		XYChart.Series vlrB = new XYChart.Series();

		for (int i = 0; i < 256; i++) {
			/* vlr.getData().add(new XYChart.Data(i + "", hist[i])); */
			vlrR.getData().add(new XYChart.Data(i + "", histR[i]));
			vlrG.getData().add(new XYChart.Data(i + "", histG[i]));
			vlrB.getData().add(new XYChart.Data(i + "", histB[i]));

		}

		grafico.getData().addAll(vlrR, vlrG, vlrB);

		/* grafico.getData().addAll(vlr, vlrR,vlrG, vlrB); */

		for (Node n : grafico.lookupAll(".default-color0.chart-bar")) {
			n.setStyle("-fx-bar-fill:red;");
		}
		for (Node n : grafico.lookupAll(".default-color1.chart-bar")) {
			n.setStyle("-fx-bar-fill:green;");
		}

		for (Node n : grafico.lookupAll(".default-color2.chart-bar")) {
			n.setStyle("-fx-bar-fill:blue;");
		}

	}

	public static Image equalizacaoHistograma(Image imagem, boolean todos) {

		int w = (int) imagem.getWidth();
		int h = (int) imagem.getHeight();

		PixelReader pr = imagem.getPixelReader();
		WritableImage wi = new WritableImage(w, h);
		PixelWriter pw = wi.getPixelWriter();

		int[] hR = histograma(imagem, Constantes.RED);
		int[] hG = histograma(imagem, Constantes.GREEN);
		int[] hB = histograma(imagem, Constantes.BLUE);

		int[] histAcR = histogramaAc(hR);
		int[] histAcG = histogramaAc(hG);
		int[] histAcB = histogramaAc(hB);

		int qtTonsRed = qtTons(hR);
		int qtTonsGreen = qtTons(hG);
		int qtTonsBlue = qtTons(hB);

		double minR = pontoMin(hR);
		double minG = pontoMin(hG);
		double minB = pontoMin(hB);

		if (todos) {
			qtTonsRed = 255;
			qtTonsGreen = 255;
			qtTonsBlue = 255;

			minR = 0;
			minG = 0;
			minB = 0;
		}

		double n = w * h;

		for (int i = 1; i < w; i++) {
			for (int j = 1; j < h; j++) {
				Color oldCor = pr.getColor(i, j);

				double acR = histAcR[(int) (oldCor.getRed() * 255)];
				double acG = histAcG[(int) (oldCor.getGreen() * 255)];
				double acB = histAcB[(int) (oldCor.getBlue() * 255)];

				double pxR = ((qtTonsRed - 1) / n) * acR;
				double pxG = ((qtTonsGreen - 1) / n) * acG;
				double pxB = ((qtTonsBlue - 1) / n) * acB;

				double corR = (minR + pxR) / 255;
				double corG = (minG + pxG) / 255;
				double corB = (minB + pxB) / 255;

				Color newCor = new Color(corR, corG, corB, oldCor.getOpacity());
				pw.setColor(i, j, newCor);

			}
		}
		return wi;
	}

	private static int qtTons(int[] hist) {

		int qt = 0;
		for (int i = 0; i < 256; i++) {
			if (hist[i] > 0) {
				qt++;
			}
		}
		return qt;
	}

	private static int[] histogramaAc(int[] hist) {
		int[] histoAcumulado = new int[256];
		histoAcumulado[0] = hist[0];
		for (int i = 1; i < hist.length; i++) {

			histoAcumulado[i] = hist[i] + histoAcumulado[i - 1];
		}
		return histoAcumulado;
	}

	private static int pontoMin(int[] hist) {
		for (int i = 0; i < hist.length; i++) {
			if (hist[i] > 0)
				return i;
		}
		return 0;
	}
}