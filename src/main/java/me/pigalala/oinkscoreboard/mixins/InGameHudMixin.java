package me.pigalala.oinkscoreboard.mixins;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.pigalala.oinkscoreboard.ScoreboardPlacements;
import me.pigalala.oinkscoreboard.config.OinkConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(
            method = "renderScoreboardSidebar",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> playerRows = scoreboard.getAllPlayerScores(objective).stream().filter((score) -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList());

        if (playerRows.size() > OinkConfig.maxRows) playerRows = Lists.newArrayList(Iterables.skip(playerRows, playerRows.size() - OinkConfig.maxRows));

        List<Pair<ScoreboardPlayerScore, Text>> rowNamePair = Lists.newArrayListWithCapacity(playerRows.size());
        Text scoreboardTitle = objective.getDisplayName();
        TextRenderer tr = this.getTextRenderer();
        int textWidth = tr.getWidth(scoreboardTitle);
        int textWidth2 = textWidth;
        int colonSpaceWidth = tr.getWidth(": ");

        ScoreboardPlayerScore scoreboardPlayerScore;
        MutableText text2;
        for(Iterator<ScoreboardPlayerScore> scoreboardRows = playerRows.iterator(); scoreboardRows.hasNext(); textWidth = Math.max(textWidth, tr.getWidth(text2) + colonSpaceWidth + this.getTextRenderer().getWidth(Integer.toString(scoreboardPlayerScore.getScore())))) {
            scoreboardPlayerScore = scoreboardRows.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            text2 = Team.decorateName(team, Text.literal(scoreboardPlayerScore.getPlayerName()));
            rowNamePair.add(Pair.of(scoreboardPlayerScore, text2));
        }

        int m;
        if(OinkConfig.scoreboardPlacement == ScoreboardPlacements.NORMAL) m = this.scaledHeight / 2 + (playerRows.size() * 9) / 2; // Normal placement
        else if(OinkConfig.scoreboardPlacement == ScoreboardPlacements.LOWER_RIGHT) m = this.scaledHeight; // Lower Right placement
        else m = (playerRows.size() + 1) * 9; // Upper Right placement

        int x = this.scaledWidth - textWidth - 3;
        int xOffset = x - 2;

        int backgroundColorLight = OinkConfig.scoreboardColour;
        int backgroundColorDark = OinkConfig.scoreboardColour + 0x1A000000;

        int inc = 0;
        for (Pair<ScoreboardPlayerScore, Text> pair : rowNamePair) {
            ++inc;
            int currentY = m - inc * 9;

            context.fill(xOffset, currentY, this.scaledWidth, currentY + 9, backgroundColorLight);
            context.drawText(tr, pair.getSecond(), x, currentY, -1, false);

            if (inc == playerRows.size()) {
                context.fill(xOffset, currentY - 9 - 1, this.scaledWidth, currentY - 1, backgroundColorDark);
                context.fill(xOffset, currentY - 1, this.scaledWidth, currentY, backgroundColorLight);
                context.drawText(tr, scoreboardTitle, (x + textWidth / 2 - textWidth2 / 2), currentY - 9, -1, false);
            }
        }
        ci.cancel();
    }
}
