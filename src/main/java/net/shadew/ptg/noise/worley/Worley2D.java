/*
 * Copyright (c) 2020 RGSW
 * Licensed under Apache 2.0 license
 */

package net.shadew.ptg.noise.worley;

import net.shadew.ptg.noise.BaseNoise2D;
import net.shadew.ptg.noise.util.Hash;

/**
 * 2D Worley (voronoi, distance to point) noise generator.
 */
public class Worley2D extends BaseNoise2D {

    /**
     * Constructs a Worley noise generator
     *
     * @param seed The seed, may be any {@link int}
     */
    public Worley2D(int seed) {
        super(seed);
    }

    /**
     * Constructs a Worley noise generator
     *
     * @param seed  The seed, may be any {@link int}
     * @param scale The coordinate scaling along all axes
     */
    public Worley2D(int seed, double scale) {
        super(seed, scale);
    }

    /**
     * Constructs a Worley noise generator
     *
     * @param seed   The seed, may be any {@link int}
     * @param scaleX The coordinate scaling along X axis
     * @param scaleY The coordinate scaling along Y axis
     */
    public Worley2D(int seed, double scaleX, double scaleY) {
        super(seed, scaleX, scaleY);
    }

    private int hashx(long x, long y) {
        return Hash.hash2I(seed * 12551 + 2315, x, y);
    }

    private int hashy(long x, long y) {
        return Hash.hash2I(seed * 5281 + 1121, x, y);
    }

    @Override
    public double generate(double x, double y) {
        x /= scaleX;
        y /= scaleY;

        return Worley.compute(x, y, this::hashx, this::hashy);
    }
}
