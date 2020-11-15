/*
 * Copyright (c) 2020 RGSW
 * Licensed under Apache 2.0 license
 */

package net.shadew.ptg.noise.worley;

import net.shadew.ptg.noise.BaseNoise2D;

/**
 * Inverse-Fractal-Worley noise generator for 2D space. This generator uses a specified amount of {@link
 * Worley2D}-instances as octaves.
 */
public class InverseFractalWorley2D extends BaseNoise2D {

    private final Worley2D[] noiseOctaves;

    /**
     * Constructs a Inverse-Fractal-Worley noise generator.
     *
     * @param seed    The seed, may be any {@code int}.
     * @param octaves The amount of octaves.
     */
    public InverseFractalWorley2D(int seed, int octaves) {
        super(seed);

        if (octaves < 1) {
            throw new IllegalArgumentException("There should be at least one octave.");
        }

        noiseOctaves = new Worley2D[octaves];

        for (int i = 0; i < octaves; i++) {
            noiseOctaves[i] = new Worley2D(seed);
        }
    }

    /**
     * Constructs a Inverse-Fractal-Worley noise generator.
     *
     * @param seed    The seed, may be any {@code int}.
     * @param scale   The coordinate scaling along every axis.
     * @param octaves The amount of octaves.
     */
    public InverseFractalWorley2D(int seed, double scale, int octaves) {
        super(seed, scale);

        if (octaves < 1) {
            throw new IllegalArgumentException("There should be at least one octave.");
        }

        noiseOctaves = new Worley2D[octaves];

        for (int i = 0; i < octaves; i++) {
            noiseOctaves[i] = new Worley2D(seed);
        }
    }

    /**
     * Constructs a Inverse-Fractal-Worley noise generator.
     *
     * @param seed    The seed, may be any {@code int}.
     * @param scaleX  The coordinate scaling along X axis.
     * @param scaleY  The coordinate scaling along Y axis.
     * @param octaves The amount of octaves.
     */
    public InverseFractalWorley2D(int seed, double scaleX, double scaleY, int octaves) {
        super(seed, scaleX, scaleY);

        if (octaves < 1) {
            throw new IllegalArgumentException("There should be at least one octave.");
        }

        noiseOctaves = new Worley2D[octaves];

        for (int i = 0; i < octaves; i++) {
            noiseOctaves[i] = new Worley2D(seed);
        }
    }

    @Override
    public double generate(double x, double y) {
        x /= scaleX;
        y /= scaleY;

        double d = 1;
        double n = 0;

        for (Worley2D noise : noiseOctaves) {
            n += noise.generate(x / d, y / d) * d;
            d *= 2;
        }
        return n;
    }

    @Override
    public void setSeed(int seed) {
        this.seed = seed;
        for (Worley2D value : noiseOctaves) {
            value.setSeed(seed++);
        }
    }
}
