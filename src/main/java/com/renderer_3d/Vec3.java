package com.renderer_3d;

public class Vec3 {
    public double x, y, z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3() {
    }

    public Vec3 rotate(double angleY, double angleX) {
        double cos = Math.cos(angleY);
        double sin = Math.sin(angleY);
        double nx = x * cos - z * sin;
        double nz = x * sin + z * cos;

        cos = Math.cos(angleX);
        sin = Math.sin(angleX);
        double ny = y * cos - nz * sin;
        nz = y * sin + nz * cos;
        return new Vec3(nx, ny, nz);
    }

    public Vec3 sub(Vec3 o) {
        return new Vec3(x - o.x, y - o.y, z - o.z);
    }

    public Vec3 cross(Vec3 o) {
        return new Vec3(
                y * o.z - z * o.y,
                z * o.x - x * o.z,
                x * o.y - y * o.x
        );
    }

    public Vec3 normalize() {
        double len = Math.sqrt(x*x + y*y + z*z);
        if (len == 0) return new Vec3(0, 0, 0);
        return new Vec3(x / len, y / len, z / len);
    }
}