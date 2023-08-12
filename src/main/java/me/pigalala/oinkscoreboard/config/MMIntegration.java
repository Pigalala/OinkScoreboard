package me.pigalala.oinkscoreboard.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class MMIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder b = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("OinkScoreboard"));

            ConfigEntryBuilder entryBuilder = b.entryBuilder();
            b.getOrCreateCategory(CATEGORY)
                    .addEntry(entryBuilder.startAlphaColorField(SCOREBOARD_COLOUR, OinkConfig.scoreboardColour)
                            .setDefaultValue(0x4c000000)
                            .setSaveConsumer(newVal -> OinkConfig.scoreboardColour = newVal)
                            .setTooltip(SCOREBOARD_COLOUR_TOOLTIP)
                            .build())
                    .addEntry(entryBuilder.startIntSlider(MAX_ROWS, OinkConfig.maxRows, 0, 200)
                            .setDefaultValue(30)
                            .setSaveConsumer(newVal -> OinkConfig.maxRows = newVal)
                            .setTooltip(MAX_ROWS_TOOLTIP)
                            .build())
                    .addEntry(entryBuilder.startEnumSelector(ScoreboardPlacements.getTitle(), ScoreboardPlacements.class, OinkConfig.scoreboardPlacement)
                            .setDefaultValue(ScoreboardPlacements.NORMAL)
                            .setSaveConsumer(newVal -> OinkConfig.scoreboardPlacement = newVal)
                            .setEnumNameProvider(val -> Text.of(ScoreboardPlacements.of(val.ordinal()).getDisplayName()))
                            .setTooltip(ScoreboardPlacements.getToolTip())
                            .build())
                    .addEntry(entryBuilder.startEnumSelector(ScoreboardMode.getTitle(), ScoreboardMode.class, OinkConfig.scoreboardMode)
                            .setDefaultValue(ScoreboardMode.DEFAULT)
                            .setSaveConsumer(newVal -> OinkConfig.scoreboardMode = newVal)
                            .setEnumNameProvider(val -> Text.of(ScoreboardMode.of(val.ordinal()).getDisplayName()))
                            .setTooltip(ScoreboardMode.getToolTip())
                            .build());

            b.setSavingRunnable(OinkConfig::save);
            return b.build();
        };
    }

    private static final Text
    CATEGORY = Text.of("OinkScoreboard"),
    //
    SCOREBOARD_COLOUR = Text.of("Scoreboard Colour"),
    SCOREBOARD_COLOUR_TOOLTIP = Text.of("Set the hex colour of the scoreboard using ARGB"),
    //
    MAX_ROWS = Text.of("Max Rows"),
    MAX_ROWS_TOOLTIP = Text.of("Set the maximum amount of rows shown on the scoreboard || This value divided by 2 is used by RANGE mode");
}
