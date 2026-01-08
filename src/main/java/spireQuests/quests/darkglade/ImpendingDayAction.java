package spireQuests.quests.darkglade;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import static spireQuests.quests.darkglade.DoomsdayCalendarQuest.IMPENDING_DAY_KILL;

public class ImpendingDayAction extends AbstractGameAction {
    private final DamageInfo info;
    private final int heal;
    private final AbstractCard parentCard;

    public ImpendingDayAction(AbstractCreature target, DamageInfo info, int heal, AbstractCard parentCard) {
        this.info = info;
        this.setValues(target, info);
        this.heal = heal;
        this.parentCard = parentCard;
        this.actionType = ActionType.DAMAGE;
        this.duration = 0.1F;
    }

    public void update() {
        if (this.duration == 0.1F && this.target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.BLUNT_LIGHT));
            this.target.damage(this.info);
            if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead && !this.target.hasPower(MinionPower.POWER_ID)) {
                AbstractDungeon.actionManager.addToBottom(new HealAction(AbstractDungeon.player, AbstractDungeon.player, heal));
                IMPENDING_DAY_KILL.trigger();
                for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
                    if (action instanceof UseCardAction) {
                        AbstractCard actionCard = ReflectionHacks.getPrivate(action, UseCardAction.class, "targetCard");
                        if (actionCard == parentCard) {
                            ((UseCardAction) action).exhaustCard = true;
                        }
                    }
                }
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
    }
}
