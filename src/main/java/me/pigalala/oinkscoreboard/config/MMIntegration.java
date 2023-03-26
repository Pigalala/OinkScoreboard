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
            b.getOrCreateCategory(Text.of("Cat"))
                    .addEntry(entryBuilder.startIntSlider(Text.of("Max Rows"), OinkConfig.maxRows, 1, 99)
                            .setDefaultValue(50)
                            .setSaveConsumer(newVal -> OinkConfig.maxRows = newVal)
                            .build())
                    .addEntry(entryBuilder.startAlphaColorField(Text.of("Scoreboard Colour"), OinkConfig.scoreboardColour)
                            .setDefaultValue(0x52FFFFFF)
                            .setSaveConsumer(newVal -> OinkConfig.scoreboardColour = newVal)
                            .build())
                    .addEntry(entryBuilder.startEnumSelector(Text.of("Scoreboard Placement"), ScoreboardPlacements.class, OinkConfig.scoreboardPlacement)
                            .setDefaultValue(ScoreboardPlacements.NORMAL)
                            .setSaveConsumer(newVal -> OinkConfig.scoreboardPlacement = newVal)
                            .build());

            b.setSavingRunnable(OinkConfig::save);
            return b.build();
        };
    }
}
