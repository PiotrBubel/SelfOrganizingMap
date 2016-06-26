package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import dataset.Dataset;
import dataset.Neuron;
import dataset.comparators.DatasetDistanceComparator;
import dataset.comparators.DatasetIndexComparator;

/**
 * Created by Piotrek on 25.06.2016.
 */
public class ImageUtils {

    public static int chunkWidth = -1;
    public static int chunkHeight = -1;
    public static int rows = -1;
    public static int cols = -1;
    public static int imageType = BufferedImage.TYPE_BYTE_GRAY;


    public static void test() {
        BufferedImage image;

        List<Dataset> datasets = datasetsFromImage("image.png", 5, 5);
        BufferedImage[] tab = new BufferedImage[datasets.size()];
        for (int i = 0; i < datasets.size(); i++) {
            tab[i] = ImageUtils.neuronToImage(datasets.get(i).toNeuron());
        }
        image = mergeImages(tab);
        saveImage("image.png", image);
    }

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

        return imgs;
    }

    private static BufferedImage mergeImages(BufferedImage[] buffImages) {
        BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, imageType);

        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
                num++;
            }
        }
        return finalImg;
    }

    private static Dataset convertToDataset(BufferedImage image, int index) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width != chunkWidth || height != chunkHeight) {
            System.out.println("Wrong chunks dimensions");
            return null;
        }

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
        int[][] imageArray = new int[chunkHeight][chunkWidth];

        //System.out.println(chunkHeight);
        //System.out.println(chunkWidth);
        //System.out.println((chunkHeight - 1) * (chunkWidth - 1) + (chunkWidth - 1));
        //System.out.println(chunkHeight * chunkWidth);
        //System.out.println(weights.length);
        //System.out.println("-------------------");
        int neu = 0;
        for (int x = 0; x < chunkHeight; x++) {
            for (int k = 0; k < chunkWidth; k++) {
                imageArray[x][k] = (int) weights[neu];//[chunkHeight * x + k];
                neu++;
            }
        }
        BufferedImage bufferedImage = new BufferedImage(chunkWidth * cols, chunkHeight * rows, imageType);
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                int pixel = imageArray[i][j];
                bufferedImage.setRGB(j, i, pixel);
                //if (pixel != bufferedImage.getRGB(i, j)) {
                //    System.out.println("nie dziala, pixel: " + pixel + " image: " + bufferedImage.getRGB(i, j));
                //}
            }
        }

        return bufferedImage;
    }


    public static void neuronsToWoronoiDiagram(List<Neuron> neurons, int width, int height, String imageName) {
        BufferedImage image = new BufferedImage(width, height, imageType);
        int[] colors = ImageUtils.randomizeTab(neurons.size());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double minDistance = Double.MAX_VALUE;
                int winnerRGB = 0;
                for (int n = 0; n < neurons.size(); n++) {
                    double[] weights = neurons.get(n).getWeights();
                    double distance = distance((int) weights[0] * 100, (int) weights[1] * 100, i, j);
                    if (distance < minDistance) {
                        winnerRGB = colors[n];
                        minDistance = distance;
                    }
                }
                image.setRGB(i, j, winnerRGB);
            }
        }
        ImageUtils.saveImage(imageName, image);
    }

    private static int[] randomizeTab(int hm) {
        int[] tab = new int[hm];
        Random rndm = new Random();
        for (int i = 0; i < hm; i++) {
            tab[i] = rndm.nextInt();
        }
        return tab;
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
            imageType = image.getType();
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
