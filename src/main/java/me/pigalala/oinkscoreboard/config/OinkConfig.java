package me.pigalala.oinkscoreboard.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class OinkConfig {

    public static int maxRows = 30;
    public static int scoreboardColour = 0x4c000000;
    public static ScoreboardPlacements scoreboardPlacement = ScoreboardPlacements.NORMAL;
    public static boolean enabled = true;

    private static File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "oinkscoreboard.properties");

    public static void load() {
        try {
            if(configFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(configFile));
                String line = br.readLine();
                do {
                    if(line.startsWith("maxrows "))
                        maxRows = Integer.parseInt(line.substring(8));
                    else if(line.startsWith("scolour "))
                        scoreboardColour = Integer.parseInt(line.substring(8));
                    else if(line.startsWith("placement "))
                        scoreboardPlacement = ScoreboardPlacements.valueOf(line.substring(10));
                    else if(line.startsWith("enabled "))
                        enabled = Boolean.parseBoolean(line.substring(8));
                    line = br.readLine();
                } while (line != null);
                br.close();
            }
        }
        catch (Exception e) {
        }

        maxRows = Math.min(Math.max(0, maxRows), 125);
    }

    public static void save() {
        try {
            FileWriter writer = new FileWriter(configFile);
            writer.write("maxrows " + maxRows + "\n");
            writer.write("scolour " + scoreboardColour + "\n");
            writer.write("placement " + scoreboardPlacement.toString() + "\n");
            writer.write("enabled " + enabled + "\n");
            writer.close();
        }
        catch (Exception e) {
            System.out.println("There was an error saving the OinkScoreboard config");
        }
    }
}
