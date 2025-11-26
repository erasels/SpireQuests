package spireQuests.quests.modargo.relics;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.quests.modargo.MulticlassQuest;

import static spireQuests.Anniv8Mod.makeID;

public class MulticlassEmblem extends AbstractSQRelic implements CustomSavable<String> {
    public static final String ID = makeID(MulticlassEmblem.class.getSimpleName());

    private static final int GOLD = 10;

    public AbstractPlayer.PlayerClass playerClass;

    public MulticlassEmblem() {
        super(ID, "modargo", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        if (this.playerClass == null) {
            return String.format(DESCRIPTIONS[0], GOLD);
        }
        String name = FontHelper.colorString(MulticlassQuest.getCharacter(this.playerClass).getTitle(this.playerClass), "y");
        return String.format(DESCRIPTIONS[1], name, name, GOLD);
    }

    @Override
    public void onObtainCard(AbstractCard c) {
        if (c.color == MulticlassQuest.getCardColor(this.playerClass)) {
            AbstractDungeon.player.gainGold(GOLD);
        }
    }

    public void setPlayerClass(AbstractPlayer.PlayerClass playerClass) {
        this.playerClass = playerClass;
        this.description = this.getUpdatedDescription();
        this.resetTips();
    }

    private void resetTips() {
        tips.clear();
        tips.add(new PowerTip(name, description));
        initializeTips();
    }

    @Override
    public String onSave() {
        return this.playerClass == null ? null : this.playerClass.name();
    }

    @Override
    public void onLoad(String s) {
        this.setPlayerClass(s == null ? null : AbstractPlayer.PlayerClass.valueOf(s));
    }
}
