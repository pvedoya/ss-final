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

        this.contact = true;
    }

    public void simulate() {
        // Check for CPM collisions
        for (int i = 0; i < redSoldiers.size(); i++) {
            for (int j = i + 1; j < redSoldiers.size(); j++) {
                Soldier s1 = redSoldiers.get(i);
                Soldier s2 = redSoldiers.get(j);

                if (s1.getHp() > 0 && s2.getHp() > 0) {
                    if (s1.calculateDistance(s2.getX(), s2.getY()) <= (s1.getR() + s2.getR())) {
                        double angleBetween = s1.calculateAngleBetween(s2.getX(), s2.getY());

                        s1.setR(Soldier.MIN_R);
                        s1.setV(Soldier.SPEED);

                        s1.setCpm(true);
                        s1.setOmega(angleBetween - Math.signum(s1.getOmega()) * Math.PI);

                        s2.setR(Soldier.MIN_R);
                        s2.setV(Soldier.SPEED);

                        s2.setCpm(true);
                        s2.setOmega(angleBetween);

                    }
                }
            }
        }

        for (int i = 0; i < blueSoldiers.size(); i++) {
            for (int j = i + 1; j < blueSoldiers.size(); j++) {
                Soldier s1 = blueSoldiers.get(i);
                Soldier s2 = blueSoldiers.get(j);

                if (s1.getHp() > 0 && s2.getHp() > 0) {
                    if (s1.calculateDistance(s2.getX(), s2.getY()) <= (s1.getR() + s2.getR())) {
                        double angleBetween = s1.calculateAngleBetween(s2.getX(), s2.getY());

                        s1.setR(Soldier.MIN_R);
                        s1.setV(Soldier.SPEED);

                        s1.setCpm(true);
                        s1.setOmega(angleBetween - Math.signum(s1.getOmega()) * Math.PI);

                        s2.setR(Soldier.MIN_R);
                        s2.setV(Soldier.SPEED);

                        s2.setCpm(true);
                        s2.setOmega(angleBetween);
                    }
                }
            }
        }

        // Fight!
        for (Soldier redSoldier : redSoldiers) {
            for (Soldier blueSoldier : blueSoldiers) {

                if (blueSoldier.getHp() > 0 && redSoldier.getHp() > 0
                        && redSoldier.calculateDistance(blueSoldier.getX(),
                                blueSoldier.getY()) <= (redSoldier.getR() + blueSoldier.getR())) {

                    contact = true;
                    if (!redSoldier.hasAttacked()) {
                        redSoldier.setV(0);
                        blueSoldier.setV(0);

                        redSoldier.setAttacked(true);

                        double newHp = blueSoldier.getHp() - redSoldier.getDps() * dt;
                        blueSoldier.setHp(newHp);
                    }

                    if (!blueSoldier.hasAttacked()) {
                        redSoldier.setV(0);
                        blueSoldier.setV(0);

                        blueSoldier.setAttacked(true);

                        double newHp = redSoldier.getHp() - blueSoldier.getDps() * dt;
                        redSoldier.setHp(newHp);
                    }

                    if (blueSoldier.getHp() <= 0 && blueSoldier.hasAttacked()) {
                        blueSoldier.setHp(0);
                        blueSoldier.setX(200);
                        blueDeaths++;
                    }

                    if (redSoldier.getHp() <= 0 && redSoldier.hasAttacked()) {
                        redSoldier.setHp(0);
                        redSoldier.setX(200);
                        redDeaths++;
                    }

                }
            }
        }

        // Makes next move
        for (Soldier redSoldier : redSoldiers) {
            if (redSoldier.getHp() > 0 && contact && !redSoldier.isCpm() && !redSoldier.hasAttacked()) {
                redSoldier.moveToNearestEnemy(blueSoldiers, spaceSize);
            }
        }

        for (Soldier blueSoldier : blueSoldiers) {
            if (blueSoldier.getHp() > 0 && contact && !blueSoldier.isCpm() && !blueSoldier.hasAttacked()) {
                blueSoldier.moveToNearestEnemy(redSoldiers, spaceSize);
            }
        }

        // Calculate new state
        for (Soldier soldier : soldiers) {
            if (soldier.getHp() > 0) {
                soldier.move(spaceSize, dt);
                soldier.updateParticleState(dt);
            }
            soldier.setAttacked(false);
            soldier.setCpm(false);
        }
    }

}
