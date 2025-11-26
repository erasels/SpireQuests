package spireQuests.quests.gk.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import spireQuests.util.Wiz;

import static spireQuests.quests.gk.util.HermitCompatUtil.HERMIT_GUN_EFFECT;

public class TrapshotAction extends AbstractGameAction {
    private DamageInfo info;
    private int deadonTimes;

    public TrapshotAction(AbstractCreature target, DamageInfo info) {
        this(target, info, 1);
    }

    public TrapshotAction(AbstractCreature target, DamageInfo info, int deadOnTimes) {
        this.info = info;
        this.setValues(target, info);
        this.actionType = ActionType.DAMAGE;
        this.startDuration = Settings.ACTION_DUR_FAST;
        this.duration = this.startDuration;
        this.deadonTimes = deadOnTimes;
    }

    public void update() {
        if (shouldCancelAction()) {
            isDone = true;
        } else {
            tickDuration();
            if (isDone) {
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, HERMIT_GUN_EFFECT, false));
                target.damage(info);
                for (int i = 0; i < deadonTimes; i++) {
                    if (target.lastDamageTaken > 0) {
                        if (!target.hasPower(ArtifactPower.POWER_ID)) {
                            Wiz.applyToEnemyTop((AbstractMonster) target, new GainStrengthPower(target, target.lastDamageTaken));
                        }
                        Wiz.applyToEnemyTop((AbstractMonster) target, new StrengthPower(target, -target.lastDamageTaken));
                    }
                }

                if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.clearPostCombatActions();
                } else {
                    addToTop(new WaitAction(0.1F));
                }
            }

        }
    }
}