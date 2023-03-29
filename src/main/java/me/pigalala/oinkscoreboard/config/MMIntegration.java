package me.pigalala.oinkscoreboard.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.pigalala.oinkscoreboard.ScoreboardPlacements;
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
                    .addEntry(entryBuilder.startBooleanToggle(SHOW_MAX_ROWS, OinkConfig.showMaxRows)
                            .setDefaultValue(true)
                            .setSaveConsumer(newVal -> OinkConfig.showMaxRows = newVal)
                            .build())
                    .addEntry(entryBuilder.startAlphaColorField(SCOREBOARD_COLOUR, OinkConfig.scoreboardColour)
                            .setDefaultValue(0x52FFFFFF)
                            .setSaveConsumer(newVal -> OinkConfig.scoreboardColour = newVal)
                            .build())
                    .addEntry(entryBuilder.startEnumSelector(SCOREBOARD_PLACEMENT, ScoreboardPlacements.class, OinkConfig.scoreboardPlacement)
                            .setDefaultValue(ScoreboardPlacements.NORMAL)
                            .setSaveConsumer(newVal -> OinkConfig.scoreboardPlacement = newVal)
                            .build());

            b.setSavingRunnable(OinkConfig::save);
            return b.build();
        };
    }

    private static final Text
    CATEGORY = Text.of("Cat"),
    SCOREBOARD_COLOUR = Text.of("Scoreboard Colour"),
    SCOREBOARD_PLACEMENT = Text.of("Scoreboard Placement"),
    SHOW_MAX_ROWS = Text.of("Show Max Row Text");
}
