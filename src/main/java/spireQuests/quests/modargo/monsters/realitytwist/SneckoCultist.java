package spireQuests.quests.modargo.monsters.realitytwist;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.exordium.Cultist;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.Anniv8Mod;
import spireQuests.quests.modargo.RealityTwistQuest;

public class SneckoCultist extends Cultist {
    public static final String[] sneckoCultistDialog = CardCrawlGame.languagePack.getUIString(Anniv8Mod.makeID("SneckoCultistDialog")).TEXT;
    private boolean praiseSnecko = true;

    public SneckoCultist(float x, float y) {
        super(x, y, false);
        this.name = RealityTwistQuest.enemyNames[2];
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 5)));
        this.addToBot(new ApplyPowerAction(this, this, new PlatedArmorPower(this, 10)));
        this.addToBot(new GainBlockAction(this, 10));
    }

    @Override
    public void takeTurn() {
        if (this.praiseSnecko) {
            this.addToBot(new TalkAction(this, sneckoCultistDialog[MathUtils.random(sneckoCultistDialog.length - 1)], 1.0F, 2.0F));// 88
            this.praiseSnecko = false;
        }
        super.takeTurn();
    }
}
