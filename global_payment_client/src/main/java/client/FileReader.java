//package main.java.client;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
///**
// * Class for reading binary files
// */
//public class FileReader {
//
//    private final String filePath;
//    private final Path path;
//
//    public FileReader(String filePath) throws IllegalAccessException {
//        if(filePath == null || filePath.isEmpty()) {
//            throw new IllegalAccessException("Inavlid file path provided");
//        }
//
//        if(!(new File(filePath).exists())) {
//            throw new IllegalAccessException("File provided by path does not exist");
//        }
//
//        this.filePath = filePath;
//        this.path = Paths.get(filePath);
//    }
//
//    public byte[] readContent() throws IOException {
//        return Files.readAllBytes(this.path);
//    }
//
//}
