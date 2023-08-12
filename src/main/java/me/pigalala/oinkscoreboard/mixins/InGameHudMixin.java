package me.pigalala.oinkscoreboard.mixins;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.pigalala.oinkscoreboard.config.ScoreboardMode;
import me.pigalala.oinkscoreboard.config.ScoreboardPlacements;
import me.pigalala.oinkscoreboard.config.OinkConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "renderScoreboardSidebar",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> playerRows = scoreboard.getAllPlayerScores(objective).stream().filter((score) -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList());

        if(playerRows.size() > OinkConfig.maxRows && OinkConfig.scoreboardMode == ScoreboardMode.DEFAULT) playerRows = Lists.newArrayList(Iterables.skip(playerRows, playerRows.size() - OinkConfig.maxRows));

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

        // Range mode stuff
        boolean rangeModeFailed = !objective.getDisplayName().getString().matches("R\\d+F\\d+ \\| .+");
        List<Text> rows = new ArrayList<>();
        int min = 0, max = 0;

        for (Pair<ScoreboardPlayerScore, Text> pair : rowNamePair) {
            ++inc;
            int currentY = m - inc * 9;

            if(OinkConfig.scoreboardMode == ScoreboardMode.RANGE && !rangeModeFailed) { // RANGE MODE
                rows.add(pair.getSecond());

                if(pair.getSecond().getString().replaceAll("\\d[\\d ]+\\|...........\\|\\|§.§. ", "").replaceAll(" +Pits: \\d+", "").equals(client.player.getDisplayName().getString()) || pair.getSecond().getString().replaceAll("\\d[\\d ]...........\\|\\| ", "").replaceAll(" \\d+§.§.", "").equals(client.player.getDisplayName().asTruncatedString(4))) {
                    Text playerRow = pair.getSecond();
                    int playerPos = rows.indexOf(playerRow) + 1;
                    int aboveBelow = OinkConfig.maxRows % 2 == 0 ? OinkConfig.maxRows / 2 : (OinkConfig.maxRows + 1) / 2;
                    min = Math.max(1, playerPos - aboveBelow);
                    max = Math.min(rowNamePair.size(), playerPos + aboveBelow);
                }
            }

            if(OinkConfig.scoreboardMode == ScoreboardMode.DEFAULT || rangeModeFailed) { // STANDARD MODE
                context.fill(xOffset, currentY, this.scaledWidth, currentY + 9, backgroundColorLight);
                context.drawText(tr, pair.getSecond(), x, currentY, -1, false);

                if (inc == playerRows.size()) {
                    context.fill(xOffset, currentY - 9 - 1, this.scaledWidth, currentY - 1, backgroundColorDark);
                    context.fill(xOffset, currentY - 1, this.scaledWidth, currentY, backgroundColorLight);
                    context.drawText(tr, scoreboardTitle, (x + textWidth / 2 - textWidth2 / 2), currentY - 9, -1, false);
                }
            }
        }

        if(OinkConfig.scoreboardMode == ScoreboardMode.RANGE && !rangeModeFailed) renderRangeModeSB(context, scoreboardTitle, rows, min, max, m, xOffset, this.scaledWidth, textWidth, textWidth2, backgroundColorLight, backgroundColorDark);

        ci.cancel();
    }

    @Unique
    private static void renderRangeModeSB(DrawContext context, Text scoreboardTitle, List<Text> rows, int min, int max, int m, int xOffset, int scaledWidth, int titleWidth, int titleWidth2, int backgroundColorLight, int backgroundColorDark) {
        int x = scaledWidth - titleWidth - 3;
        int inc = 0;

        for(Text rowText : rows) {
            int currentY;
            if(rows.indexOf(rowText) + 1 >= min && rows.indexOf(rowText) + 1 <= max) {
                ++inc;
                currentY = m - inc * 9;
                context.fill(xOffset, currentY, scaledWidth, currentY + 9, backgroundColorLight);
                context.drawText(MinecraftClient.getInstance().textRenderer, rowText, x, currentY, -1, false);
            }

            if(rows.indexOf(rowText) + 1 >= max) {
                currentY = m - inc * 9;
                context.fill(xOffset, currentY - 9 - 1, scaledWidth, currentY - 1, backgroundColorDark);
                context.fill(xOffset, currentY - 1, scaledWidth, currentY, backgroundColorLight);
                context.drawText(MinecraftClient.getInstance().textRenderer, scoreboardTitle, (x + titleWidth / 2 - titleWidth2 / 2), currentY - 9, -1, false);
                break;
            }
        }
    }
}
