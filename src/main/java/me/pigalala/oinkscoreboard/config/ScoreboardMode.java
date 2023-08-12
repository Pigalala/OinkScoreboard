package me.pigalala.oinkscoreboard.config;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public enum ScoreboardMode {
    DEFAULT("Default", "The standard implementation of scoreboards"),
    RANGE("Range", "Shows a specified range* of entries from your position. *[MaxRows ÷ 2] §c(ONLY ENABLES WHEN TimingSystem SCOREBOARD IS DETECTED)");

    private final String displayName, toolTip;

    ScoreboardMode(@Nullable String displayName, @Nullable String toolTip) {
        this.displayName = displayName;
        this.toolTip = toolTip;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Text getTitle() {
        return Text.of("Scoreboard Mode");
    }

    public static Text getToolTip() {
        return Text.of("""
                %DEFAULT_DN%: %DEFAULT_TT%.
                %RANGE_DN%: %RANGE_TT%
                """
                .replace("%DEFAULT_DN%", DEFAULT.displayName)
                .replace("%DEFAULT_TT%", DEFAULT.toolTip)
                .replace("%RANGE_DN%", RANGE.displayName)
                .replace("%RANGE_TT%", RANGE.toolTip));
    }

    public static ScoreboardMode of(int ordinal) {
        for(ScoreboardMode val : ScoreboardMode.values()) {
            if(val.ordinal() == ordinal) return val;
        }

        return null;
    }
}
