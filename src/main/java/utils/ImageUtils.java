package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import dataset.Dataset;
import dataset.DatasetDistanceComparator;
import dataset.DatasetIndexComparator;
import dataset.Neuron;

/**
 * Created by Piotrek on 25.06.2016.
 */
public class ImageUtils {

    public static int chunkWidth = -1;
    public static int chunkHeight = -1;
    public static int rows = -1;
    public static int cols = -1;
    //public static int imageWidth = -1;
    //public static int imageHeight = -1;

    private static BufferedImage[] splitImage(BufferedImage image, int rows, int cols) {
        int chunks = rows * cols;
        ImageUtils.rows = rows;
        ImageUtils.cols = cols;
        ImageUtils.chunkWidth = image.getWidth() / cols;
        ImageUtils.chunkHeight = image.getHeight() / rows;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        System.out.println("Splitting done");

        return imgs;
    }

    private static BufferedImage mergeImages(BufferedImage[] buffImages) {
        //BufferedImage[] buffImages = new BufferedImage[chunks];
        int chunks = buffImages.length;

        //chunkWidth = buffImages[0].getWidth();
        //chunkHeight = buffImages[0].getHeight();

        //Initializing the final image
        System.out.println(chunkWidth * cols);
        System.out.println(chunkHeight * rows);

        BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, BufferedImage.TYPE_BYTE_GRAY);

        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
                num++;
            }
        }
        System.out.println("Image concatenated.....");
        return finalImg;
    }

    private static Dataset convertToDataset(BufferedImage image, int index) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width != chunkWidth || height != chunkHeight) {
            System.out.println("Wrong chunks dimensions");
            return null;
        }
//FIXME
        double[] result = new double[width * height];
        int n = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[n] = image.getRGB(col, row);
                n++;
            }
        }

        return new Dataset(result, index);
    }

    public static List<Dataset> datasetsFromImage(String image, int rows, int cols) {
        BufferedImage i = ImageUtils.loadImage(image);
        BufferedImage[] i2 = ImageUtils.splitImage(i, rows, cols);
        List<Dataset> list = new ArrayList<>();
        for (int j = 0; j < i2.length; j++) {
            list.add(ImageUtils.convertToDataset(i2[j], j));
        }
        return list;
    }

    public static void neuronsToImage(List<Neuron> neurons, List<Dataset> input, String outImage) {
        BufferedImage[] splitted = new BufferedImage[rows * cols];

        input.sort(new DatasetIndexComparator());
        for (int i = 0; i < input.size(); i++) {
            Neuron winner = ImageUtils.findWinner(input.get(i), neurons);
            splitted[i] = ImageUtils.neuronToImage(winner);
        }

        ImageUtils.saveImage(outImage, ImageUtils.mergeImages(splitted));
    }

    private static BufferedImage neuronToImage(Neuron neuron) {
        double[] weights = neuron.getWeights();
        //int[][] imageArray = new int[chunkHeight][chunkWidth];
        int[][] imageArray = new int[chunkHeight][chunkWidth];

        for (int x = 0; x < chunkHeight; x++) {
            for (int k = 0; k < chunkWidth; k++) {
                imageArray[x][k] = (int) weights[chunkHeight * x + k];
            }
        }
        BufferedImage bufferedImage = new BufferedImage(chunkWidth * cols, chunkHeight * rows, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                int pixel = imageArray[i][j];
                System.out.println("The pixel in Matrix: " + pixel);
                bufferedImage.setRGB(i, j, pixel);
                System.out.println("The pixel in BufferedImage: " + bufferedImage.getRGB(i, j));
            }
        }

        return bufferedImage;
    }


    public static BufferedImage neuronsToImageWoronoi(List<Neuron> neurons, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double minDistance = Double.MAX_VALUE;
                double winnerRGB = 0;
                for (Neuron n : neurons) {
                    double[] weights = n.getWeights();
                    double distance = distance((int) weights[0], (int) weights[1], i, j);
                    if (distance < minDistance) {
                        winnerRGB = weights[2];
                        minDistance = distance;
                    }
                }
                image.setRGB(i, j, (int) winnerRGB);
            }
        }
        return image;
    }

    private static double distance(int x1, int y1, int x2, int y2) {
        return Math.abs(Math.sqrt(Utils.power(x1 - x2) + Utils.power(y1 - y2)));
    }

    public static void saveImage(String fileName, BufferedImage image) {
        try {
            ImageIO.write(image, "png", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage loadImage(String fileName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private static Neuron findWinner(Dataset in, List<Neuron> neurons) {
        List<Neuron> tmp2 = new ArrayList<>();
        tmp2.addAll(neurons);
        Collections.sort(tmp2, new DatasetDistanceComparator(in));
        return tmp2.get(0);
    }
}
