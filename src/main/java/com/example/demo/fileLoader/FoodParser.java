package com.example.demo.fileLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class FoodParser {
    public void parseFoodFile() {
        Path filePath = Path.of("src/main/resources/mockdata/food.txt");

        System.out.println("================================");
        System.out.println("FoodParser.parseFoodFile()...");
        System.out.println("================================");


        try {
            ClassPathResource resource = new ClassPathResource("mockdata/food.txt");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream())
                );

            reader.lines()
                .skip(1) // Skip header
                .map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                .forEach(parts -> {
                    String category = parts[0];
                    String item = parts[1].replace("\"", "");
                    String fdcId = parts[2];
                    System.out.println("================================");
                    System.out.println("Category: " + category + " | FDC ID: " + fdcId + " | Item: " + item);
                    System.out.println("================================");
                });

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
