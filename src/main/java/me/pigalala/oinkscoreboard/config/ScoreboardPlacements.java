package me.pigalala.oinkscoreboard.config;

import net.minecraft.text.Text;

public enum ScoreboardPlacements {
    NORMAL("Default"),
    LOWER_RIGHT("Lower-Right"),
    UPPER_RIGHT("Upper-Right");

    private final String displayName;

    ScoreboardPlacements(String displayName) {
        this.displayName = displayName;
    }

    public Text getDisplayText() {
        return Text.literal(displayName);
    }
}
