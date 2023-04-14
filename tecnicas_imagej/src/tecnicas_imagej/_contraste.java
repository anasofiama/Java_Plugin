package tecnicas_imagej;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import java.util.IntSummaryStatistics;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.lang.Math;

public class _contraste implements PlugInFilter {
	
	public int width;
	public int height;
	
	public int setup(String arg, ImagePlus imp) {
		this.width = imp.getWidth();
		this.height = imp.getHeight();
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		byte[] pixels = (byte[]) ip.getPixels();
		int[] pixels_to_int = new int[pixels.length];
		
		for (int i=0;i<pixels_to_int.length;i++) {
			pixels_to_int[i] = (int)(0xff & pixels[i]);
			if (pixels_to_int[i] < 0 || pixels_to_int[i] > 255) {
				System.out.println("position: "+i+"pixel info: "+pixels_to_int[i]);
			}
		}
		//System.out.println("pixels_to_int1:"+pixels_to_int);

		IntSummaryStatistics stats = Arrays.stream(pixels_to_int).summaryStatistics();

		double max = stats.getMax();		
		double min = stats.getMin();	
		
		System.out.println("Max = "+max+" : Min = "+min);
		
		final double maxAjuste = 255;
		final double minAjuste = 0;
		double deltay = maxAjuste - minAjuste;
		double deltax = max - min;
		double a = deltay/deltax;
		double b = (-1)*a*min;
		ajuste(a,b,pixels_to_int);
		//System.out.println("pixels_to_int2:"+pixels_to_int);
		getImageFromArray(pixels_to_int,width,height);
		//System.out.println("Fim!!!");
		// Varrer a imagem para reprocessar as intensidades
		
			// new_value = Funcao linear --> como montar essa equacao?
		
		// Criar uma nova imagem associando para cada (x,y) a nova intensidade
		
		
	}
	
	private void ajuste(double a, double b, int [] entrada) {
		for (int i = 0; i<entrada.length; i++) {
			double aux = entrada[i]*a + b;
			//System.out.println("aux:"+aux);
			entrada[i] = (int) Math.round(aux);
			//if (entrada[i] >= 128) {
				//entrada[i] = 128;
			//}
			if (entrada[i] < 0 || entrada[i] > 255) {
				System.out.println("position: "+i+"pixel info: "+entrada[i]);
			}
		}
	}
	

    public static void getImageFromArray(int[] pixels, int width, int height) {

        // create a BufferedImage object with the specified width, height, and type
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // get the raster data of the image
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        // copy the pixel data to the raster data buffer
        for (int i = 0; i < pixels.length; i++) {
            data[i] = (byte) pixels[i];
        }

        // save the grayscale image to a file
        try {
            ImageIO.write(image, "png", new File("output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}