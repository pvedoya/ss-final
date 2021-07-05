package ar.edu.itba.ss.utils;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.ss.models.Soldier;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InputFormat {
    double gridSize;
    // int soldiersAmountPerFaction;
    int blueSoldiers;
    int redSoldiers;
    int factions;
    List<InputSoldier> soldiers;

    public List<Soldier> getSoldiers() {
        List<Soldier> retSoldiers = new ArrayList<>();

        for (InputSoldier is : soldiers) {
            retSoldiers.add(new Soldier(is.x, is.y, is.training, is.faction, is.id));
        }

        return retSoldiers;
    }

    @Data
    @AllArgsConstructor
    private static class InputSoldier {
        double x;
        double y;
        double training;
        String faction;
        int id;
    }
}
