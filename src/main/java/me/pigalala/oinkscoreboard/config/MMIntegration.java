package me.pigalala.oinkscoreboard.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.pigalala.oinkscoreboard.OinkScoreboard;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public final class MMIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            OinkConfig config = OinkScoreboard.config;
            ConfigBuilder b = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("OinkScoreboard"));

            ConfigEntryBuilder entryBuilder = b.entryBuilder();
            b.getOrCreateCategory(CATEGORY)
                    .addEntry(entryBuilder.startAlphaColorField(SCOREBOARD_COLOUR, config.scoreboardColor)
                            .setDefaultValue(0x4c000000)
                            .setSaveConsumer(newVal -> config.scoreboardColor = newVal)
                            .setTooltip(SCOREBOARD_COLOUR_TOOLTIP)
                            .build())
                    .addEntry(entryBuilder.startIntField(MAX_ROWS, config.maxRows)
                            .setDefaultValue(15)
                            .setSaveConsumer(newVal -> config.maxRows = newVal)
                            .setTooltip(MAX_ROWS_TOOLTIP)
                            .build())
                    .addEntry(entryBuilder.startEnumSelector(SCOREBOARD_PLACEMENT, ScoreboardPlacements.class, config.scoreboardPlacement())
                            .setDefaultValue(ScoreboardPlacements.NORMAL)
                            .setSaveConsumer(config::scoreboardPlacement)
                            .setEnumNameProvider(val -> ScoreboardPlacements.values()[val.ordinal()].getDisplayText())
                            .setTooltip(SCOREBOARD_PLACEMENT_TOOLTIP)
                            .build());

            b.setSavingRunnable(config::save);
            return b.build();
        };
    }

    private final Text
    CATEGORY = Text.literal("OinkScoreboard"),
    //
    SCOREBOARD_COLOUR = Text.literal("Scoreboard Colour"),
    SCOREBOARD_COLOUR_TOOLTIP = Text.literal("Set the hex colour of the scoreboard using AARRGGBB"),
    //
    MAX_ROWS = Text.literal("Max Rows"),
    MAX_ROWS_TOOLTIP = Text.literal("Set the maximum amount of rows shown on the scoreboard"),
    //
    SCOREBOARD_PLACEMENT = Text.literal("Scoreboard Placement"),
    SCOREBOARD_PLACEMENT_TOOLTIP = Text.literal("The location of the scoreboard on the screen.")
    ;
}
