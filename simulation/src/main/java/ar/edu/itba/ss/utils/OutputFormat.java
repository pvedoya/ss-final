package ar.edu.itba.ss.utils;

import java.util.List;

import ar.edu.itba.ss.models.OutputParticle;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutputFormat {
    private List<Integer> redDeaths;
    private List<Integer> blueDeaths;
    private List<List<OutputParticle>> states;
    private int n;
    private double l;
    private double r;
    private double dt;
    private String winner;
}