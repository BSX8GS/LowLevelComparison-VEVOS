package at.jku.simplelinedifference;

import javafx.fxml.FXML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Compare {
    //This class supposed to have a rough comparison between the presence conditions of the previous and current commit
    //Based on the difference between the start and end lines taking the file names into consideration.

    //NOTE: This is not a code-to-code comparison, not an exhaustive test

    private List<String> before_BlockCondition;

    private Path previous;
    private Path current;
    private List<String> previousFile;
    private List<String> currentFile;

    private Path changedPath;
    private Path notFoundPath;

    public Compare(Path prev, Path curr, Path output) throws IOException {
        this.previous = prev;
        this.current = curr;
        createFile(output);
        readFiles();
    }

    private void readFiles() throws IOException {
        previousFile = Files.readAllLines(previous);
        if(previousFile.size()>0)
            previousFile.remove(0);//Removing header
        currentFile = Files.readAllLines(current);
        if(currentFile.size()>0)
            currentFile.remove(0);//removing header
    }

    public void compare() throws IOException {

        // Key → list of block lengths
        Map<String, List<Integer>> previousMap = buildMap(previousFile);
        Map<String, List<Integer>> currentMap  = buildMap(currentFile);

        Set<String> selectedBC = new HashSet<>();

        for (String key : previousMap.keySet()) {

            if (!currentMap.containsKey(key)) continue;

            List<Integer> prevLengths = previousMap.get(key);
            List<Integer> currLengths = currentMap.get(key);

            // Sort so we can compare consistently
            Collections.sort(prevLengths);
            Collections.sort(currLengths);

            if (!prevLengths.equals(currLengths)) {
                // extract block condition from key
                String[] parts = key.split("\\|");
                String bc = parts[1];

                selectedBC.add(bc);
            }
        }

        for (String bc : selectedBC) {
            appendFile(String.format("%s\n", bc), changedPath);
        }
    }

    private Map<String, List<Integer>> buildMap(List<String> file) {

        Map<String, List<Integer>> map = new HashMap<>();

        for (String line : file) {

            String[] split = line.split(";");

            if (split.length < 7) continue;

            String fileName = split[0].trim();
            String bc = split[2].trim();
            String pc = split[3].trim();

            int start = Integer.parseInt(split[5].trim());
            int end   = Integer.parseInt(split[6].trim());

            int length = end - start;

            if(fileName.endsWith(".c") || fileName.endsWith(".h")) {
                String key = fileName + "|" + bc + "|" + pc;

                map.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(length);
            }
        }

        return map;
    }

    public void getNewList() {
        getBeforeFeatureList();
        List<String> newList = new ArrayList<>();
        Iterator<String> iterator = currentFile.iterator();
        while(iterator.hasNext()) {
            String line = iterator.next();
            String splitLine[] = line.split(";");

            String fileName = splitLine[0];
            String bc = splitLine[2].trim();
            if(!before_BlockCondition.contains(bc) && !newList.contains(bc) &&
                    (fileName.endsWith(".c") || fileName.endsWith(".h"))) {
                newList.add(bc);
            }
        }

        for(String elem: newList) {
            appendFile(String.format("%s\n", elem), notFoundPath);
        }
    }

    private void getBeforeFeatureList() {
        this.before_BlockCondition = new ArrayList<>();
        Iterator<String> iterator = previousFile.iterator();
        while(iterator.hasNext()) {
            String line = iterator.next();
            String splitLine[] = line.split(";");//Line of the current file
            if(!before_BlockCondition.contains(splitLine[2].trim())){
                before_BlockCondition.add(splitLine[2].trim());
            }
        }
    }

    private void createFile(Path path) {
        try {
            if(path.toString().length() > 0) {
                Path outPath = Path.of(path.toString(), "\\changedFeatures.csv");
                this.changedPath = outPath;
                Path outPathb = Path.of(path.toString(), "\\notFoundFeatures.csv");
                this.notFoundPath = outPathb;
                if (Files.notExists(outPath.getParent())) {
                    Files.createDirectories(outPath.getParent());
                }
                if (Files.notExists(outPathb.getParent())) {
                    Files.createDirectories(outPathb.getParent());
                }
                if (Files.notExists(outPath)) {
                    Files.createFile(outPath);
                }
                if (Files.notExists(outPathb)) {
                    Files.createFile(outPathb);
                }
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void appendFile(String result, Path path) {
        try {
            // Ensure file exists before writing
            if (Files.notExists(path)) {
                Files.createFile(path);
            }

            Files.writeString(
                    path,
                    result,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            System.out.println("Error while writing to file: " + e.getMessage());
        }
    }

}
