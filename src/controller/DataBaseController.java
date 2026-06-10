package controller;

import model.GameRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    private static final String FILE_PATH = "records.txt";

    public void saveRecord(GameRecord record) {
        try {
            FileWriter   fw = new FileWriter(FILE_PATH, true);
            BufferedWriter bw = new BufferedWriter(fw);
            String line = record.getPlayer1Name() + ","
                    + record.getPlayer2Name() + ","
                    + record.getWinnerName()  + ","
                    + record.getDate();
            bw.write(line);
            bw.newLine();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GameRecord> loadRecords() {
        List<GameRecord> records = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return records;
        }
        try {
            FileReader   fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    records.add(new GameRecord(parts[0], parts[1], parts[2], parts[3]));
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}