package com.renderer_3d;

public class Triangle {
    public Vec3 v1, v2, v3;

    public Triangle(Vec3 v1, Vec3 v2, Vec3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Triangle rotate(double angleY, double angleX) {
        return new Triangle(v1.rotate(angleY, angleX), v2.rotate(angleY, angleX), v3.rotate(angleY, angleX));
    }

    public Vec3 getNormal() {
        Vec3 edge1 = v2.sub(v1);
        Vec3 edge2 = v3.sub(v1);
        return edge1.cross(edge2).normalize();
    }
}