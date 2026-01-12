package spireQuests.quests.luaviper.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Bite;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import spireQuests.abstracts.AbstractSQRelic;

import java.util.ArrayList;

import static spireQuests.Anniv8Mod.makeID;

public class VampireFangs extends AbstractSQRelic {

    private static final String ID = makeID(VampireFangs.class.getSimpleName());

    public VampireFangs() {
        super(ID, "luaviper", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEquip() {
        ArrayList<AbstractCard> masterDeck = AbstractDungeon.player.masterDeck.group;
        int i;
        AbstractCard strike = null;

        // search for unupgraded Strikes first
        for (i = masterDeck.size() - 1; i >= 0; i--) {
            AbstractCard card = masterDeck.get(i);
            if (!card.upgraded && card.tags.contains(AbstractCard.CardTags.STARTER_STRIKE)) {
                strike = card;
                break;
            }
        }
        if (strike == null) {
            // if no unupgraded Strikes were found, check for upgraded ones
            for (i = masterDeck.size() - 1; i >= 0; i--) {
                AbstractCard card = masterDeck.get(i);
                if (card.tags.contains(AbstractCard.CardTags.STARTER_STRIKE)) {
                    strike = card;
                    break;
                }
            }
        }
        //finally, replace the found strike with a bite
        if (strike != null) {
            AbstractDungeon.player.masterDeck.removeCard(strike);
            Bite bite = new Bite();
            if (strike.upgraded) bite.upgrade();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(bite, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        }
    }
}