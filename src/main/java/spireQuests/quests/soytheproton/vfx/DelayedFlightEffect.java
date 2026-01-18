package spireQuests.quests.soytheproton.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import spireQuests.quests.soytheproton.patches.MothFriendPatch;

import static spireQuests.quests.soytheproton.MothQuest.FLY_SFX;

public class DelayedFlightEffect extends AbstractGameEffect {
    private static final float DURATION = 5.0f;
    private float vX;
    private float vY;
    private boolean laugh;
    public DelayedFlightEffect() {
        this.duration = DURATION;
        vX = 600.0F;
        vY = 400.0F;
        MothFriendPatch.isFlyingMoth = true;
    }

    @Override
    public void update() {
        duration -= Gdx.graphics.getDeltaTime();
        if (duration < DURATION - 1.0F) {
            if(!laugh) {
                CardCrawlGame.sound.play(FLY_SFX,0.1F);
                laugh = true;
            }
            if(!MothFriendPatch.flyingMoth)MothFriendPatch.flyingMoth = true;
            MothFriendPatch.drawY += vY * Gdx.graphics.getDeltaTime();
            MothFriendPatch.drawX += vX * Gdx.graphics.getDeltaTime();
            vY += vY * 2.0f * Gdx.graphics.getDeltaTime();
            vX -= vX * 2.0f * Gdx.graphics.getDeltaTime();
        }
        if(duration < 0f) {
            MothFriendPatch.isFlyingMoth = false;
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
    }

    @Override
    public void dispose() {
    }
}
