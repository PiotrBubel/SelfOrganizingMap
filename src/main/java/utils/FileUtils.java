package utils;

/**
 * Created by Piotrek on 17.06.2016.
 */

import java.io.BufferedWriter;
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

    /*
        public static List<List<Double>> loadDataLists(String filePath) {
            List<List<Double>> listOfLists = new ArrayList<>();
            try (Scanner sc = new Scanner(new FileReader(filePath))) {
                int l = 0;
                while (sc.hasNext()) {
                    String line = sc.nextLine();
                    String[] values = line.split(" ");
                    listOfLists.add(new ArrayList<Double>());
                    for (String v : values) {
                        listOfLists.get(l).add(Double.parseDouble(v.replaceAll(",", ".")));
                    }
                    l++;
                }
            } catch (FileNotFoundException ex) {

            }
            return listOfLists;
        }
    */
    /*
        public static double[][] loadDataArrays(String filePath) {
            List<List<Double>> listOfLists = loadDataLists(filePath);
            int x = listOfLists.size();
            int y = listOfLists.get(0).size();
            double[][] arrayOfArrays = new double[x][y];
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    arrayOfArrays[i][j] = listOfLists.get(i).get(j);
                }
            }
            return arrayOfArrays;
        }


    public static void saveArray(String filePath, double[][] data) {
        try (PrintStream out = new PrintStream(new FileOutputStream(filePath))) {
            //DecimalFormat df = new DecimalFormat("0.000");
            for (double[] line : data) {
                for (double value : line) {
                    //out.print(df.format(value));
                    out.print(value);
                    out.print(" ");
                }
                out.println();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public static void saveList(String filePath, List<List<Double>> data) {
        try (PrintStream out = new PrintStream(new FileOutputStream(filePath))) {
            DecimalFormat df = new DecimalFormat("0.000");
            for (List<Double> line : data) {
                for (double value : line) {
                    out.print(df.format(value));
                    out.print(" ");
                }
                out.println();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    */

}
