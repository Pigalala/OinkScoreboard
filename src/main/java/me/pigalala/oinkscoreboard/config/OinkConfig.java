package me.pigalala.oinkscoreboard.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OinkConfig {

    private final Path configFilePath = FabricLoader.getInstance().getConfigDir().resolve("oinkscoreboard.json");

    @SaveMePlease("max_rows")
    public int maxRows = 15;

    @SaveMePlease("scoreboard_color")
    public int scoreboardColor = 0x4c000000;

    @SaveMePlease("scoreboard_placement")
    public int scoreboardPlacementOrdinal = ScoreboardPlacements.NORMAL.ordinal();

    @SaveMePlease("enabled")
    public boolean enabled = true;

    public OinkConfig() {
        // Create file with default values if it doesn't exist
        if (!Files.exists(configFilePath)) {
            save();
        }

        // Read json file
        JsonObject configJson;
        try (BufferedReader reader = Files.newBufferedReader(configFilePath)) {
            configJson = (JsonObject) JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Field field : getClass().getFields()) {
            if (!field.isAnnotationPresent(SaveMePlease.class)) {
                continue;
            }

            String saveAsName = field.getAnnotation(SaveMePlease.class).value();
            try {
                // Set values somehow
                Field valueField = JsonPrimitive.class.getDeclaredField("value"); // Pray
                valueField.setAccessible(true);
                Object value;

                if (configJson.get(saveAsName) instanceof JsonPrimitive primitive) {
                    value = valueField.get(primitive);
                    if (value instanceof Number n) {
                        value = n.intValue();
                    }
                } else {
                    value = null;
                }

                field.set(this, value);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject result = new JsonObject();

        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(SaveMePlease.class)) {
                continue;
            }

            String saveAsName = field.getAnnotation(SaveMePlease.class).value();
            Object value;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                result.add(saveAsName, JsonNull.INSTANCE);
                return;
            }

            switch (value) {
                case String s -> result.addProperty(saveAsName, s);
                case Number n -> result.addProperty(saveAsName, n);
                case Boolean b -> result.addProperty(saveAsName, b);
                case Character c -> result.addProperty(saveAsName, c);
                default -> result.add(saveAsName, JsonNull.INSTANCE);
            }
        }

        try {
            if (!Files.exists(configFilePath)) {
                Files.createFile(configFilePath);
            }
            Files.write(configFilePath, gson.toJson(result).getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save OinkScoreboard config", e);
        }
    }

    public ScoreboardPlacements scoreboardPlacement() {
        return ScoreboardPlacements.values()[scoreboardPlacementOrdinal];
    }

    public void scoreboardPlacement(ScoreboardPlacements scoreboardPlacements) {
        scoreboardPlacementOrdinal = scoreboardPlacements.ordinal();
    }
}
