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

    public static void saveNeuronsArray(String filePath, Neuron[][] neurons){
        List<Neuron> tmp2 = new ArrayList<>();
        for (int i = 0; i < neurons.length; i++) {
            for (int j = 0; j < neurons[0].length; j++) {
                tmp2.add(neurons[i][j]);// = new Neuron(dimentions);
            }
        }
        FileUtils.saveNeuronsList(filePath, tmp2);
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


}
