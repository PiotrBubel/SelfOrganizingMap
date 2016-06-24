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
import dataset.DatasetDistanceComparator;
import dataset.Neuron;
import utils.FileUtils;
import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class NeuralGasAlgorithm {

    public static double START_LEARNING_RATE = 0.5d;
    public static double MIN_LEARNING_RATE = 0.05d;
    public static double START_LAMBDA = 0.5d;
    public static double MIN_LAMBDA = 0.05d;

    public static boolean ENABLE_NEURON_POTENTIAL = false;
    public static double MIN_POTENTIAL = 0.75d;
    public static double POTENTIAL_INCRASE_RATE = 0.1d;
    public static double POTENTIAL_DECRASE_RATE = 0.5d;


    private List<Neuron> neurons;
    private String outputFilePrefix = "_NeuralGas";
    private double learning_set_decrase_rate = 0d;
    private double lambda_decrase_rate = 0d;


    public NeuralGasAlgorithm() {
        this.neurons = null;
    }

    private void initializeNeurons(int howMuch, int dimentions) {
        this.neurons = new ArrayList<>();
        for (int i = 0; i < howMuch; i++) {
            this.neurons.add(new Neuron(dimentions));
        }
    }

    public void runAlgorithm(List<Dataset> inputs, int iterations, int howMuchNeurons) {

        START_LAMBDA = howMuchNeurons / 2d;

        this.initializeNeurons(howMuchNeurons, inputs.get(0).size());
        this.changeLearningSetRate(iterations);
        this.changeLambdaRate(iterations);

        FileUtils.saveDatasetList(outputFilePrefix + "_inputs", inputs);
        FileUtils.saveNeuronsList(outputFilePrefix + "_it" + 0, neurons);

        double learningRate = START_LEARNING_RATE;
        double lambda = START_LAMBDA;
        for (int i = 1; i <= iterations; i++) {
            for (Dataset p : inputs) {
                process(p, learningRate, lambda);
            }
            learningRate = learningRate - learning_set_decrase_rate;
            lambda = lambda - lambda_decrase_rate;

            FileUtils.saveNeuronsList(outputFilePrefix + "_it" + i, neurons);
        }

        savePlotCommand(iterations, outputFilePrefix + "_plot");
        try {
            Utils.runGnuplotScript(outputFilePrefix + "_plot");
        } catch (IOException e) {
            e.printStackTrace();
        }

        deleteFiles(iterations);
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
    }


    private void process(Dataset in, double learningRate, double lambda) {
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

    private void changeLearningSetRate(int iterations) {
        this.learning_set_decrase_rate = (START_LEARNING_RATE - MIN_LEARNING_RATE) / (double) iterations;
    }

    private void changeLambdaRate(int iterations) {
        this.lambda_decrase_rate = (START_LAMBDA - MIN_LAMBDA) / (double) iterations;
    }

    private void savePlotCommand(int iterations, String plotFilePath) {
        try (PrintStream out = new PrintStream(new FileOutputStream(plotFilePath))) {
            out.println("set terminal gif animate delay 10");
            out.println("set output '" + outputFilePrefix + "_animation.gif" + "'");
            out.println("set key outside");
            iterations = iterations - 1;
            out.println("do for [i=0:" + iterations + "] {");
            out.println("set title \'Algorytm gazu neuronowego, iteracja \'.i");
            out.print("plot \'" + this.outputFilePrefix + "_it\'.i title \'Neurony\'");
            out.print(", \'" + this.outputFilePrefix + "_inputs\' title \'Wzorzec\'");
            out.println();
            out.println("}");
            out.println();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
