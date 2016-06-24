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
public class KohonenAlgorithm {

    public static boolean WINNER_TAKES_ALL = false;

    public static double START_LEARNING_RATE = 0.5d;
    public static double MIN_LEARNING_RATE = 0.05d;
    public static double START_LAMBDA = 0.5d;
    public static double MIN_LAMBDA = 0.05d;

    public static boolean ENABLE_NEURON_POTENTIAL = false;
    public static double MIN_POTENTIAL = 0.75d;
    public static double POTENTIAL_INCRASE_RATE = 0.1d;
    public static double POTENTIAL_DECRASE_RATE = 0.5d;

    private List<Neuron> neurons;
    private String outputFilePrefix = "_Kohonen";
    private double learning_rate_decrase_rate = 0d;
    private double lambda_decrase_rate = 0d;


    public KohonenAlgorithm() {
        this.neurons = null;
    }

    public void setOutputFileName(String i) {
        this.outputFilePrefix = i;
    }

    private void initializeNeurons(int howMuch, int dimentions) {
        this.neurons = new ArrayList<>();
        for (int i = 0; i < howMuch; i++) {
            this.neurons.add(new Neuron(dimentions));
        }
    }

    private void changeLambdaRate(int iterations) {
        this.lambda_decrase_rate = (START_LAMBDA - MIN_LAMBDA) / (double) iterations;
    }

    private void changeLearningRate(int iterations) {
        this.learning_rate_decrase_rate = (START_LEARNING_RATE - MIN_LEARNING_RATE) / (double) iterations;
    }

    public void runAlgorithm(List<Dataset> inputs, int iterations, int howMuchNeurons) {

        START_LAMBDA = howMuchNeurons / 2d;

        this.initializeNeurons(howMuchNeurons, inputs.get(0).size());
        this.changeLambdaRate(iterations);
        this.changeLearningRate(iterations);

        FileUtils.saveDatasetList(outputFilePrefix + "_inputs", inputs);
        FileUtils.saveNeuronsList(outputFilePrefix + "_it" + 0, neurons);

        double learningRate = START_LEARNING_RATE;
        double lambda = START_LAMBDA;
        for (int i = 1; i <= iterations; i++) {
            for (Dataset p : inputs) {
                process(p, learningRate, lambda);
            }
            learningRate = learningRate - learning_rate_decrase_rate;
            lambda = lambda - lambda_decrase_rate;
            FileUtils.saveNeuronsList(outputFilePrefix + "_it" + i, neurons);
        }

        savePlotCommand(iterations, outputFilePrefix + "_plot", false);
        try {
            Utils.runGnuplotScript(outputFilePrefix + "_plot");
        } catch (IOException e) {
            e.printStackTrace();
        }

        deleteFiles(iterations);
    }

    public void runTwoPhaseAlgorithm(List<Dataset> inputs, int iterations, int howMuchNeurons) {

        String tmpFile = this.outputFilePrefix;
        this.outputFilePrefix = outputFilePrefix + "2F";
        int it1 = iterations / 2;
        this.initializeNeurons(howMuchNeurons, inputs.get(0).size());
        this.changeLearningRate(iterations);
        this.changeLambdaRate(iterations);


        //FAZA 1
        KohonenAlgorithm.WINNER_TAKES_ALL = false;
        FileUtils.saveDatasetList(outputFilePrefix + "_inputs", inputs);
        FileUtils.saveNeuronsList(outputFilePrefix + "_it" + 0, neurons);

        double learningRate = START_LEARNING_RATE;
        double lambda = START_LAMBDA;
        for (int i = 1; i < it1; i++) {
            for (Dataset p : inputs) {
                process(p, learningRate, lambda);
            }
            learningRate = learningRate - learning_rate_decrase_rate;
            lambda = lambda - lambda_decrase_rate;
            FileUtils.saveNeuronsList(outputFilePrefix + "_it" + i, neurons);
        }

        //FAZA 2
        KohonenAlgorithm.WINNER_TAKES_ALL = true;

        //learningRate = START_LEARNING_RATE;
        for (int i = it1; i <= iterations; i++) {
            for (Dataset p : inputs) {
                process(p, learningRate, lambda);
            }
            learningRate = learningRate - learning_rate_decrase_rate;
            lambda = lambda - lambda_decrase_rate;
            FileUtils.saveNeuronsList(outputFilePrefix + "_it" + i, neurons);
        }


        savePlotCommand(iterations, outputFilePrefix + "_plot", true);
        try {
            Utils.runGnuplotScript(outputFilePrefix + "_plot");
        } catch (IOException e) {
            e.printStackTrace();
        }

        deleteFiles(iterations);
        this.outputFilePrefix = tmpFile;
    }

    private void deleteFiles(int iterations) {
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


    private Neuron findWinner(List<Neuron> neurons, Dataset in) {
        List<Neuron> tmp2 = new ArrayList<>();
        tmp2.addAll(neurons);
        Collections.sort(tmp2, new DatasetDistanceComparator(in));
        return tmp2.get(0);
    }

    private void process(Dataset in, double learningRate, double lambda) {
        Neuron winner = findWinner(neurons, in);
        winner.moveForward(in, learningRate);
        if (ENABLE_NEURON_POTENTIAL) {
            winner.decreasePotential(POTENTIAL_DECRASE_RATE);
            for (Neuron n : neurons) {
                if (n != winner) {
                    n.rest(POTENTIAL_INCRASE_RATE);
                }
            }
        }

        if (!WINNER_TAKES_ALL) {
            int winnerIndex = neurons.indexOf(winner);

            for (int i = 0; i < neurons.size(); i++) {
                if (i != winnerIndex) {
                    if (ENABLE_NEURON_POTENTIAL) {
                        if (neurons.get(i).potential() > MIN_POTENTIAL) {
                            neurons.get(i).moveForward(in, learningRate *
                                    Math.exp(-(Utils.power(Math.abs(winnerIndex - i)))
                                            / 2d * Utils.power(lambda)));

                            neurons.get(i).decreasePotential(POTENTIAL_DECRASE_RATE);
                        } else {
                            neurons.get(i).rest(POTENTIAL_INCRASE_RATE);
                        }
                    } else {
                        neurons.get(i).moveForward(in, learningRate *
                                Math.exp(-(Utils.power(Math.abs(winnerIndex - i)))
                                        / 2d * Utils.power(lambda)));
                    }
                }
            }
        }
    }

    private void savePlotCommand(int iterations, String plotFilePath, boolean twoPhase) {
        String header;
        if (twoPhase) {
            header = "Dwufazowy algorytm Kohonena";
        } else {
            header = "Algorytm Kohonena";
        }
        try (PrintStream out = new PrintStream(new FileOutputStream(plotFilePath))) {
            out.println("set terminal gif animate delay 10");
            out.println("set output '" + outputFilePrefix + "_animation.gif" + "'");
            out.println("set key outside");
            iterations = iterations - 1;
            out.println("do for [i=0:" + iterations + "] {");
            out.println("set title \'" + header + ", iteracja \'.i");
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
