package spireQuests.quests.indi_keurodz.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SoulboundField;
import com.evacipated.cardcrawl.mod.stslib.util.extraicons.ExtraIcons;
import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.icons.EternalIcon;
import spireQuests.util.TexLoader;

public class EternalModifier extends AbstractCardModifier {

    public static final String ID = Anniv8Mod.makeID("Eternal");

    public static final Texture icon = TexLoader
            .getTexture(Anniv8Mod.modID + "Resources/images/indi_keurodz/EternalStickerIcon.png");

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        ExtraIcons.icon(icon).render(card);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EternalModifier();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        SoulboundField.soulbound.set(card, true);
        card.initializeDescription();
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        final Keyword KEYWORD = Anniv8Mod.keywords.get(ID);
        tips.add(new TooltipInfo("[" + EternalIcon.ID + "Icon] " + KEYWORD.PROPER_NAME, KEYWORD.DESCRIPTION));
        return tips;
    }

}
