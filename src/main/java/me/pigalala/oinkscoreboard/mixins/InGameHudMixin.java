package me.pigalala.oinkscoreboard.mixins;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import com.mojang.datafixers.util.Pair;
import me.pigalala.oinkscoreboard.config.ScoreboardPlacements;
import me.pigalala.oinkscoreboard.config.OinkConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
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
import java.util.Comparator;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        int scaledWidth = context.getScaledWindowWidth();
        int scaledHeight = context.getScaledWindowHeight();

        if(OinkConfig.maxRows == 0) {
            ci.cancel();
            return;
        }
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardEntry> playerRows = scoreboard.getScoreboardEntries(objective).stream().filter((score) -> score.owner() != null && !score.owner().startsWith("#")).sorted(Comparator.comparingInt(ScoreboardEntry::value)).collect(Collectors.toList());;

        if(playerRows.size() > OinkConfig.maxRows) playerRows = Lists.newArrayList(Iterables.skip(playerRows, playerRows.size() - OinkConfig.maxRows));

        List<Pair<ScoreboardEntry, Text>> rowNamePair = Lists.newArrayListWithCapacity(playerRows.size());
        Text scoreboardTitle = objective.getDisplayName();
        TextRenderer tr = this.getTextRenderer();
        int textWidth = tr.getWidth(scoreboardTitle);
        int textWidth2 = textWidth;
        int colonSpaceWidth = tr.getWidth(": ");

        ScoreboardEntry scoreboardPlayerScore;
        MutableText text2;
        for(Iterator<ScoreboardEntry> scoreboardRows = playerRows.iterator(); scoreboardRows.hasNext(); textWidth = Math.max(textWidth, tr.getWidth(text2) + colonSpaceWidth)) {
            scoreboardPlayerScore = scoreboardRows.next();
            Team team = scoreboard.getScoreHolderTeam(scoreboardPlayerScore.owner());
            text2 = Team.decorateName(team, Text.literal(scoreboardPlayerScore.owner()));
            rowNamePair.add(Pair.of(scoreboardPlayerScore, text2));
        }

        int m;
        if(OinkConfig.scoreboardPlacement == ScoreboardPlacements.NORMAL) m = scaledHeight / 2 + (playerRows.size() * 9) / 2; // Normal placement
        else if(OinkConfig.scoreboardPlacement == ScoreboardPlacements.LOWER_RIGHT) m = scaledHeight; // Lower Right placement
        else m = (playerRows.size() + 1) * 9; // Upper Right placement

        int x = scaledWidth - textWidth - 3;
        int xOffset = x - 2;

        int backgroundColorLight = OinkConfig.scoreboardColour;
        int backgroundColorDark = OinkConfig.scoreboardColour + 0x1A000000;

        int inc = 0;
        for (Pair<ScoreboardEntry, Text> pair : rowNamePair) {
            ++inc;
            int currentY = m - inc * 9;
            context.fill(xOffset, currentY, scaledWidth, currentY + 9, backgroundColorLight);
            context.drawText(tr, pair.getSecond(), x, currentY, -1, false);

            if (inc == playerRows.size()) {
                context.fill(xOffset, currentY - 9 - 1, scaledWidth, currentY - 1, backgroundColorDark);
                context.fill(xOffset, currentY - 1, scaledWidth, currentY, backgroundColorLight);
                context.drawText(tr, scoreboardTitle, (x + textWidth / 2 - textWidth2 / 2), currentY - 9, -1, false);
            }
        }

        ci.cancel();
    }
}
