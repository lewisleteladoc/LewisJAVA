package com.example.demo.fileLoader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component  // ← registers it with Spring
public class StartupFileLoaderService implements CommandLineRunner {

    private final FoodParser foodParser;
    public StartupFileLoaderService(FoodParser foodParser) {
        this.foodParser = foodParser;
    }

    @Override
    public void run(String... args) throws Exception {
        // This runs automatically when the app starts
        System.out.println("================================");
        System.out.println("================================");
        System.out.println("Startup File Loader Service: Loading files...");
        System.out.println("================================");
        System.out.println("================================");
        loadFiles();
    }

    private void loadFiles() {
        // your file loading logic here
        System.out.println("================================");
        System.out.println("calling food parser...");
        foodParser.parseFoodFile();
        System.out.println("================================");
    }
}