package spireQuests.quests.indi_keurodz.cards;

import java.util.Random;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.helpers.CardModifierManager;
import spireQuests.Anniv8Mod;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.quests.indi_keurodz.modifiers.DebuffedModifier;
import spireQuests.quests.indi_keurodz.modifiers.EternalModifier;
import spireQuests.quests.indi_keurodz.modifiers.FoilModifier;
import spireQuests.quests.indi_keurodz.modifiers.HoloModifier;
import spireQuests.quests.indi_keurodz.modifiers.NegativeModifier;
import spireQuests.quests.indi_keurodz.modifiers.PerishableModifier;
import spireQuests.quests.indi_keurodz.modifiers.PolychromeModifier;
import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;

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
                    CardModifierManager.addModifier(card, new EternalModifier());
                    CardModifierManager.addModifier(card, new NegativeModifier());
                    break;

                case 1:
                    CardModifierManager.addModifier(card, new PerishableModifier());
                    CardModifierManager.addModifier(card, new HoloModifier());
                    break;

                case 2:
                    CardModifierManager.addModifier(card, new RentalModifier());
                    CardModifierManager.addModifier(card, new PolychromeModifier());
                    break;

                case 3:
                    CardModifierManager.addModifier(card, new FoilModifier());
                    CardModifierManager.addModifier(card, new DebuffedModifier());
                    break;

            }

        }

        for (AbstractCard card : p.masterDeck.group) {
            int mod = rand.nextInt(3);

            switch (mod) {
                case 0:
                    CardModifierManager.addModifier(card, new EternalModifier());
                    CardModifierManager.addModifier(card, new NegativeModifier());
                    break;

                case 1:
                    CardModifierManager.addModifier(card, new PerishableModifier());
                    break;

                case 2:
                    CardModifierManager.addModifier(card, new RentalModifier());
                    break;

            }

        }
    }

}
