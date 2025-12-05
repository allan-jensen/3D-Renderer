package com.renderer_3d;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileImporter {
    public static Mesh getMeshFromFile() {
        String content = readFile();
        if (content == null || content.isEmpty()) {
            return new Mesh(null);
        }
        Mesh mesh = parseFile(content);
        mesh.centralize();
        return mesh;
    }

    private static Mesh parseFile(String content) {
        List<String> lines = content.lines().toList();
        List<String> verticesLines = lines.stream().filter(line -> line.startsWith("v ")).toList();
        List<String> facesLines = lines.stream().filter(line -> line.startsWith("f ")).toList();

        List<Vec3> vertices = verticesLines.stream().map(line -> {
            String[] parts = line.split(" ");
            List<String> list = Arrays.stream(parts).filter(i -> !i.isEmpty() && !i.equals(" ")).toList();
            float x = Float.parseFloat(list.get(1));
            float y = Float.parseFloat(list.get(2));
            float z = Float.parseFloat(list.get(3));
            return new Vec3(x, y, z);
        }).toList();

        List<Triangle> faces = new ArrayList<>();
        for (String line : facesLines) {
            String[] parts = Arrays.stream(line.split(" ")) //
                    .filter(i -> !i.isEmpty() && !i.equals(" ") && !i.equals("v")).toArray(String[]::new);

            Vec3 v1 = vertices.get(Integer.parseInt(parts[1].split("/")[0]) - 1);
            Vec3 v2 = vertices.get(Integer.parseInt(parts[2].split("/")[0]) - 1);
            Vec3 v3 = vertices.get(Integer.parseInt(parts[3].split("/")[0]) - 1);
            faces.add(new Triangle(v1, v3, v2));

            if (parts.length > 4) {
                for (int i = 3; i < parts.length - 1; i++) {
                    v2 = vertices.get(Integer.parseInt(parts[i].split("/")[0]) - 1);
                    v3 = vertices.get(Integer.parseInt(parts[i + 1].split("/")[0]) - 1);
                    faces.add(new Triangle(v1, v3, v2));
                }
            }
        }

        return new Mesh(faces.toArray(new Triangle[0]));
    }

    private static String readFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecione um arquivo");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos OBJ", "obj"));

        String userHome = System.getProperty("user.home");
        java.io.File downloads = new java.io.File(userHome, "Downloads");
        if (!downloads.exists() || !downloads.isDirectory()) {
            downloads = new java.io.File(userHome);
        }
        chooser.setCurrentDirectory(downloads);

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();

            try {
                return Files.readString(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
