package ar.edu.itba.ss.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import ar.edu.itba.ss.Args;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Parser {
    private Args args;
    private String defaultInputUrl;
    private String defaultOutputUrl;

    public InputFormat parseJson() throws FileNotFoundException {

        String inputUrl;
        if (args.getInputFilePath() == null || args.getInputFilePath().isBlank()) {
            inputUrl = defaultInputUrl;
        } else {
            inputUrl = args.getInputFilePath();
        }

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(inputUrl));
        return gson.fromJson(reader, InputFormat.class);
    }

    public void dumpToJson(Object output) throws IOException {

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        String outputUrl;
        if (args.getOutputFilePath() == null || args.getOutputFilePath().isBlank()) {
            outputUrl = defaultOutputUrl;
        } else {
            outputUrl = args.getOutputFilePath();
        }

        File file = new File(outputUrl);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        FileWriter fw = new FileWriter(file);
        gson.toJson(output, fw);
        fw.close();
    }
}