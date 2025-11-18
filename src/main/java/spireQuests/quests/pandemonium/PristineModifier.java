package spireQuests.quests.pandemonium;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.TooltipInfo;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;
import java.util.List;

import static spireQuests.Anniv8Mod.makeID;

public class PristineModifier extends AbstractCardModifier {
    public static final String ID = makeID(PristineModifier.class.getSimpleName());
    private static final UIStrings strings = CardCrawlGame.languagePack.getUIString(ID);
    private static float shineTimer = 0f;
    private static final Color SHINE_COLOR = new Color(0.85f,0.9f,1f,1f);
    private static final float SHINE_FREQUENCY = 0.3f;

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        tips.add(new TooltipInfo(strings.TEXT[0],strings.TEXT[1]));
        return tips;
    }

    @Override
    public void onUpdate(AbstractCard card) {
        shineTimer += Gdx.graphics.getDeltaTime();
        if (shineTimer >= SHINE_FREQUENCY) {
            float shineX = MathUtils.random(card.current_x - card.hb.width / 2f, card.current_x + card.hb.width / 2f);
            float shineY = MathUtils.random(card.current_y - card.hb.height / 2f, card.current_y + card.hb.height / 2f);
            AbstractGameEffect shineEffect =
                    new VfxBuilder(ImageMaster.ROOM_SHINE_2, shineX, shineY, 1.5f)
                            .setAlpha(0.7f)
                            .setScale(0.6f*card.drawScale)
                            .setColor(SHINE_COLOR)
                            .fadeIn(1f)
                            .fadeOut(1.5f)
                            .build();
            AbstractDungeon.topLevelEffectsQueue.add(shineEffect);
            shineTimer = MathUtils.random(0f,0.25f);
        }
    }


    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PristineModifier();
    }
}
