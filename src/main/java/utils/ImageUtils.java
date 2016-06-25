package utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import dataset.Dataset;
import dataset.Neuron;

/**
 * Created by Piotrek on 25.06.2016.
 */
public class ImageUtils {

    public static int[][] loadImage(String fileName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertTo2DUsingGetRGB(image);
    }

    public static void saveImage(String fileName, BufferedImage image) {

        try {
            ImageIO.write(image, "png", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0, 0, width, height, pixels);
        return image;
    }

    public static void saveImage(byte[] bytearray, String fileName) {
        //String dirName = "C:\\";
        try {
            BufferedImage imag = ImageIO.read(new ByteArrayInputStream(bytearray));
            ImageIO.write(imag, "jpg", new File(fileName + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] loadImageByte(String fileName) {
        byte[] bytearray = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
            BufferedImage img = ImageIO.read(new File(fileName));
            ImageIO.write(img, fileName.substring(fileName.length() - 3), baos);
            baos.flush();

            String base64String = Base64.encode(baos.toByteArray());
            baos.close();

            bytearray = Base64.decode(base64String);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytearray;
    }

    public static int[][] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
            }
        }

        return result;
    }


    public static double[] byteArrayToDouble(byte[] bytes) {
        double[] d = new double[bytes.length / 8];
        for (int i = 0; i < d.length; i++) {
            byte[] b = new byte[8];
            for (int j = 0; j < 8; j++) {
                b[j] = bytes[(i * 8) + j];
            }

            d[i] = ByteBuffer.wrap(b).getDouble();
        }
        return d;
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static byte[] doubleArrayToBytes(double[] doubles) {
        byte[] d = new byte[doubles.length * 8];
        for (int i = 0; i < doubles.length; i++) {
            byte[] bytes = new byte[8];
            ByteBuffer.wrap(bytes).putDouble(doubles[i]);
            int x = 0;
            for (int j = i * 8; j < (i * 8) + 8; j++) {
                d[j] = bytes[x];
                x++;
            }
        }
        return d;
    }

    public static java.util.List<Dataset> doubleArrayToDatasets(double[] doubles) {
        java.util.List<Dataset> list = new ArrayList<>();
        for (int i = 0; i < doubles.length; i++) {
            list.add(new Dataset(new double[]{doubles[i]}));
        }
        return list;
    }

    public static double[] neuronsToDoubleArray(java.util.List<Neuron> list) {
        double[] d = new double[list.size()];
        for (int i = 0; i < d.length; i++) {
            d[i] = list.get(i).getValue(0);
        }
        return d;
    }

}
