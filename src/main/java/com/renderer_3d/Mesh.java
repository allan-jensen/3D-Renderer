package com.renderer_3d;

public class Mesh {
    public Triangle[] triangles;

    public Mesh(Triangle[] triangles) {
        this.triangles = triangles;

        if (triangles == null) this.triangles = getCubeMesh();
    }

    private Triangle[] getCubeMesh() {
        return new Triangle[]{
                // Frente
                new Triangle(new Vec3(-0.5, -0.5, -0.5), new Vec3(0.5, -0.5, -0.5), new Vec3(0.5, 0.5, -0.5)),
                new Triangle(new Vec3(-0.5, -0.5, -0.5), new Vec3(0.5, 0.5, -0.5), new Vec3(-0.5, 0.5, -0.5)),
                // Trás
                new Triangle(new Vec3(-0.5, -0.5, 0.5), new Vec3(0.5, 0.5, 0.5), new Vec3(0.5, -0.5, 0.5)),
                new Triangle(new Vec3(-0.5, -0.5, 0.5), new Vec3(-0.5, 0.5, 0.5), new Vec3(0.5, 0.5, 0.5)),
                // Esquerda
                new Triangle(new Vec3(-0.5, -0.5, -0.5), new Vec3(-0.5, 0.5, 0.5), new Vec3(-0.5, -0.5, 0.5)),
                new Triangle(new Vec3(-0.5, -0.5, -0.5), new Vec3(-0.5, 0.5, -0.5), new Vec3(-0.5, 0.5, 0.5)),
                // Direita
                new Triangle(new Vec3(0.5, -0.5, -0.5), new Vec3(0.5, -0.5, 0.5), new Vec3(0.5, 0.5, 0.5)),
                new Triangle(new Vec3(0.5, -0.5, -0.5), new Vec3(0.5, 0.5, 0.5), new Vec3(0.5, 0.5, -0.5)),
                // Topo
                new Triangle(new Vec3(-0.5, 0.5, -0.5), new Vec3(0.5, 0.5, 0.5), new Vec3(-0.5, 0.5, 0.5)),
                new Triangle(new Vec3(-0.5, 0.5, -0.5), new Vec3(0.5, 0.5, -0.5), new Vec3(0.5, 0.5, 0.5)),
                // Base
                new Triangle(new Vec3(-0.5, -0.5, -0.5), new Vec3(-0.5, -0.5, 0.5), new Vec3(0.5, -0.5, 0.5)),
                new Triangle(new Vec3(-0.5, -0.5, -0.5), new Vec3(0.5, -0.5, 0.5), new Vec3(0.5, -0.5, -0.5))
        };
    }

    public Mesh rotate(double angleY, double angleX) {
        Triangle[] rotatedTriangles = new Triangle[triangles.length];
        for (int i = 0; i < triangles.length; i++) {
            rotatedTriangles[i] = triangles[i].rotate(angleY, angleX);
        }
        return new Mesh(rotatedTriangles);
    }

    public BoundingBox getBoundingBox() {
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;
        double minZ = 0;
        double maxZ = 0;

        java.util.List<Vec3> vertices = new java.util.ArrayList<>();
        for (Triangle triangle : this.triangles) {
            vertices.add(triangle.v1);
            vertices.add(triangle.v2);
            vertices.add(triangle.v3);
        }

        for (Vec3 vertex : vertices) {
            if (vertex.x < minX) minX = vertex.x;
            if (vertex.x > maxX) maxX = vertex.x;
            if (vertex.y < minY) minY = vertex.y;
            if (vertex.y > maxY) maxY = vertex.y;
            if (vertex.z < minZ) minZ = vertex.z;
            if (vertex.z > maxZ) maxZ = vertex.z;
        }
        return new BoundingBox(minX, maxX, minY, maxY, minZ, maxZ);
    }

    public record BoundingBox(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
    }

    public void centralize() {
        Mesh.BoundingBox boundingBox = this.getBoundingBox();

        double centerX = (boundingBox.minX() + boundingBox.maxX()) / 2;
        double centerY = (boundingBox.minY() + boundingBox.maxY()) / 2;
        double centerZ = (boundingBox.minZ() + boundingBox.maxZ()) / 2;
        Triangle[] centralizedTriangles = new Triangle[this.triangles.length];
        for (int i = 0; i < this.triangles.length; i++) {
            Triangle tri = this.triangles[i];
            Vec3 v1 = new Vec3(tri.v1.x - centerX, tri.v1.y - centerY, tri.v1.z - centerZ);
            Vec3 v2 = new Vec3(tri.v2.x - centerX, tri.v2.y - centerY, tri.v2.z - centerZ);
            Vec3 v3 = new Vec3(tri.v3.x - centerX, tri.v3.y - centerY, tri.v3.z - centerZ);
            centralizedTriangles[i] = new Triangle(v1, v2, v3);
        }
        this.triangles = centralizedTriangles;
    }
}
