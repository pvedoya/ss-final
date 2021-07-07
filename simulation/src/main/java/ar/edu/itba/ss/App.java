package ar.edu.itba.ss;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import ar.edu.itba.ss.models.OutputParticle;
import ar.edu.itba.ss.models.Soldier;
import ar.edu.itba.ss.models.State;
import ar.edu.itba.ss.utils.InputFormat;
import ar.edu.itba.ss.utils.OutputFormat;
import ar.edu.itba.ss.utils.Parser;

public class App {
    private static String DEFAULT_INPUT_PATH = "generated-files/generator/random-input.json";
    private static String DEFAULT_OUTPUT_PATH = "generated-files/simulation/random-simulation.json";

    private static double DT = 0.046875;

    public static void main(String[] argv) {

        // Parse arguments
        Args args = new Args();
        try {
            JCommander.newBuilder().addObject(args).build().parse(argv);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Simulation Parameters:");
        System.out.println("Input: " + args.getInputFilePath());
        System.out.println("Output: " + args.getOutputFilePath());
        System.out.println("Max time: " + args.getMaxTime());
        // System.out.println("Soldier speed: " + args.getSoldiersSpeed());
        System.out.println("======================================");

        Parser parser = Parser.builder().args(args).defaultInputUrl(DEFAULT_INPUT_PATH)
                .defaultOutputUrl(DEFAULT_OUTPUT_PATH).build();

        InputFormat input;
        try {
            input = parser.parseJson();
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't parse input file");
            System.exit(-1);
            return;
        }

        List<Soldier> soldiers = input.getSoldiers();

        State state = new State(soldiers, input.getGridSize(), DT);
        double time = 0;

        List<List<OutputParticle>> states = new ArrayList<>();
        List<Integer> redDeaths = new ArrayList<>();
        List<Integer> blueDeaths = new ArrayList<>();

        while (state.getBlueDeaths() < state.getBlueSoldiers().size()
                && state.getRedDeaths() < state.getRedSoldiers().size()
                && (args.getMaxTime() != -1 ? time < args.getMaxTime() : true)) {

            state.simulate();

            if (time % (DT * 5) == 0) {
                states.add(saveState(state.getSoldiers()));
                redDeaths.add(state.getRedDeaths());
                blueDeaths.add(state.getBlueDeaths());
            }

            time += DT;
        }

        states.add(saveState(state.getSoldiers()));
        redDeaths.add(state.getRedDeaths());
        blueDeaths.add(state.getBlueDeaths());

        String winner;
        if(state.getRedDeaths() > state.getBlueDeaths()){
            winner = "blue";
        }
        else{
            winner = "red";
        }

        try {
            parser.dumpToJson(
                    new OutputFormat(redDeaths, blueDeaths, states, input.getSoldiers().size()/2, input.getGridSize(), 
                    Soldier.MAX_R, DT, winner));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<OutputParticle> saveState(List<Soldier> soldiers) {
        List<OutputParticle> out = new ArrayList<>();
        for (Soldier s : soldiers) {
            out.add(new OutputParticle(s.getX(), s.getY(), s.getHp(), s.getFaction(), s.getId()));
        }

        out.sort(Comparator.comparingInt(OutputParticle::getId));

        return out;
    }
}