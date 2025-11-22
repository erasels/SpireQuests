package spireQuests.quests.modargo.cards;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.quests.modargo.PackFanaticQuest;
import spireQuests.quests.modargo.actions.PerfectlyPackedAction;

import static spireQuests.Anniv8Mod.makeID;

public class PerfectlyPacked extends AbstractSQCard {
    public static final String ID = makeID(PerfectlyPacked.class.getSimpleName());

    public PerfectlyPacked() {
        super(ID, "modargo", 0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new PerfectlyPackedAction());
    }

    @Override
    public void upp() {
        this.isInnate = true;
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (!super.canUse(p, m)) {
            return false;
        }
        if (!Loader.isModLoaded("anniv5")) {
            return false;
        }
        long packCardCount = AbstractDungeon.player.drawPile.group.stream()
                .filter(c -> PackFanaticQuest.cardParentMap.containsKey(c.cardID))
                .count();
        if (packCardCount == 0) {
            this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
            return false;
        }
        return true;
    }
}
