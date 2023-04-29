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
                    .addEntry(entryBuilder.startAlphaColorField(SCOREBOARD_COLOUR, OinkConfig.scoreboardColour)
                            .setDefaultValue(0x4c000000)
                            .setSaveConsumer(newVal -> OinkConfig.scoreboardColour = newVal)
                            .setTooltip(SCOREBOARD_COLOUR_TOOLTIP)
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
    SCOREBOARD_COLOUR_TOOLTIP = Text.of("Set the hex colour of the scoreboard in the form #AARRGGBB"),
    SCOREBOARD_PLACEMENT = Text.of("Scoreboard Placement");
}
