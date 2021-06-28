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

    private static double GRID_SIZE = 20;
    private static int FACTIONS = 2;

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
        List<Particle> soldiers = new ArrayList<>();

        System.out.println("======================================");
        System.out.println("Generator Parameters:");
        System.out.println("Output: " + args.getOutputFileUrl());
        System.out.println("Soldiers: " + args.getSoldiers());
        System.out.println("Red formation: " + args.getRedFormation());
        System.out.println("Blue formation: " + args.getBlueFormation());
        System.out.println("--------------------------------------");

        double particleRadius = 0.32; 

        // Generate factions


        // Generating input JSON file
        InputFormat input = new InputFormat(GRID_SIZE, args.getSoldiers(), FACTIONS, soldiers);

        String outputURL;
        if(args.outputFileUrl == null){
            outputURL = STATIC_PATH + "/" + "random-input" + JSON;
        }else {
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

    private static boolean hasSuperPosition(Particle p, List<Particle> particles, double radius) {
        for (Particle other : particles) {
            double dx = other.x - p.x;
            double dy = other.y - p.y;
            double distance = Math.sqrt(dx*dx + dy*dy);
            if (distance < 2*radius) {
                return true;
            }
        }
        return false;
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
        List<Particle> soldiers;
    }

    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    private static class Particle {
        double x;
        double y;
        String faction;
        int id;
    }
}