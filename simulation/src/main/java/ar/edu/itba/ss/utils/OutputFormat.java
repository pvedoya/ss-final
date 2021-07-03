package ar.edu.itba.ss.utils;

import java.util.List;

import ar.edu.itba.ss.models.OutputParticle;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutputFormat {
    private int redDeaths;
    private int blueDeaths;
    private List<List<OutputParticle>> states;
    private double l;
    private double r;
    private double dt;
    private double totalTime;
}