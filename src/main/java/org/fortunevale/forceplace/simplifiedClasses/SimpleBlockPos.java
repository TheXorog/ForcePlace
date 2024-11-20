package org.fortunevale.forceplace.simplifiedClasses;

import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class SimpleBlockPos {
    public Integer X;
    public Integer Y;
    public Integer Z;

    public SimpleBlockPos(BlockPos pos)
    {
        X = pos.getX();
        Y = pos.getY();
        Z = pos.getZ();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleBlockPos that = (SimpleBlockPos) o;
        return Objects.equals(X, that.X) && Objects.equals(Y, that.Y) && Objects.equals(Z, that.Z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y, Z);
    }

    @Override
    public String toString() {
        return "SimpleBlockPos{" +
                "X=" + X +
                ", Y=" + Y +
                ", Z=" + Z +
                '}';
    }
}
