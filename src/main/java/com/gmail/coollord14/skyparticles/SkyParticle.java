package com.gmail.coollord14.skyparticles;

import org.bukkit.Location;
import org.bukkit.Particle;

public class SkyParticle {
    private final String name;
    private final Particle particle;
    private final double distance;
    private final int count;
    private final double speed;
    private final boolean enabled;
    private final Location min;
    private final Location max;

    public SkyParticle(String name, Particle particle, double distance, int count, double speed, boolean enabled, Location min, Location max) {
        this.name = name;
        this.particle = particle;
        this.distance = distance;
        this.count = count;
        this.speed = speed;
        this.enabled = enabled;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public Particle getParticle() {
        return particle;
    }

    public double getDistance() {
        return distance;
    }

    public int getCount() {
        return count;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Location getMin() {
        return min;
    }

    public Location getMax() {
        return max;
    }
}
