package spireQuests.quests.indi_keurodz.cards;

import java.util.Random;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.helpers.CardModifierManager;
import spireQuests.Anniv8Mod;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.quests.indi_keurodz.EternalStickerModifier;
import spireQuests.quests.indi_keurodz.FaceDownModifier;
import spireQuests.quests.indi_keurodz.PerishableStickerModifier;
import spireQuests.quests.indi_keurodz.RentalStickerModifier;

public class testcard extends AbstractSQCard {

    private static final String ID = Anniv8Mod.makeID("balatrotestcard");

    public testcard() {
        super(ID, "indi_keurodz", 0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.NONE);
    }

    @Override
    public void upp() {
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Random rand = new Random();
        for (AbstractCard card : p.hand.group) {
            int mod = rand.nextInt(4);

            switch (mod) {
                case 0:
                    CardModifierManager.addModifier(card, new EternalStickerModifier());
                    break;

                case 1:
                    CardModifierManager.addModifier(card, new PerishableStickerModifier());
                    break;

                case 2:
                    CardModifierManager.addModifier(card, new RentalStickerModifier());
                    break;

                case 3:
                    CardModifierManager.addModifier(card, new FaceDownModifier());
                    break;

            }

        }

        for (AbstractCard card : p.masterDeck.group) {
            int mod = rand.nextInt(3);

            switch (mod) {
                case 0:
                    CardModifierManager.addModifier(card, new EternalStickerModifier());
                    break;

                case 1:
                    CardModifierManager.addModifier(card, new PerishableStickerModifier());
                    break;

                case 2:
                    CardModifierManager.addModifier(card, new RentalStickerModifier());
                    break;

            }

        }
    }

}
