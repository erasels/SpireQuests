package spireQuests.quests.snumodder.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.quests.snumodder.ZilliaxDeluxe3000Quest;

public abstract class AbstractModuleCard extends AbstractSQCard {
    public final CardStrings cardStrings;
    public boolean costReduction = false;
    public boolean persist = false;

    public AbstractModuleCard(final String cardID, final String packageName, final int cost, final CardType type, final CardRarity rarity, final CardTarget target, final CardColor color) {
        super(cardID, packageName, cost, type, rarity, target, color);
        cardStrings = CardCrawlGame.languagePack.getCardStrings(this.cardID);
    }

    public abstract void moduleUse(AbstractPlayer p, AbstractMonster m, AbstractCard card);

    public abstract ZilliaxDeluxe3000Quest.ZilliaxModules getModule();
}
