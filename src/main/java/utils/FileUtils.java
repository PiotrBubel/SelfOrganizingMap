package utils;

/**
 * Created by Piotrek on 17.06.2016.
 */

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import dataset.Dataset;
import dataset.Neuron;

public class FileUtils {

    public static void saveDatasetList(String filePath, List<Dataset> data) {
        try (PrintStream out = new PrintStream(new FileOutputStream(filePath))) {
            DecimalFormat df = new DecimalFormat("0.00000000");
            for (Dataset dataset : data) {
                String toPrint = "";
                boolean flag = true;
                for (int i = 0; i < dataset.size(); i++) {
                    if (!Double.isNaN(dataset.getValue(i)) && flag) {
                        toPrint = toPrint + String.valueOf(dataset.getValue(i)).replaceAll(",", ".") + " ";
                    } else {
                        flag = false;
                    }
                }
                if (flag) {
                    out.print(toPrint);
                    out.println();
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveNeuronsList(String filePath, List<Neuron> data) {
        try (PrintStream out = new PrintStream(new FileOutputStream(filePath))) {
            DecimalFormat df = new DecimalFormat("0.00000000");
            for (Neuron dataset : data) {
                String toPrint = "";
                boolean flag = true;
                for (int i = 0; i < dataset.size(); i++) {
                    if (!Double.isNaN(dataset.getValue(i)) && flag) {
                        toPrint = toPrint + String.valueOf(dataset.getValue(i)).replaceAll(",", ".") + " ";
                    } else {
                        flag = false;
                    }
                }
                if (flag) {
                    out.print(toPrint);
                    out.println();
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<Dataset> loadPointsList(String filePath) {
        List<Dataset> listOfLists = new ArrayList<>();
        try (Scanner sc = new Scanner(new FileReader(filePath))) {
            while (sc.hasNext()) {
                String line = sc.nextLine();
                String[] values = line.split(" ");
                double[] doubles = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    doubles[i] = Double.parseDouble(values[i].replaceAll(",", "."));
                }
                listOfLists.add(new Dataset(doubles));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listOfLists;
    }

    public static void addDataset(String filePath, Dataset p) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filePath, true))) {
            for (int i = 0; i < p.size(); i++) {
                out.append(String.valueOf(p.getValue(i)));
                out.append(" ");
            }
            out.newLine();

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


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

}
