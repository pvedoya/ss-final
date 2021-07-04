package ar.edu.itba.ss.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class State {

    private double spaceSize;

    private double dt;

    @Getter
    private List<Soldier> soldiers;

    @Getter
    private List<Soldier> redSoldiers;

    @Getter
    private int redDeaths;

    @Getter
    private List<Soldier> blueSoldiers;

    @Getter
    private int blueDeaths;

    private boolean contact;

    public State(List<Soldier> soldiers, double spaceSize, double dt) {
        this.soldiers = soldiers;

        this.redSoldiers = new ArrayList<>();
        this.blueSoldiers = new ArrayList<>();

        for (Soldier soldier : soldiers) {
            if (soldier.getFaction().equals("red")) {
                redSoldiers.add(soldier);
            } else {
                blueSoldiers.add(soldier);
            }
        }

        this.spaceSize = spaceSize;

        this.dt = dt;

        this.redDeaths = 0;
        this.blueDeaths = 0;

        this.contact = false;
    }

    public void simulate() {

        // Update particle states
        for (Soldier soldier : soldiers) {
            if (soldier.getHp() > 0 && !soldier.isFighting()) {
                soldier.updateParticleState(dt);
                soldier.setCpm(false);
                soldier.setAttacked(false);
            }
        }

        // Check for CPM collisions
        for (int i = 0; i < redSoldiers.size(); i++) {
            for (int j = i + 1; j < redSoldiers.size(); j++) {
                Soldier s1 = redSoldiers.get(i);
                Soldier s2 = redSoldiers.get(j);

                if (s1.getHp() > 0 && s2.getHp() > 0) {
                    if (s1.calculateDistance(s2.getX(), s2.getY()) < (s1.getR() + s2.getR())) {
                        double angleBetween = s1.calculateAngleBetween(s2.getX(), s2.getY());

                        if (!s1.isFighting()) {
                            s1.setR(Soldier.MIN_R);
                            s1.setV(Soldier.SPEED);

                            s1.setCpm(true);
                            s1.setOmega(angleBetween - Math.signum(s1.getOmega()) * Math.PI);
                        }
                        
                        if (!s2.isFighting()) {
                            s2.setR(Soldier.MIN_R);
                            s2.setV(Soldier.SPEED);

                            s2.setCpm(true);
                            s2.setOmega(angleBetween);
                        }

                    }
                }
            }
        }

        for (int i = 0; i < blueSoldiers.size(); i++) {
            for (int j = i + 1; j < blueSoldiers.size(); j++) {
                Soldier s1 = blueSoldiers.get(i);
                Soldier s2 = blueSoldiers.get(j);

                if (s1.getHp() > 0 && s2.getHp() > 0) {
                    if (s1.calculateDistance(s2.getX(), s2.getY()) < (s1.getR() + s2.getR())) {
                        double angleBetween = s1.calculateAngleBetween(s2.getX(), s2.getY());
                        
                        if (!s1.isFighting()) {
                            s1.setR(Soldier.MIN_R);
                            s1.setV(Soldier.SPEED);

                            s1.setCpm(true);
                            s1.setOmega(angleBetween - Math.signum(s1.getOmega()) * Math.PI);
                        }
                        if (!s2.isFighting()) {
                            s2.setR(Soldier.MIN_R);
                            s2.setV(Soldier.SPEED);

                            s2.setCpm(true);
                            s2.setOmega(angleBetween);
                        }

                    }
                }
            }
        }

        // Fight!
        for (Soldier redSoldier : redSoldiers) {
            for (Soldier blueSoldier : blueSoldiers) {
                if (redSoldier.calculateDistance(blueSoldier.getX(),
                        blueSoldier.getY()) < (redSoldier.getR() + blueSoldier.getR())) {

                    contact = true;
                    if (!redSoldier.hasAttacked() && blueSoldier.getHp() > 0 && redSoldier.getHp() > 0 && !redSoldier.isCpm()) {
                        redSoldier.setV(0);
                        blueSoldier.setV(0);

                        redSoldier.setAttacked(true);
                        redSoldier.setFighting(true);

                        double newHp = blueSoldier.getHp() - redSoldier.getDps();
                        blueSoldier.setHp(newHp);
                    }   

                    if (!blueSoldier.hasAttacked() && redSoldier.getHp() > 0 && blueSoldier.getHp() > 0 && !blueSoldier.isCpm()) {
                        redSoldier.setV(0);
                        blueSoldier.setV(0);

                        blueSoldier.setAttacked(true);
                        blueSoldier.setFighting(true);

                        double newHp = redSoldier.getHp() - blueSoldier.getDps();
                        redSoldier.setHp(newHp);
                    }
                    
                    boolean fightEnded = false;

                    if (blueSoldier.getHp() <= 0 && blueSoldier.isFighting()) {
                        fightEnded = true;

                        blueSoldier.setHp(0);
                        blueDeaths++;
                    }

                    if (redSoldier.getHp() <= 0 && redSoldier.isFighting()) {
                        fightEnded = true;

                        redSoldier.setHp(0);
                        redDeaths++;
                    }

                    if(fightEnded){
                        blueSoldier.setFighting(false);
                        redSoldier.setFighting(false);
                    }
                }
            }
        }

        // Makes next move
        for (Soldier redSoldier : redSoldiers) {
            if (redSoldier.getHp() > 0 && !redSoldier.isFighting() && contact && !redSoldier.isCpm()) {
                redSoldier.moveToNearestEnemy(blueSoldiers, spaceSize);
            }
        }

        for (Soldier blueSoldier : blueSoldiers) {
            if (blueSoldier.getHp() > 0 && !blueSoldier.isFighting() && contact && !blueSoldier.isCpm()) {
                blueSoldier.moveToNearestEnemy(redSoldiers, spaceSize);
            }
        }

        // Calculate new positions and reset attacks
        for (Soldier soldier : soldiers) {
            if (soldier.getHp() > 0 && !soldier.hasAttacked() && !soldier.isFighting()) {
                soldier.move(spaceSize, dt);
                soldier.setAttacked(false);
            }
        }

    }

}
