package spireQuests.quests.coda.relics;

import static spireQuests.Anniv8Mod.makeID;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;


import spireQuests.abstracts.AbstractSQRelic;

public class SpearmintPelletRelic extends AbstractSQRelic {

    private static final String RELIC_ID = makeID(SpearmintPelletRelic.class.getSimpleName());
    private static boolean SKILL = false, POWER = false, ATTACK = false;

    public SpearmintPelletRelic() {
        super(RELIC_ID, "coda", RelicTier.SPECIAL, LandingSound.SOLID);
    }

    @Override
    public void atTurnStart() {
        SKILL = false;
        POWER = false;
        ATTACK = false;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            ATTACK = true;
        } else if (card.type == AbstractCard.CardType.SKILL) {
            SKILL = true;
        } else if (card.type == AbstractCard.CardType.POWER) {
            POWER = true;
        }

        if (ATTACK && SKILL && POWER) {
            flash();

            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            addToBot((AbstractGameAction)new GainEnergyAction(1));

            SKILL = false;
            POWER = false;
            ATTACK = false;
        }
    }
    
}
