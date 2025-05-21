package org.example.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileUtil {
    private static final String DATA_DIR;
    
    static {
        try {
            // Get the absolute path to your CarManagement project
            String projectPath = "C:\\Users\\MSI\\OneDrive - Sri Lanka Institute of Information Technology\\Desktop\\CarManagement";
            DATA_DIR = projectPath + File.separator + "data";
            
            System.out.println("FileUtil: Project directory is: " + projectPath);
            System.out.println("FileUtil: Attempting to create/access data directory at: " + DATA_DIR);
            
            // Create data directory if it doesn't exist
            File directory = new File(DATA_DIR);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    System.out.println("FileUtil: Successfully created data directory at: " + directory.getAbsolutePath());
                } else {
                    System.err.println("FileUtil: Failed to create data directory at: " + directory.getAbsolutePath());
                    File parent = directory.getParentFile();
                    System.err.println("FileUtil: Parent directory exists: " + (parent != null && parent.exists()));
                    System.err.println("FileUtil: Parent directory writable: " + (parent != null && parent.canWrite()));
                }
            } else {
                System.out.println("FileUtil: Using existing data directory at: " + directory.getAbsolutePath());
            }
            
            // Verify the directory is usable
            if (!directory.canWrite()) {
                System.err.println("FileUtil: WARNING - Data directory is not writable!");
            }
        } catch (Exception e) {
            System.err.println("FileUtil: Error during initialization:");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize FileUtil", e);
        }
    }

    public static void writeLines(String filename, List<String> lines) {
        File file = new File(DATA_DIR, filename);
        try {
            // Ensure the data directory exists before writing
            File directory = file.getParentFile();
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                System.out.println("FileUtil: Created directory structure: " + created);
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("FileUtil: Successfully wrote " + lines.size() + " lines to: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("FileUtil: Error writing to file: " + file.getAbsolutePath());
            System.err.println("FileUtil: File exists: " + file.exists());
            System.err.println("FileUtil: File can write: " + file.canWrite());
            e.printStackTrace();
        }
    }

    public static List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(DATA_DIR, filename);
        
        if (!file.exists()) {
            System.out.println("FileUtil: File does not exist, returning empty list: " + file.getAbsolutePath());
            return lines;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                lineCount++;
            }
            System.out.println("FileUtil: Successfully read " + lineCount + " lines from: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("FileUtil: Error reading from file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
        return lines;
    }

    public static String getDataDirectory() {
        return DATA_DIR;
    }
    
    // Helper method to check if data directory exists and is writable
    public static boolean checkDataDirectory() {
        File directory = new File(DATA_DIR);
        boolean exists = directory.exists();
        boolean isDirectory = directory.isDirectory();
        boolean canWrite = directory.canWrite();
        
        System.out.println("FileUtil: Data Directory Status:");
        System.out.println("- Path: " + directory.getAbsolutePath());
        System.out.println("- Exists: " + exists);
        System.out.println("- Is Directory: " + isDirectory);
        System.out.println("- Can Write: " + canWrite);
        
        return exists && isDirectory && canWrite;
    }
} 