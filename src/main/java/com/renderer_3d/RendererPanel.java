package com.renderer_3d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class RendererPanel extends JPanel {
    private final int width;
    private final int height;
    private final double SCALE;
    private double CAMERA_DIST = 10.0;
    private double angleY = 0;
    private double angleX = 0;
    private final Mesh mesh;
    BufferedImage framebuffer;
    double[][] zbuffer;
    Vec3 lightDir = new Vec3(0, 0, -1).normalize();

    private int lastMouseX, lastMouseY;
    private boolean dragging = false;

    public void clearBuffers() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                zbuffer[x][y] = Double.POSITIVE_INFINITY;
                framebuffer.setRGB(x, y, 0xFF000000);
            }
        }
    }

    void drawTriangle(Vec3 v0, Vec3 v1, Vec3 v2, int color) {
        int minX = (int) Math.max(0, Math.ceil(Math.min(v0.x, Math.min(v1.x, v2.x))));
        int maxX = (int) Math.min(width - 1, Math.floor(Math.max(v0.x, Math.max(v1.x, v2.x))));
        int minY = (int) Math.max(0, Math.ceil(Math.min(v0.y, Math.min(v1.y, v2.y))));
        int maxY = (int) Math.min(height - 1, Math.floor(Math.max(v0.y, Math.max(v1.y, v2.y))));

        double area = edgeFunction(v0, v1, v2);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                Vec3 p = new Vec3();
                p.x = x + 0.5f;
                p.y = y + 0.5f;

                double w0 = edgeFunction(v1, v2, p);
                double w1 = edgeFunction(v2, v0, p);
                double w2 = edgeFunction(v0, v1, p);

                if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                    w0 /= area;
                    w1 /= area;
                    w2 /= area;

                    double z = v0.z * w0 + v1.z * w1 + v2.z * w2;

                    if (z < zbuffer[x][y]) {
                        zbuffer[x][y] = z;
                        framebuffer.setRGB(x, y, color);
                    }
                }
            }
        }
    }

    double edgeFunction(Vec3 a, Vec3 b, Vec3 c) {
        return ((c.x - a.x) * (b.y - a.y) - (c.y - a.y) * (b.x - a.x));
    }

    Vec3 projectVec3(Vec3 v) {
        Vec3 out = new Vec3();

        out.x = projectX(v.x, v.z);
        out.y = projectY(v.y, v.z);
        out.z = v.z;

        return out;
    }

    private int projectX(double x, double z) {
        return (int) (getWidth()/2 + (x * SCALE) / (z + CAMERA_DIST));
    }
    private int projectY(double y, double z) {
        return (int) (getHeight()/2 - (y * SCALE) / (z + CAMERA_DIST));
    }

    public RendererPanel(int width, int height, Mesh mesh) {
        this.width = width;
        this.height = height;
        this.mesh = mesh;
        this.framebuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.zbuffer = new double[width][height];
        this.SCALE = getScale(mesh);

        Timer timer = new Timer(16, e -> repaint());
        timer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragging = true;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    int dx = e.getX() - lastMouseX;
                    int dy = e.getY() - lastMouseY;

                    double sensitivity = 0.005;

                    angleY += dx * sensitivity;
                    angleX += dy * -sensitivity;

                    lastMouseX = e.getX();
                    lastMouseY = e.getY();

                    repaint();
                }
            }
        });

        addMouseWheelListener(e -> {
            double delta = e.getPreciseWheelRotation();

            CAMERA_DIST += delta * 0.2;

            if (CAMERA_DIST < 1.0) CAMERA_DIST = 1.0;
            if (CAMERA_DIST > 100.0) CAMERA_DIST = 100.0;

            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        clearBuffers();

        Mesh rotatedMesh = mesh.rotate(angleY, angleX);
        for (int i = 0; i < rotatedMesh.triangles.length; i++) {
            Triangle triangle = rotatedMesh.triangles[i];

            Vec3 normal = triangle.getNormal();
            double intensity = normal.x * -lightDir.x +
                    normal.y * -lightDir.y +
                    normal.z * -lightDir.z;
            if (intensity <= 0) continue;

            Color base = Color.BLUE;
            int r = (int)(base.getRed() * intensity);
            int g = (int)(base.getGreen() * intensity);
            int b = (int)(base.getBlue() * intensity);
            Color shadedColor = new Color(r, g, b);

            Vec3 p1 = projectVec3(triangle.v1);
            Vec3 p2 = projectVec3(triangle.v2);
            Vec3 p3 = projectVec3(triangle.v3);


            drawTriangle(p1, p2, p3, shadedColor.getRGB());
        }
        graphics.drawImage(framebuffer, 0, 0, null);
    }

    private double getScale(Mesh mesh) {
        Mesh.BoundingBox boundingBox = mesh.getBoundingBox();

        double sizeX = boundingBox.maxX() - boundingBox.minX();
        double sizeY = boundingBox.maxY() - boundingBox.minY();
        double sizeZ = boundingBox.maxZ() - boundingBox.minZ();

        double maxSize = Math.max(sizeZ, Math.max(sizeX, sizeY));
        double targetSize = 5000.0;
        return targetSize / maxSize;
    }
}