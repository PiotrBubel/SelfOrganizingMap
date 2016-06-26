package maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dataset.Dataset;
import dataset.Neuron;
import dataset.comparators.DatasetDistanceComparator;
import utils.FileUtils;
import utils.ImageUtils;
import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class NeuralGasAlgorithm {

    public static double START_LEARNING_RATE = 0.5d;
    public static double MIN_LEARNING_RATE = 0.05d;
    public static double MIN_LAMBDA = 0.05d;
    public static boolean ENABLE_NEURON_POTENTIAL = false;
    public static double MIN_POTENTIAL = 0.75d;
    public static double POTENTIAL_INCRASE_RATE = 0.1d;
    public static double POTENTIAL_DECRASE_RATE = 0.5d;
    private static double START_LAMBDA = 0.5d;
    private List<Neuron> neurons;
    private String outputFilePrefix = "_NeuralGas";
    private double lambda;
    private double learningRate;


    public NeuralGasAlgorithm() {
        this.neurons = null;
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    private void initializeNeurons(int howMuch, int dimentions, List<Dataset> input) {
        this.neurons = new ArrayList<>();
        double[] mm = Utils.findMaxMin(input);
        double max = mm[0];
        double min = mm[1];
        double span = max - min;
        Dataset.MAX_FIRST_VAL = min + span * 0.25;
        Dataset.MIN_FIRST_VAL = min + span * 0.75;
        for (int i = 0; i < howMuch; i++) {
            this.neurons.add(new Neuron(dimentions));
        }
    }

    public void runAlgorithm(List<Dataset> inputs, int iterations, int howMuchNeurons) {

        START_LAMBDA = howMuchNeurons / 2d;

        this.initializeNeurons(howMuchNeurons, inputs.get(0).size(), inputs);
        File f = new File(outputFilePrefix + "_errors");
        f.delete();

        FileUtils.saveDatasetList(outputFilePrefix + "_inputs", inputs);
        FileUtils.saveNeuronsList(outputFilePrefix + "_it" + 0, neurons);

        learningRate = START_LEARNING_RATE;
        lambda = START_LAMBDA;
        double error = 0d;
        for (int i = 1; i <= iterations; i++) {
            for (Dataset p : inputs) {
                process(p);
            }
            changeParameters(i, iterations);
            error = countError(inputs);
            FileUtils.addDataset(outputFilePrefix + "_errors", new Dataset(new double[]{i, error}));
            FileUtils.saveNeuronsList(outputFilePrefix + "_it" + i, neurons);
        }


        savePlotCommand(iterations, outputFilePrefix + "_plot");
        try {
            Utils.runGnuplotScript(outputFilePrefix + "_plot");
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveErrorPlotCommand(outputFilePrefix + "_error_plot", outputFilePrefix + "_errors");
        try {
            Utils.runGnuplotScript(outputFilePrefix + "_error_plot");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ImageUtils.neuronsToWoronoiDiagram(neurons, 800, 800, outputFilePrefix + "_voronoi_diagram.png");

        deleteFiles(iterations);
    }

    private void changeParameters(int i, int iterations) {
        learningRate = START_LEARNING_RATE * Math.pow(MIN_LEARNING_RATE / START_LEARNING_RATE, (double) i / (double) iterations);//START_LEARNING_RATE * Math.exp(-0.1d * (double) i);//learningRate - learning_set_decrase_rate;
        lambda = START_LAMBDA * Math.pow(MIN_LAMBDA / START_LAMBDA, (double) i / (double) iterations);//START_LAMBDA * Math.exp(-0.1d * (double) i);//lambda - lambda_decrase_rate;
    }

    public void runAlgorithmWithoutGraph(List<Dataset> inputs, int iterations, int howMuchNeurons) {

        START_LAMBDA = howMuchNeurons / 2d;

        this.initializeNeurons(howMuchNeurons, inputs.get(0).size(), inputs);

        learningRate = START_LEARNING_RATE;
        lambda = START_LAMBDA;
        for (int i = 1; i <= iterations; i++) {
            for (Dataset p : inputs) {
                process(p);
            }
            changeParameters(i, iterations);
        }
    }

    public void runAlgorithmOnImage(String inImage, String outImage, int iterations, int rows, int columns) {
        List<Dataset> d = ImageUtils.datasetsFromImage(inImage, rows, columns);

        this.runAlgorithmWithoutGraph(d, iterations, rows * columns);
        ImageUtils.neuronsToImage(this.neurons, d, outImage);
    }

    public void deleteFiles(int iterations) {
        File f = new File(outputFilePrefix + "_inputs");
        f.delete();
        f = new File(outputFilePrefix + "_it" + 0);
        f.delete();
        for (int i = 1; i <= iterations; i++) {
            f = new File(outputFilePrefix + "_it" + i);
            f.delete();
        }
        f = new File(outputFilePrefix + "_plot");
        f.delete();
        f = new File(outputFilePrefix + "_error_plot");
        f.delete();
        f = new File(outputFilePrefix + "_errors");
        f.delete();
    }

    public double countError(List<Dataset> in) {
        double sum = 0d;
        for (Dataset d : in) {
            Neuron winner = findWinner(d);
            sum = sum + winner.distanceTo(d);
        }
        return sum / in.size();
    }

    private Neuron findWinner(Dataset in) {
        List<Neuron> tmp2 = new ArrayList<>();
        tmp2.addAll(neurons);
        Collections.sort(tmp2, new DatasetDistanceComparator(in));
        return tmp2.get(0);
    }

    private void process(Dataset in) {
        Collections.sort(neurons, new DatasetDistanceComparator(in));

        for (int i = 0; i < neurons.size(); i++) {
            if (ENABLE_NEURON_POTENTIAL) {
                if (neurons.get(i).potential() > MIN_POTENTIAL) {
                    neurons.get(i).moveForward(in, learningRate * Math.exp((double) -i / lambda));
                    neurons.get(i).decreasePotential(POTENTIAL_DECRASE_RATE);
                } else {
                    neurons.get(i).rest(POTENTIAL_INCRASE_RATE);
                }
            } else {
                neurons.get(i).moveForward(in, learningRate * Math.exp((double) -i / lambda));
            }
        }
    }

    private void savePlotCommand(int iterations, String plotFilePath) {
        try (PrintStream out = new PrintStream(new FileOutputStream(plotFilePath))) {
            out.println("set terminal gif animate delay 10");
            out.println("set output '" + outputFilePrefix + "_animation.gif" + "'");
            out.println("set key outside");
            out.println("do for [i=0:" + iterations + "] {");
            out.println("set title \'Algorytm gazu neuronowego, iteracja \'.i");
            out.print("plot \'" + this.outputFilePrefix + "_inputs\' title \'Wzorzec\'");
            out.print(", \'" + this.outputFilePrefix + "_it\'.i title \'Neurony\'");
            out.println();
            out.println("}");

            out.println("do for [i=0:30] {");
            out.println("set title \'Algorytm gazu neuronowego, iteracja " + iterations + "\'");
            out.print("plot \'" + this.outputFilePrefix + "_inputs\' title \'Wzorzec\'");
            out.print(",'" + this.outputFilePrefix + "_it" + iterations + "\' title \'Neurony\'");
            out.println();
            out.println("}");

            out.println();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void saveErrorPlotCommand(String plotFilePath, String pointsPathT) {
        try (PrintStream out = new PrintStream(new FileOutputStream(plotFilePath))) {
            out.println("set terminal png size 640,480");
            out.println("set output '" + outputFilePrefix + "_error.png'");
            out.println("set yrange [0:15]");
            out.println("set ylabel \'Wartosc bledu\'");
            out.println("set xlabel \'Epoki\'");

            out.println("set title \"Algorytm gazu neuronowego\"");
            //out.println("set key outside");
            out.println("set style data lines");
            out.println("plot \"" + pointsPathT + "\" title \"Sredni blad kwantyzacji\", \\");
            out.println();
        } catch (FileNotFoundException ex) {
        }
    }
}
