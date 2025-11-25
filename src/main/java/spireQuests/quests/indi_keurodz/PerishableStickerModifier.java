package spireQuests.quests.indi_keurodz;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.util.extraicons.ExtraIcons;
import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.Anniv8Mod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.indi_keurodz.patches.BattleEndPatch;
import spireQuests.util.TexLoader;

public class PerishableStickerModifier extends AbstractCardModifier {

    public static String MODIFIER_ID = Anniv8Mod.makeID("PerishableSticker");

    public int REMAINING_TURNS;

    private static final Texture icon = TexLoader
            .getTexture(Anniv8Mod.modID + "Resources/images/indi_keurodz/PerishableStickerIcon.png");

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        ExtraIcons.icon(icon).render(card);
    }

    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PerishableStickerModifier();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        REMAINING_TURNS = 5;
    }

    /*
        Ticks remaining turns down by 1
        @returns true if the remaining turns is 0 or less than 0
     */
    public boolean tickRemainingTurns() {
        return --REMAINING_TURNS <= 0;
    }

}
