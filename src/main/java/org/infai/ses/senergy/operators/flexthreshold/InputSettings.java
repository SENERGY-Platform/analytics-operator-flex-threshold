package org.infai.ses.senergy.operators.flexthreshold;

public class InputSettings {
    private double threshold;
    private boolean upperLimit;
    private int points;

    public InputSettings(double threshold, boolean upperThreshold, int points) {
        this.threshold = threshold;
        this.upperLimit = upperThreshold;
        this.points = points;
    }

    public boolean isUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(boolean upperLimit) {
        this.upperLimit = upperLimit;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
