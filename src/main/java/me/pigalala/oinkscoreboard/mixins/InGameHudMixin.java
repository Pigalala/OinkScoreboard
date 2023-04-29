package me.pigalala.oinkscoreboard.mixins;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.pigalala.oinkscoreboard.OinkScoreboard;
import me.pigalala.oinkscoreboard.ScoreboardPlacements;
import me.pigalala.oinkscoreboard.config.OinkConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.client.gui.DrawableHelper.fill;

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
    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> playerRows = scoreboard.getAllPlayerScores(objective).stream().filter((score) -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList());

        List<Pair<ScoreboardPlayerScore, Text>> rowNamePair = Lists.newArrayListWithCapacity(playerRows.size());
        Text text = objective.getDisplayName();
        TextRenderer tr = this.getTextRenderer();
        int textWidth = tr.getWidth(text);
        int textWidth2 = textWidth;
        int colonSpaceWidth = getTextRenderer().getWidth(": ");

        ScoreboardPlayerScore scoreboardPlayerScore;
        MutableText text2;
        for(Iterator<ScoreboardPlayerScore> var11 = playerRows.iterator(); var11.hasNext(); textWidth = Math.max(textWidth, tr.getWidth(text2) + colonSpaceWidth + this.getTextRenderer().getWidth(Integer.toString(scoreboardPlayerScore.getScore())))) {
            scoreboardPlayerScore = var11.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            text2 = Team.decorateName(team, Text.literal(scoreboardPlayerScore.getPlayerName()));
            rowNamePair.add(Pair.of(scoreboardPlayerScore, text2));
        }

        int m;
        if(OinkConfig.scoreboardPlacement == ScoreboardPlacements.NORMAL) m = this.scaledHeight / 2 + (playerRows.size() * 9) / 2; // Normal placement
        else m = this.scaledHeight; // Bottom placement

        int x = this.scaledWidth - textWidth - 3;
        int xOffset = x - 2;
        int inc = 0;

        int backgroundColorLight = OinkConfig.scoreboardColour;
        int backgroundColorDark = OinkConfig.scoreboardColour + 0x1A000000; // 66 - 4c

        for (Pair<ScoreboardPlayerScore, Text> pair : rowNamePair) {
            ++inc;
            int currentY = m - inc * 9;
            fill(matrices, xOffset, currentY, this.scaledWidth, currentY + 9, backgroundColorLight);
            tr.draw(matrices, pair.getSecond(), (float) x, (float) currentY, -1);

            if (inc == playerRows.size()) {
                fill(matrices, xOffset, currentY - 9 - 1, this.scaledWidth, currentY - 1, backgroundColorDark);
                fill(matrices, xOffset, currentY - 1, this.scaledWidth, currentY, backgroundColorLight);
                tr.draw(matrices, text, (float) (x + textWidth / 2 - textWidth2 / 2), (float) (currentY - 9), -1);
            }
        }
        ci.cancel();
    }
}
