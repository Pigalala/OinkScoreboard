package me.pigalala.oinkscoreboard.config;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public enum ScoreboardPlacements {
    NORMAL("Default", "The standard location of the scoreboard, centre-right"),
    LOWER_RIGHT("Lower-Right", "Places the scoreboard in the lower right of the screen, the bottom entry is at the bottom right"),
    UPPER_RIGHT("Upper-Right", "Places the scoreboard in the upper right of the screen, the title is at the top left");

    private final String displayName, toolTip;

    ScoreboardPlacements(@Nullable String displayName, @Nullable String toolTip) {
        this.displayName = displayName;
        this.toolTip = toolTip;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Text getTitle() {
        return Text.of("Scoreboard Placement");
    }

    public static Text getToolTip() {
        return Text.of("""
                %NORMAL_DN%: %NORMAL_TT%.
                %LOWER_RIGHT_DN%: %LOWER_RIGHT_TT%
                %UPPER_RIGHT_DN%: %UPPER_RIGHT_TT%
                """
                .replace("%NORMAL_DN%", NORMAL.displayName)
                .replace("%NORMAL_TT%", NORMAL.toolTip)
                .replace("%LOWER_RIGHT_DN%", LOWER_RIGHT.displayName)
                .replace("%LOWER_RIGHT_TT%", LOWER_RIGHT.toolTip)
                .replace("%UPPER_RIGHT_DN%", UPPER_RIGHT.displayName)
                .replace("%UPPER_RIGHT_TT%", UPPER_RIGHT.toolTip));
    }

    public static ScoreboardPlacements of(int ordinal) {
        for(ScoreboardPlacements val : ScoreboardPlacements.values()) {
            if(val.ordinal() == ordinal) return val;
        }

        return null;
    }
}
