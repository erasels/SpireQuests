package spireQuests.quests.indi_keurodz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class RentalStickerEffect extends AbstractGameEffect {
    private int numSoundsPlayed = 0;
    private int numStickers;

    private static final float DURATION = 0.3f;

    public RentalStickerEffect(int numStickers) {
        this.numStickers = numStickers;
        this.duration = 0f;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();

        while (numSoundsPlayed < numStickers && this.duration < 0.0F) {
            CardCrawlGame.sound.play(RentalStickerModifier.MODIFIER_ID);

            if (AbstractDungeon.player != null) {
                AbstractDungeon.player.loseGold(3);
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
    }

    @Override
    public void render(SpriteBatch sb) {
    }

}
