package spireQuests.quests.coda.potions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Fission;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeContributionPath;

import spireQuests.util.TexLoader;

import java.lang.reflect.Field;

public class NuclearJuicePotion extends AbstractPotion {
    private static final Field containerImg;
    private static final Field outlineImg;
    private static final Field liquidImg;
    private static final Field hybridImg;
    private static final Field spotsImg;
        static {
            try {
                containerImg = AbstractPotion.class.getDeclaredField("containerImg");
                outlineImg = AbstractPotion.class.getDeclaredField("outlineImg");
                liquidImg = AbstractPotion.class.getDeclaredField("liquidImg");
                hybridImg = AbstractPotion.class.getDeclaredField("hybridImg");
                spotsImg = AbstractPotion.class.getDeclaredField("spotsImg");

                containerImg.setAccessible(true);
                outlineImg.setAccessible(true);
                liquidImg.setAccessible(true);
                hybridImg.setAccessible(true);
                spotsImg.setAccessible(true);
            } catch (NoSuchFieldException e){
                throw new RuntimeException("Failed to access potion image fields.", e);
            }
        }
    public static final String POTION_ID = makeID(NuclearJuicePotion.class.getSimpleName());

    // private static final Color LIQUID_COLOR = Color.ORANGE;
    // private static final Color HYBRID_COLOR = Color.RED;
    // private static final Color SPOTS_COLOR = Color.ORANGE;

    private static final Color LIQUID_COLOR = Color.CYAN;
    private static final Color HYBRID_COLOR = Color.BLUE;
    private static final Color SPOTS_COLOR = Color.CYAN;

    private static final Texture CONTAINER_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "potions/exploding_glass_2.png"));
    private static final Texture OUTLINE_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "potions/exploding_outline.png"));
    private static final Texture LIQUID_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "potions/exploding_liquid.png"));
    private static final Texture HYBRID_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "potions/exploding_hybrid.png"));
    private static final Texture SPOTS_TEXTURE = TexLoader.getTexture(makeContributionPath("coda", "potions/exploding_spots_2.png"));

    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);
    
    public NuclearJuicePotion() {
        super(potionStrings.NAME, POTION_ID, PotionRarity.PLACEHOLDER, PotionSize.SPHERE, PotionEffect.OSCILLATE, LIQUID_COLOR, HYBRID_COLOR, SPOTS_COLOR);
        try {
            outlineImg.set(this, OUTLINE_TEXTURE);
            containerImg.set(this, CONTAINER_TEXTURE);
            liquidImg.set(this, LIQUID_TEXTURE);
            hybridImg.set(this, HYBRID_TEXTURE);
            spotsImg.set(this, SPOTS_TEXTURE);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initializeData() {
        this.potency = getPotency();
        this.description = potionStrings.DESCRIPTIONS[0] + this.potency + potionStrings.DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 1;
    }
    @Override
    public AbstractPotion makeCopy() {
        return new NuclearJuicePotion();
    }
    @Override
    public void use(AbstractCreature target) {
        if (this.potency < 1) {
            return;
        }
        AbstractCard card = new Fission();
        card.upgrade();
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            addToBot(new MakeTempCardInHandAction(card.makeStatEquivalentCopy(), this.potency));
        }
    }

    
}
