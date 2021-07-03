package ar.edu.itba.ss;

import com.beust.jcommander.Parameter;

import lombok.Getter;

@Getter
public class Args {

        // Input File
        @Parameter(names = { "-i",
                        "--input-file-name" }, description = "Path of the generated input file", required = false)
        String inputFilePath;

        // Output File
        @Parameter(names = { "-o",
                        "--output-file-name" }, description = "Path of the generated output file", required = false)
        String outputFilePath;

        // Simulation params
        @Parameter(names = { "-t",
                        "--simulation-max-time" }, description = "Maximum time the simulation can take in ms", required = false)
        double maxTime = -1;

        // // Soldiers params
        // @Parameter(names = { "-v",
        //                 "--soldiers-speed" }, description = "Speed for all soldiers", required = true)
        // int soldiersSpeed;

}