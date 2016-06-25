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
public class Kohonen2DAlgorithm {

    public static boolean WINNER_TAKES_ALL = false;

    public static double START_LEARNING_RATE = 0.5d;
    public static double MIN_LEARNING_RATE = 0.05d;
    public static double START_LAMBDA = 0.5d;
    public static double MIN_LAMBDA = 0.05d;

    public static boolean ENABLE_NEURON_POTENTIAL = false;
    public static double MIN_POTENTIAL = 0.75d;
    public static double POTENTIAL_INCRASE_RATE = 0.1d;
    public static double POTENTIAL_DECRASE_RATE = 0.5d;


    //private List<Neuron> neurons;
    private Neuron[][] neurons;
    private String outputFilePrefix = "_Kohonen2D";
    private double learning_rate_decrase_rate = 0d;
    private double lambda_decrase_rate = 0d;
    private double lambda;
    private double learningRate;


    public Kohonen2DAlgorithm() {
        this.neurons = null;
    }

    public Neuron[][] getNeurons() {
        return neurons;
    }

    public void setOutputFileName(String i) {
        this.outputFilePrefix = i;
    }

    private void initializeNeurons(int x, int y, int dimentions) {
        this.neurons = new Neuron[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                neurons[i][j] = new Neuron(dimentions);
            }
        }
    }

    private void changeLambdaRate(int iterations) {
        this.lambda_decrase_rate = (START_LAMBDA - MIN_LAMBDA) / (double) iterations;
    }

    private void changeLearningRate(int iterations) {
        this.learning_rate_decrase_rate = (START_LEARNING_RATE - MIN_LEARNING_RATE) / (double) iterations;
    }

    public void runAlgorithm(List<Dataset> inputs, int iterations, int x, int y) {

        START_LAMBDA = ((double) (x * y)) / 2d;

        this.initializeNeurons(x, y, inputs.get(0).size());
        this.changeLambdaRate(iterations);
        this.changeLearningRate(iterations);

        FileUtils.saveDatasetList(outputFilePrefix + "_inputs", inputs);
        FileUtils.saveNeuronsArray(outputFilePrefix + "_it" + 0, neurons);

        learningRate = START_LEARNING_RATE;
        lambda = START_LAMBDA;
        for (int i = 1; i <= iterations; i++) {
            for (Dataset p : inputs) {
                process(p);
            }
            learningRate = learningRate - learning_rate_decrase_rate;
            lambda = lambda - lambda_decrase_rate;
            FileUtils.saveNeuronsArray(outputFilePrefix + "_it" + i, neurons);
        }

        savePlotCommand(iterations, outputFilePrefix + "_plot", false);
        try {
            Utils.runGnuplotScript(outputFilePrefix + "_plot");
        } catch (IOException e) {
            e.printStackTrace();
        }

        deleteFiles(iterations);
    }

    public void runTwoPhaseAlgorithm(List<Dataset> inputs, int iterations, int x, int y) {

        START_LAMBDA = ((double) (x * y)) / 2d;

        this.initializeNeurons(x, y, inputs.get(0).size());

        String tmpFile = this.outputFilePrefix;
        this.outputFilePrefix = outputFilePrefix + "2F";
        int it1 = iterations + (iterations / 2);
        this.changeLearningRate(iterations);
        this.changeLambdaRate(iterations);


        //FAZA 1
        KohonenAlgorithm.WINNER_TAKES_ALL = false;
        FileUtils.saveDatasetList(outputFilePrefix + "_inputs", inputs);
        FileUtils.saveNeuronsArray(outputFilePrefix + "_it" + 0, neurons);

        learningRate = START_LEARNING_RATE;
        lambda = START_LAMBDA;
        for (int i = 1; i < iterations; i++) {
            for (Dataset p : inputs) {
                process(p);
            }
            learningRate = learningRate - learning_rate_decrase_rate;
            lambda = lambda - lambda_decrase_rate;
            FileUtils.saveNeuronsArray(outputFilePrefix + "_it" + i, neurons);
        }

        //FAZA 2
        KohonenAlgorithm.WINNER_TAKES_ALL = true;

        //learningRate = START_LEARNING_RATE;
        for (int i = iterations; i <= it1; i++) {
            for (Dataset p : inputs) {
                process(p);
            }
            //learningRate = learningRate - learning_rate_decrase_rate;
            //lambda = lambda - lambda_decrase_rate;
            FileUtils.saveNeuronsArray(outputFilePrefix + "_it" + i, neurons);
        }


        savePlotCommand(it1, outputFilePrefix + "_plot", true);
        try {
            Utils.runGnuplotScript(outputFilePrefix + "_plot");
        } catch (IOException e) {
            e.printStackTrace();
        }

        deleteFiles(it1);
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


    private Neuron findWinner(Dataset in) {
        List<Neuron> tmp2 = new ArrayList<>();
        //tmp2.addAll(neurons);
        for (int i = 0; i < neurons.length; i++) {
            for (int j = 0; j < neurons[0].length; j++) {
                tmp2.add(neurons[i][j]);// = new Neuron(dimentions);
            }
        }
        Collections.sort(tmp2, new DatasetDistanceComparator(in));
        return tmp2.get(0);
    }

    private void process(Dataset in) {
        Neuron winner = findWinner(in);
        int winnerX = 0;
        int winnerY = 0;
        for (int i = 0; i < neurons.length; i++) {
            for (int j = 0; j < neurons[0].length; j++) {
                if (winner == neurons[i][j]) {
                    winnerX = i;
                    winnerY = j;
                    break;
                }
            }
        }
        //int winnerIndex = neurons.indexOf(winner);
        neurons[winnerX][winnerY].moveForward(in, learningRate);
        if (ENABLE_NEURON_POTENTIAL) {
            winner.decreasePotential(POTENTIAL_DECRASE_RATE);
            for (int i = 0; i < neurons.length; i++) {
                for (int j = 0; j < neurons[0].length; j++) {
                    if (i != winnerX && j != winnerY) {
                        neurons[i][j].rest(POTENTIAL_INCRASE_RATE);
                    }
                }
            }
        }

        if (!WINNER_TAKES_ALL) {
            for (int i = 0; i < neurons.length; i++) {
                for (int j = 0; j < neurons[0].length; j++) {
                    if (i == winnerX && j == winnerY) {
                        continue;
                    }
                    if (ENABLE_NEURON_POTENTIAL) {
                        if (neurons[i][j].potential() >= MIN_POTENTIAL) {
                            double exp = Math.exp(-(Utils.power(distance(i, j, winnerX, winnerY))) / (2 * lambda * lambda));
                            neurons[i][j].moveForward(in, learningRate * exp);

                            neurons[i][j].decreasePotential(POTENTIAL_DECRASE_RATE);
                        } else {
                            neurons[i][j].rest(POTENTIAL_INCRASE_RATE);
                        }
                    } else {
                        double exp = Math.exp(-(Utils.power(distance(i, j, winnerX, winnerY))) / (2 * lambda * lambda));
                        //System.out.println(exp);
                        neurons[i][j].moveForward(in, learningRate * exp);
                    }

                }
            }
        }

    }

    private double distance(int x1, int y1, int x2, int y2) {
        //System.out.println("distance:" + Math.abs(Math.sqrt(Utils.power(x1 - x2) + Utils.power(y1 - y2))));

        return Math.abs(Math.sqrt(Utils.power(x1 - x2) + Utils.power(y1 - y2)));
    }

    private void savePlotCommand(int iterations, String plotFilePath, boolean twoPhase) {
        String header;
        if (twoPhase) {
            header = "Dwufazowy algorytm Kohonena 2D";
        } else {
            header = "Algorytm Kohonena 2D";
        }
        try (PrintStream out = new PrintStream(new FileOutputStream(plotFilePath))) {
            out.println("set terminal gif animate delay 10");
            out.println("set output '" + outputFilePrefix + "_animation.gif" + "'");
            out.println("set key outside");
            out.println("do for [i=0:" + iterations + "] {");
            out.println("set title \'" + header + ", iteracja \'.i");
            out.print("plot \'" + this.outputFilePrefix + "_inputs\' title \'Wzorzec\'");
            out.print(", \'" + this.outputFilePrefix + "_it\'.i title \'Neurony\'");
            out.println();
            out.println("}");
            out.println("do for [i=0:30] {");
            out.println("set title \'" + header + ", iteracja " + iterations + "\'");
            out.print("plot \'" + this.outputFilePrefix + "_inputs\' title \'Wzorzec\'");
            out.print(", \'" + this.outputFilePrefix + "_it" + iterations + "\' title \'Neurony\'");
            out.println();
            out.println("}");

            out.println();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
