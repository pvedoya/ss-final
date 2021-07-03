package ar.edu.itba.ss.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OutputParticle {
    private double x;
    private double y;
    private double hp;
    private String faction;
    private int id;
}