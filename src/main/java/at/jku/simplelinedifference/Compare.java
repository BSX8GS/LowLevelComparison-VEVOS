package at.jku.simplelinedifference;

import javafx.fxml.FXML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        previousFile.remove(0);//Removing header
        currentFile = Files.readAllLines(current);
        currentFile.remove(0);//removing header
    }

    public void compare() throws IOException {
        List<String> selectedBC = new ArrayList<>();
        Iterator<String> iterator = previousFile.iterator();
        while(iterator.hasNext()) {
            String line = iterator.next();
            String splitLine[] = line.split(";");//Line of the previous file
            String currFile = splitLine[0];
            String currBlockCondition = splitLine[2];
            String currPresenceCondition = splitLine[3];
            int currStartLine = Integer.parseInt(splitLine[5]);
            int currEndLine = Integer.parseInt(splitLine[6]);

            Iterator<String> iteratorPrev = currentFile.iterator();
            while (iteratorPrev.hasNext()) {
                String prevLine = iteratorPrev.next();
                String prevSplitLine[] = prevLine.split(";");
                String prevFile = prevSplitLine[0];
                String prevBlockCondition = prevSplitLine[2];
                String prevPresenceCondition = prevSplitLine[3];
                int prevStartLine = Integer.parseInt(prevSplitLine[5]);
                int prevEndLine = Integer.parseInt(prevSplitLine[6]);

                if(   currFile.equals(prevFile)
                   && currBlockCondition.equals(prevBlockCondition)
                   && currPresenceCondition.equals(prevPresenceCondition)) {
                    if((currEndLine - currStartLine) != (prevEndLine - prevStartLine)){
                        //This indicates that there was a change in the feature itself
                        if(!selectedBC.contains(currBlockCondition)) {
                            selectedBC.add(currBlockCondition);
                        }
                    }
                }

            }
        }
        for(String bc: selectedBC) {
            appendFile(String.format("%s\n", bc), changedPath);
        }
    }

    public void getNewList() {
        getBeforeFeatureList();
        List<String> newList = new ArrayList<>();
        Iterator<String> iterator = currentFile.iterator();
        while(iterator.hasNext()) {
            String line = iterator.next();
            String splitLine[] = line.split(";");
            String bc = splitLine[2];
            if(!before_BlockCondition.contains(bc) && !newList.contains(bc)) {
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
            if(!before_BlockCondition.contains(splitLine[2])){
                before_BlockCondition.add(splitLine[2]);
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
