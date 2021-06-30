package ar.edu.itba.ss;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class App {
    private static String STATIC_PATH = "generated-files/input";
    private static String JSON = ".json";

    private static double GRID_SIZE = 30;
    private static int FACTIONS = 2;
    private static double MAX_RADIUS = 0.32;

    public static void main(String[] argv) {

        // Parse arguments
        Args args = new Args();
        try {
            JCommander.newBuilder().addObject(args).build().parse(argv);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        List<Soldier> soldiers = new ArrayList<>();

        System.out.println("======================================");
        System.out.println("Generator Parameters:");
        System.out.println("Output: " + args.getOutputFileUrl());
        System.out.println("Soldiers: " + args.getSoldiers());
        System.out.println("Red formation: " + args.getRedFormation());
        System.out.println("Blue formation: " + args.getBlueFormation());
        System.out.println("--------------------------------------");

        // Generate factions
        int idCounter = 0;

        // First faction
        List<Soldier> redSoldiers;
        switch (args.getRedFormation()) {
            case "phalanx":
                redSoldiers = generatePhalanx("red", args.getSoldiers(), GRID_SIZE, idCounter);
                break;
            case "testudo":
                redSoldiers = generateTestudo("red", args.getSoldiers(), GRID_SIZE, idCounter);
                break;
            case "shieldwall":
                redSoldiers = generateShieldwall("red", args.getSoldiers(), GRID_SIZE, idCounter);
                break;
            default:
                throw new IllegalArgumentException("Wrong faction input");
        }

        soldiers.addAll(redSoldiers);
        idCounter = redSoldiers.size();

        // First faction
        List<Soldier> blueSoldiers;
        switch (args.getBlueFormation()) {
            case "phalanx":
                blueSoldiers = generatePhalanx("blue", args.getSoldiers(), GRID_SIZE, idCounter);
                break;
            case "testudo":
                blueSoldiers = generateTestudo("blue", args.getSoldiers(), GRID_SIZE, idCounter);
                break;
            case "shieldwall":
                blueSoldiers = generateShieldwall("blue", args.getSoldiers(), GRID_SIZE, idCounter);
                break;
            default:
                throw new IllegalArgumentException("Wrong faction input");
        }

        soldiers.addAll(blueSoldiers);

        // Generating input JSON file
        InputFormat input = new InputFormat(GRID_SIZE, args.getSoldiers(), FACTIONS, soldiers);

        String outputURL;
        if (args.outputFileUrl == null) {
            outputURL = STATIC_PATH + "/" + "random-input" + JSON;
        } else {
            outputURL = args.outputFileUrl;
        }

        File file = new File(outputURL);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try {
            FileWriter fw = new FileWriter(file);
            gson.toJson(input, fw);
            fw.close();
        } catch (IOException e) {
            System.err.println("Couldn't create input file - IOException");
            e.printStackTrace();
            return;
        }

    }

    // Generates an (ideally) nxn formation with the strongest units on the first rows
    private static List<Soldier> generatePhalanx(String faction, int soldierAmount, double gridSize, int firstId) {
        List<Soldier> soldiers = new ArrayList<>();
        int idCounter = firstId;

        int side = (int) Math.sqrt(soldierAmount);
        int line = 1;

        double x;
        double y = 20;

        double maxY = y;

        if (faction == "red") {
            x = 20;
        } else if (faction == "blue") {
            x = 30;
        } else {
            throw new IllegalArgumentException("Wrong faction detected");
        }

        while (soldiers.size() < soldierAmount) {

            if (line <= 3) {
                soldiers.add(new Soldier(x, y, randDoubleBetween(0.5, 1), faction, idCounter++));
            } else {
                soldiers.add(new Soldier(x, y, randDoubleBetween(0, 0.5), faction, idCounter++));
            }

            if (soldiers.size() % side == 0 && soldiers.size() != 0) {
                maxY = y;
                if (faction == "red") {
                    x -= (MAX_RADIUS * 2 + 0.2);
                } else {
                    x += (MAX_RADIUS * 2 + 0.2);
                }
                y = 20;
                line++;
            } else {
                y += (MAX_RADIUS * 2 + 0.2);
            }
        }

        double mid = (maxY - 10)/2;
        double offset = 15 - (10+mid);

        for (Soldier s : soldiers) {
            s.y += offset;
        }

        return soldiers;
    }

    // Generates a mxn formation with the strongest units on the sides
    private static List<Soldier> generateTestudo(String faction, int soldierAmount, double gridSize, int firstId) {
        List<Soldier> soldiers = new ArrayList<>();
        int idCounter = firstId;

        int width = soldierAmount / 10;
        width = Math.max(3, width/2);

        int line = 1;

        double x;
        double y = 20;

        double maxY = y;

        if (faction == "red") {
            x = 20;
        } else if (faction == "blue") {
            x = 30;
        } else {
            throw new IllegalArgumentException("Wrong faction detected");
        }

        while (soldiers.size() < soldierAmount) {
            int position = 1;

            if (line == 1) {
                soldiers.add(new Soldier(x, y, randDoubleBetween(0.5, 1), faction, idCounter++));
            } else {
                if (position == 1 || position == width) {
                    position++;
                    soldiers.add(new Soldier(x, y, randDoubleBetween(0.5, 1), faction, idCounter++));

                } else {
                    soldiers.add(new Soldier(x, y, randDoubleBetween(0, 0.5), faction, idCounter++));
                }
            }

            if (soldiers.size() % width == 0 && soldiers.size() != 0) {
                maxY = y;
                position = 1;
                if (faction == "red") {
                    x -= (MAX_RADIUS * 2 + 0.2);
                } else {
                    x += (MAX_RADIUS * 2 + 0.2);
                }
                y =  20;
                line++;
            } else {
                y += (MAX_RADIUS * 2 + 0.2);
            }
        }

        double mid = (maxY - 10)/2;
        double offset = 15 - (10+mid);

        for (Soldier s : soldiers) {
            s.y += offset;
        }

        return soldiers;
    }

    private static List<Soldier> generateShieldwall(String faction, int soldierAmount, double gridSize, int firstId){
        List<Soldier> soldiers = new ArrayList<>();
        int idCounter = firstId;

        int line = 1;

        int width = soldierAmount / 10;
        width = Math.max(2, width*3);

        double x;
        double y = 20;

        double maxY = y;

        if (faction == "red") {
            x = 20;
        } else if (faction == "blue") {
            x = 30;
        } else {
            throw new IllegalArgumentException("Wrong faction detected");
        }

        while (soldiers.size() < soldierAmount) {
            if (line == 1) {
                soldiers.add(new Soldier(x, y, randDoubleBetween(0.5, 1), faction, idCounter++));
            } else {
                soldiers.add(new Soldier(x, y, randDoubleBetween(0, 0.5), faction, idCounter++));

            }

            if (soldiers.size() % width == 0 && soldiers.size() != 0) {
                maxY = y;
                if (faction == "red") {
                    x -= (MAX_RADIUS * 2 + 0.2);
                } else {
                    x += (MAX_RADIUS * 2 + 0.2);
                }
                y = 20;    
                line++;
            } else {
                y += (MAX_RADIUS * 2 + 0.2);
            }
        }

        double mid = (maxY - 10)/2;
        double offset = 15 - (10+mid);

        for (Soldier s : soldiers) {
            s.y += offset;
        }

        return soldiers;
    }

    private static double randDoubleBetween(double a, double b) {
        if (a == b) {
            return a;
        }
        return ThreadLocalRandom.current().nextDouble(a, b);
    }

    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    private static class InputFormat {
        double gridSize;
        int soldiersAmountPerFaction;
        int factions;
        List<Soldier> soldiers;
    }

    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    private static class Soldier {
        double x;
        double y;
        double training;
        String faction;
        int id;
    }
}