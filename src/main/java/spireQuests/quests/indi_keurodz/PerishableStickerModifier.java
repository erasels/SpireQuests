package spireQuests.quests.indi_keurodz;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.util.extraicons.ExtraIcons;
import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import spireQuests.Anniv8Mod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.indi_keurodz.patches.BattleEndPatch;
import spireQuests.util.TexLoader;

public class PerishableStickerModifier extends AbstractCardModifier {

    public static String MODIFIER_ID = Anniv8Mod.makeID("PerishableSticker");

    public static int REMAINING_TURNS;

    private static final Texture icon = TexLoader
            .getTexture(Anniv8Mod.modID + "Resources/images/indi_keurodz/PerishableStickerIcon.png");

    @Override
    public boolean isInherent(AbstractCard card) {
        return true;
    }

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

    public PerishableStickerModifier() {
        // REMAINING_TURNS = 5;
        // new AbstractQuest.TriggeredUpdateTracker<>(QuestTriggers.COMBAT_END, 0, 5, ()
        // -> {
        // return --REMAINING_TURNS;
        // }).add(this);

    }

}
