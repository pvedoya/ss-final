package ar.edu.itba.ss.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Soldier {

    public final static double MAX_R = 0.32;
    public final static double MIN_R = 0.15;

    public final static double TAU = 0.5;
    public final static double BETA = 1;

    public final static double SPEED = 1.6;

    public final static double MAX_DPS = 10;
    public final static double MAX_HP = 100;

    @Getter
    @Setter
    private double x;

    @Getter
    @Setter
    private double y;

    @Getter
    @Setter
    private double v;

    @Getter
    @Setter
    private double omega;

    @Getter
    @Setter
    private double r;

    @Getter
    @Setter
    private double hp;

    @Getter
    private double dps;

    @Getter
    private String faction;

    @Getter
    private final int id;

    @Getter
    @Setter
    private boolean fighting;

    @Setter
    private boolean attacked;

    public Soldier(double x, double y, double training, String faction, int id) {
        this.x = x;
        this.y = y;

        this.hp = training * MAX_HP;
        this.dps = training * MAX_DPS;

        this.faction = faction;

        this.v = SPEED;
        if (faction.equals("red")) {
            this.omega = 0;
        } else {
            this.omega = Math.PI;
        }

        this.r = MAX_R;

        this.id = id;

        this.fighting = false;

        this.attacked = false;
    }

    public void moveToNearestEnemy(List<Soldier> enemies) {
        Soldier nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        double nearestAngle = Double.POSITIVE_INFINITY;

        for (Soldier s : enemies) {
            // double[] limits = { omega - Math.PI / 2, omega + Math.PI / 2 };
            double angleBetween = calculateAngleBetween(s.x, s.y);

            // if (angleBetween <= limits[1] && angleBetween >= limits[0]) {
            double distance = calculateDistance(s.x, s.y) - 2 * MAX_R;
            if (distance < nearestDistance) {
                nearest = s;
                nearestDistance = distance;
                nearestAngle = angleBetween;
            }
            // }
        }

        if (nearest != null) {
            double targetAngle;

            if (faction.equals("red")) {
                targetAngle = 0;
            } else {
                targetAngle = Math.PI;
            }

            double[] v = { Math.cos(targetAngle), Math.sin(targetAngle) };

            double nc = 1.25 * Math.exp(-nearestDistance / 1.25);

            v[0] = v[0] + nc * Math.cos(nearestAngle);
            v[1] = v[1] + nc * Math.sin(nearestAngle);

            if (v[0] == 0) {
                omega = Math.signum(v[1]) * (Math.PI / 2);
            } else {
                omega = Math.atan2(v[1], v[0]);
            }
        }
    }

    public void move(double spaceSize, double dt) {
        double dx = Math.cos(omega) * v * dt;
        double dy = Math.sin(omega) * v * dt;

        if (x + dx <= 0 || x + dx >= spaceSize) {
            dx = 0;
        }

        if (y + dy >= spaceSize || y + dy <= 0) {
            dy = 0;
        }

        x = x + dx;
        y = y + dy;
    }

    public void updateParticleState(double dt) {
        attacked = false;

        if (r < MAX_R) {
            r += MAX_R / (TAU / dt);
        }
        v = SPEED * Math.pow(((r - MIN_R) / (MAX_R - MIN_R)), BETA);

    }

    public double calculateAngleBetween(double x2, double y2) {
        double distance = calculateDistance(x2, y2);
        double dy = y2 - y;
        double dx = x2 - x;

        double aux = Math.signum(dy);
        if (aux == 0) {
            aux = 1;
        }

        return aux * Math.acos(dx / distance);
    }

    public double calculateDistance(double x2, double y2) {
        double dx = x2 - x;
        double dy = y2 - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean hasAttacked() {
        return attacked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        Soldier p = (Soldier) o;
        return this.id == p.id;
    }

}
