package me.pigalala.oinkscoreboard.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "oinkscoreboard")
public class OinkConfig implements ConfigData {
    @Comment("Sets the max size of the scoreboard. Set to -1 for no limit")
    @ConfigEntry.BoundedDiscrete(min = -1, max = 50)
    public int scoreboardSize = -1;
}
