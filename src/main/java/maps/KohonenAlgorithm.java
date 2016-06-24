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
    public static double START_LEARNING_RATE = 0.5d;
    public static double LEARNING_RATE_DECREASE_STEP = 0.01;
    public static boolean ENABLE_NEURON_POTENTIAL = true;
    public static boolean WINNER_TAKES_ALL = false;
    public static double LAMBDA = 0.5d;
    public static double MIN_POTENTIAL = 0.7d;
    public static double POTENTIAL_INCRASE_RATE = 0.1d;
    public static double POTENTIAL_DECRASE_RATE = 0.1d;


    protected List<Neuron> neurons;
    protected String outputFilePrefix = "_Kohonen";

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

    public void runAlgorithm(List<Dataset> inputs, int iterations, int howMuchNeurons) {

        this.initializeNeurons(howMuchNeurons, inputs.get(0).size());


        FileUtils.saveDatasetList(outputFilePrefix + "_inputs", inputs);
        FileUtils.saveNeuronsList(outputFilePrefix + "_it" + 0, neurons);

        double learningRate = START_LEARNING_RATE;
        for (int i = 1; i <= iterations; i++) {
            for (Dataset p : inputs) {
                process(p, learningRate);
            }
            if (!(learningRate <= LEARNING_RATE_DECREASE_STEP)) {
                learningRate = learningRate - LEARNING_RATE_DECREASE_STEP;
            }
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


        //FAZA 1
        KohonenAlgorithm.WINNER_TAKES_ALL = false;
        FileUtils.saveDatasetList(outputFilePrefix + "_inputs", inputs);
        FileUtils.saveNeuronsList(outputFilePrefix + "_it" + 0, neurons);

        double learningRate = START_LEARNING_RATE;
        for (int i = 1; i < it1; i++) {
            for (Dataset p : inputs) {
                process(p, learningRate);
            }
            if (!(learningRate <= LEARNING_RATE_DECREASE_STEP)) {
                learningRate = learningRate - LEARNING_RATE_DECREASE_STEP;
            }
            FileUtils.saveNeuronsList(outputFilePrefix + "_it" + i, neurons);
        }

        //FAZA 2
        KohonenAlgorithm.WINNER_TAKES_ALL = true;

        //learningRate = START_LEARNING_RATE;
        for (int i = it1; i <= iterations; i++) {
            for (Dataset p : inputs) {
                process(p, learningRate);
            }
            if (!(learningRate <= LEARNING_RATE_DECREASE_STEP)) {
                learningRate = learningRate - LEARNING_RATE_DECREASE_STEP;
            }
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

    private void process(Dataset in, double learningRate) {
        List<Neuron> tmp = new ArrayList<>();
        if (ENABLE_NEURON_POTENTIAL) {
            tmp.addAll(neuronsWithHighPotential(neurons));
        } else {
            tmp.addAll(neurons);
        }
        Collections.sort(tmp, new DatasetDistanceComparator(in));
        //bierzemy najblizszego wzorca - zwyciezce
        Neuron winner = tmp.get(0);
        winner.moveForward(in, learningRate);
        if (ENABLE_NEURON_POTENTIAL) {
            winner.decreasePotential(POTENTIAL_DECRASE_RATE);
        }

        if (!WINNER_TAKES_ALL) {
            tmp.remove(winner);
            Collections.sort(tmp, new DatasetDistanceComparator(winner));
            for (int i = 0; i < tmp.size(); i++) {
                //System.out.println(gaussianNeibourghood(in, tmp.get(i)));
                tmp.get(i).moveForward(in, learningRate * gaussianNeibourghood(winner, tmp.get(i)));
                if (ENABLE_NEURON_POTENTIAL) {
                    tmp.get(0).decreasePotential(POTENTIAL_DECRASE_RATE);
                }
            }
        }

        for (Neuron n : neurons) {
            if (ENABLE_NEURON_POTENTIAL && !tmp.contains(n) && !winner.equals(n)) {
                n.rest(POTENTIAL_INCRASE_RATE);
            }
        }
    }

    private List<Neuron> neuronsWithHighPotential(List<Neuron> list) {
        ArrayList<Neuron> tmp = new ArrayList<>();

        for (Neuron n : list) {
            if (n.potential() >= MIN_POTENTIAL) {
                tmp.add(n);
            }
        }

        return tmp;
    }

    private double gaussianNeibourghood(Dataset in, Neuron ne) {
        return Math.exp(-1d * (Utils.power(ne.distanceTo(in)) / 2d * Utils.power(LAMBDA)));
    }

    private void savePlotCommand(int iterations, String plotFilePath, boolean twoPhase) {
        String header;
        if (twoPhase) {
            header = "Dwufazowy algorytm Kohonena";
        } else {
            header = "Algorytm Kohonena";
        }
        try (PrintStream out = new PrintStream(new FileOutputStream(plotFilePath))) {
            out.println("set terminal gif animate delay 50");
            out.println("set output '" + outputFilePrefix + "_animation.gif" + "'");
            out.println("set key outside");
            iterations = iterations - 1;
            out.println("do for [i=0:" + iterations + "] {");
            out.println("set title \'" + header + ", iteracja \'.i");
            out.print("plot \'" + this.outputFilePrefix + "_it\'.i title \'Neurony\'");
            out.print(", \'" + this.outputFilePrefix + "_inputs\' title \'Wzorzec\'");
            //for (int i = 1; i < groups; i++) {
            //    out.print(", \'" + this.outputFilePrefix + "_group" + i + "_it\'.i title \'Grupa " + i + "\'");
            //}
            //out.print(", \'_centroids\'.i title \'Centroidy\' pt 26 ps 3 lc rgb \"blue\"");

            out.println();
            out.println("}");
            out.println();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
