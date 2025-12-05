package com.renderer_3d;

import javax.swing.*;

public class Main {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    public static void main(String[] args) {
        Mesh mesh = FileImporter.getMeshFromFile();
        JFrame frame = new JFrame("3D Renderer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        RendererPanel rendererPanel = new RendererPanel(WIDTH, HEIGHT, mesh);
        frame.add(rendererPanel);

        frame.setVisible(true);
    }
}