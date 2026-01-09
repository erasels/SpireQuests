package spireQuests.quests.enbeon.powers;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.stances.DivinityStance;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.stance.StanceChangeParticleGenerator;
import spireQuests.abstracts.AbstractSQPower;
import spireQuests.quests.enbeon.monsters.WatcherEliteMonster;

import static spireQuests.Anniv8Mod.makeID;

public class InvisibleDivinityForMonsterPower extends AbstractSQPower implements InvisiblePower {
    public static String POWER_ID = makeID(InvisibleDivinityForMonsterPower.class.getSimpleName());
    public static String NAME = "";

    public InvisibleDivinityForMonsterPower(AbstractCreature owner) {
        super(POWER_ID, NAME, "enbeon", NeutralPowertypePatch.NEUTRAL, false, owner, 0);
        updateDescription();
        this.loadRegion("mantra");
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return (type == DamageInfo.DamageType.NORMAL ? damage * 3.0f : damage);
    }

    @Override
    public void atEndOfRound() {
        addToTop(new RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public void onInitialApplication() {
        CardCrawlGame.sound.play("STANCE_ENTER_DIVINITY");
        AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.PINK, true));
        AbstractDungeon.effectsQueue.add(new StanceChangeParticleGenerator(owner.hb.cX, owner.hb.cY, DivinityStance.STANCE_ID));
        if (owner instanceof WatcherEliteMonster) {
            WatcherEliteMonster watcher = (WatcherEliteMonster) owner;
            watcher.startIdleSfx();
            watcher.eyeState.setAnimation(0, "Divinity", true);
            watcher.prepareDivinityMove();
        }
    }

    @Override
    public void onRemove() {
        if (owner instanceof WatcherEliteMonster) {
            WatcherEliteMonster watcher = (WatcherEliteMonster) owner;
            watcher.stopIdleSfx();
            watcher.eyeState.setAnimation(0, "None", true);
        }
    }
}
