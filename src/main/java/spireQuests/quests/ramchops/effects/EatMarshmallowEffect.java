package spireQuests.quests.ramchops.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;
import spireQuests.quests.Trigger;

import static spireQuests.util.Wiz.adp;

public class EatMarshmallowEffect extends AbstractGameEffect {
    private static final float DUR = 4.0F;
    private boolean finishedCooking = false;


    public EatMarshmallowEffect(){
        this.duration = DUR;
        ((RestRoom) AbstractDungeon.getCurrRoom()).cutFireSound();
    }

    public static final Trigger<Void> EAT_MARSHMALLOW = new Trigger<>();

    @Override
    public void update() {
        if (this.duration == DUR){
            AbstractDungeon.topLevelEffects.add(new ScreenOnFireEffect());
        }

        this.duration -= Gdx.graphics.getDeltaTime();

        if (this.duration < 1.0F && !finishedCooking){
            finishedCooking = true;
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, true);
            CardCrawlGame.sound.play("EVENT_VAMP_BITE");
            AbstractDungeon.topLevelEffects.add(new BorderFlashEffect(Color.GREEN));
            adp().increaseMaxHp(3, true);
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
            EAT_MARSHMALLOW.trigger();
            ((RestRoom)AbstractDungeon.getCurrRoom()).fadeIn();
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {

    }

    @Override
    public void dispose() {

    }
}
