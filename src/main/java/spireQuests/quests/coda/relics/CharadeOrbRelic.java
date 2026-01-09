package spireQuests.quests.coda.relics;

import static spireQuests.Anniv8Mod.makeContributionPath;
import static spireQuests.Anniv8Mod.makeID;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.quests.coda.monsters.CharadeMonster.OrbColor;
import spireQuests.util.TexLoader;

public class CharadeOrbRelic extends AbstractSQRelic {

    private static final Texture BASE_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "CharadeOrbRelic.png"));
    private static final Texture RED_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "CharadeOrbRelic_red.png"));
    private static final Texture GREEN_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "CharadeOrbRelic_green.png"));
    private static final Texture BLUE_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "CharadeOrbRelic_blue.png"));
    private static final Texture PURPLE_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "CharadeOrbRelic_purple.png"));
    
    private static final String ID = makeID(CharadeOrbRelic.class.getSimpleName());

    private OrbColor state;
    private List<OrbColor> stateOrder = new ArrayList<>();
    private StringBuilder strbuild = new StringBuilder();
    
    public CharadeOrbRelic() {
        super(ID, "coda", RelicTier.SPECIAL, LandingSound.MAGICAL);
        this.state = OrbColor.NONE;
        this.counter = -1;
        this.stateOrder.add(OrbColor.RED);
        this.stateOrder.add(OrbColor.GREEN);
        this.stateOrder.add(OrbColor.BLUE);
        this.stateOrder.add(OrbColor.PURPLE);
    }
    
    @Override
    public void update() {
        super.update();
        if (this.counter >= 0 && this.state.equals(OrbColor.NONE)) {
            this.counter--;
            updateState();
        }
    }
    
    @Override
    public void atTurnStart() {
        updateState();
        if (this.state.equals(OrbColor.PURPLE)) {
            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new ScryAction(1));
        } else if (this.state.equals(OrbColor.RED)) {
            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(5, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE));
        } else if (this.state.equals(OrbColor.BLUE)) {
            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 5));
        }
    }

    @Override
    public void atTurnStartPostDraw() {
        if (this.state.equals(OrbColor.GREEN)) {
            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new DrawCardAction(AbstractDungeon.player, 1));
            addToBot(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false));
        }
    }

    @Override
    public void onEquip() {
        updateState();
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {}

    private void updateState() {
        this.counter = (this.counter + 1) % this.stateOrder.size();
        this.state = stateOrder.get(this.counter);
        switch (this.state) {
            case RED:
                this.img = RED_TEXTURE;
                break;
            case GREEN:
                this.img = GREEN_TEXTURE;
                break;
            case BLUE:
                this.img = BLUE_TEXTURE;
                break;
            case PURPLE:
                this.img = PURPLE_TEXTURE;
                break;
            default:
                this.img = BASE_TEXTURE;
                break;
        }
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, getTooltipDescription()));
    }

    public String getTooltipDescription() {
        this.strbuild.setLength(0);
        switch (this.state) {
            case RED:
                strbuild.append(DESCRIPTIONS[1] + " NL ");
                break;
            case GREEN:
                strbuild.append(DESCRIPTIONS[2] + " NL ");
                break;
            case BLUE:
                strbuild.append(DESCRIPTIONS[3] + " NL ");
                break;
            case PURPLE:
                strbuild.append(DESCRIPTIONS[4] + " NL ");
                break;
            default:
                strbuild.append(DESCRIPTIONS[0]);
                break;
        }

        String s;
        switch (this.stateOrder.get(this.counter)) {
            case RED:
                s = DESCRIPTIONS[7];
                break;
            case GREEN:
                s = DESCRIPTIONS[8];
                break;
            case BLUE:
                s = DESCRIPTIONS[9];
                break;
            case PURPLE:
                s = DESCRIPTIONS[6];
                break;
            default:
                s = "";
                break;
        }
        if (this.stateOrder != null) {
            strbuild.append(String.format(DESCRIPTIONS[5], s));
        }
        return strbuild.toString();
    }

}
