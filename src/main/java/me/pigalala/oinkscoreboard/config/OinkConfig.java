package me.pigalala.oinkscoreboard.config;

import me.pigalala.oinkscoreboard.ScoreboardPlacements;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class OinkConfig {

    public static int scoreboardColour = 0x52FFFFFF;
    public static int maxRows = 50;
    public static ScoreboardPlacements scoreboardPlacement = ScoreboardPlacements.NORMAL;

    private static File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "oinkscoreboard.properties");

    public static void load() {
        try {
            if(configFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(configFile));
                String line = br.readLine();
                do {
                    if(line.startsWith("scolour "))
                        scoreboardColour = Integer.parseInt(line.substring(8));
                    else if(line.startsWith("maxrows "))
                        maxRows = Integer.parseInt(line.substring(8));
                    else if(line.startsWith("placement "))
                        scoreboardPlacement = ScoreboardPlacements.valueOf(line.substring(10));
                    line = br.readLine();
                } while (line != null);
                br.close();
            }
        }
        catch (Exception e) {
        }
    }

    public static void save() {
        try {
            FileWriter writer = new FileWriter(configFile);
            writer.write("scolour " + scoreboardColour + "\n");
            writer.write("maxrows " + maxRows + "\n");
            writer.write("placement " + scoreboardPlacement.toString() + "\n");
            writer.close();
        }
        catch (Exception e) {
        }
    }
}
