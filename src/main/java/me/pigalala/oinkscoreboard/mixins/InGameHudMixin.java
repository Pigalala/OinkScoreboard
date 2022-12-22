package me.pigalala.oinkscoreboard.mixins;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.pigalala.oinkscoreboard.OinkScoreboard;
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
import java.util.Objects;
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
        if (OinkScoreboard.config.scoreboardSize != -1 && playerRows.size() > OinkScoreboard.config.scoreboardSize) {
            playerRows = Lists.newArrayList(Iterables.skip(playerRows, playerRows.size() - OinkScoreboard.config.scoreboardSize));
        }

        List<Pair<ScoreboardPlayerScore, Text>> rowNamePair = Lists.newArrayListWithCapacity((playerRows).size());
        Text text = objective.getDisplayName();
        int textWidth = this.getTextRenderer().getWidth(text);
        int textWidth2 = textWidth;
        int colonSpaceWidth = this.getTextRenderer().getWidth(": ");

        ScoreboardPlayerScore scoreboardPlayerScore;
        MutableText text2;
        for(Iterator<ScoreboardPlayerScore> var11 = playerRows.iterator(); var11.hasNext(); textWidth = Math.max(textWidth, this.getTextRenderer().getWidth(text2) + colonSpaceWidth + this.getTextRenderer().getWidth(Integer.toString(scoreboardPlayerScore.getScore())))) {
            scoreboardPlayerScore = var11.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            text2 = Team.decorateName(team, Text.literal(scoreboardPlayerScore.getPlayerName()));
            rowNamePair.add(Pair.of(scoreboardPlayerScore, text2));
        }

        int var10000 = playerRows.size();
        Objects.requireNonNull(this.getTextRenderer());
        int l = var10000 * 9;
        int m = this.scaledHeight / 2 + l / 2;
        int x = this.scaledWidth - textWidth - 3;
        int inc = 0;
        int backgroundColorLight = this.client.options.getTextBackgroundColor(0.2F);
        int backgroundColorDark = this.client.options.getTextBackgroundColor(0.75F);

        for (Pair<ScoreboardPlayerScore, Text> pair : rowNamePair) {
            ++inc;
            Text textRow = pair.getSecond();
            Objects.requireNonNull(this.getTextRenderer());
            int currentY = m - inc * 9;
            int u = this.scaledWidth /*- 3 + 2*/;
            int var10001 = x - 2;
            Objects.requireNonNull(this.getTextRenderer());
            fill(matrices, var10001, currentY, u, currentY + 9, backgroundColorLight);
            this.getTextRenderer().draw(matrices, textRow, (float) x, (float) currentY, -1);
            if (inc == playerRows.size()) {
                //var10001 = x - 2;
                Objects.requireNonNull(this.getTextRenderer());
                fill(matrices, var10001, currentY - 9 - 1, u, currentY - 1, backgroundColorDark);
                fill(matrices, x - 2, currentY - 1, u, currentY, backgroundColorLight);
                TextRenderer textRenderer = this.getTextRenderer();
                float var10003 = (float) (x + textWidth / 2 - textWidth2 / 2);
                Objects.requireNonNull(this.getTextRenderer());
                textRenderer.draw(matrices, text, var10003, (float) (currentY - 9), -1);
            }
        }
        ci.cancel();
    }
}
