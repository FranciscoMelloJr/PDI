package utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

public final class Utils {

	public static Image matImage(Mat frame) {
		try {
			return SwingFXUtils.toFXImage(matBufferedImage(frame), null);
		} catch (Exception e) {
			System.err.println("Não foi possível converter o objeto: " + e);
			return null;
		}
	}

	public static Mat imageMat(Image image) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		byte[] buffer = new byte[width * height * 4];

		PixelReader reader = image.getPixelReader();
		WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
		reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);
		Mat mat = new Mat(height, width, CvType.CV_8UC4);
		mat.put(0, 0, buffer);
		return mat;
	}
	
	// ImagemView e Image para dar update na thread
	public static <T> void onFXThread(final ObjectProperty<T> property, final T t) {
		Platform.runLater(() -> {
			property.set(t);
		});
	}

	// Auxilio na conversão matImage GRAY RGB
	private static BufferedImage matBufferedImage(Mat original) {

		BufferedImage image = null;
		int width = original.width() , height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);

		if (original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}
}
