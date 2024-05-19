package it.dominick.lzp.utils;

import java.util.Random;

public class RNG extends Random {
    public static final RNG r = new RNG();

    public float f(float lowerBound, float upperBound) {
        return lowerBound + (nextFloat() * ((upperBound - lowerBound)));
    }

}
