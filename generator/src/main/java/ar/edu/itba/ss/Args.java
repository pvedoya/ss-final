package ar.edu.itba.ss;

import com.beust.jcommander.Parameter;

import lombok.Getter;

@Getter
public class Args {

        // Output File
        @Parameter(names = { "-o",
                        "--output-file-name" }, description = "Path of the generated output file", required = false)
        String outputFileUrl;

        // Soldiers params
        @Parameter(names = { "-rn",
                        "--red-soldiers-amount" }, description = "Amount of agent particles per red faction", required = true)
        int redSoldiers;

        @Parameter(names = { "-bn",
                        "--blue-soldiers-amount" }, description = "Amount of agent particles per blue faction", required = true)
        int blueSoldiers;

        @Parameter(names = { "-bf", "--blue-formation" }, description = "Formation for the blue faction", required = true)
        String blueFormation;

        @Parameter(names = { "-rf", "--red-formation" }, description = "Formation for the red faction", required = true)
        String redFormation;

}
