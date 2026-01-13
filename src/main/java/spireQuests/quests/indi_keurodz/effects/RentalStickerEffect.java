package spireQuests.quests.indi_keurodz.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;
import spireQuests.quests.indi_keurodz.patches.DisableLoseGoldSfxPatch;

public class RentalStickerEffect extends AbstractGameEffect {
    private int numSoundsPlayed = 0;
    private final int numStickers;
    private int extraGold = 0;
    private int originalGold = -5;

    private static final float DURATION = 0.35f;

    public RentalStickerEffect(int numStickers) {
        if (numStickers > 5) {
            extraGold = (numStickers - 5) * 3;
            this.numStickers = 5;
        } else {
            this.numStickers = numStickers;

        }
        this.duration = 0f;
    }

    @Override
    public void update() {
        if (originalGold == -5) {
            originalGold = AbstractDungeon.player.gold;
        }
        DisableLoseGoldSfxPatch.ENABLED = true;
        this.duration -= Gdx.graphics.getDeltaTime();

        while (numSoundsPlayed < numStickers && this.duration < 0.0F) {
            CardCrawlGame.sound.play(RentalModifier.ID);

            AbstractDungeon.player.loseGold(Integer.min(3, originalGold));
            originalGold -= 3;

            if (numSoundsPlayed == numStickers - 1) {
                AbstractDungeon.player.loseGold(Integer.min(extraGold, originalGold));
            }

            numSoundsPlayed++;

            this.duration += DURATION;
        }

        if (numSoundsPlayed >= numStickers) {
            this.isDone = true;
        }

    }

    @Override
    public void dispose() {
        DisableLoseGoldSfxPatch.ENABLED = false;
    }

    @Override
    public void render(SpriteBatch sb) {
    }

}
