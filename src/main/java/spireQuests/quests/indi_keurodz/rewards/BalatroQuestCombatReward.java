package spireQuests.quests.indi_keurodz.rewards;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import basemod.abstracts.CustomReward;
import basemod.helpers.CardModifierManager;
import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.modifiers.FoilModifier;
import spireQuests.quests.indi_keurodz.modifiers.HoloModifier;
import spireQuests.quests.indi_keurodz.modifiers.NegativeModifier;
import spireQuests.quests.indi_keurodz.modifiers.PolychromeModifier;
import spireQuests.quests.indi_keurodz.patches.StickersInRewardsPatch;
import spireQuests.util.TexLoader;

public class BalatroQuestCombatReward extends CustomReward {

    private boolean cardsSelected = true;

    private static final UIStrings uiStrings = CardCrawlGame.languagePack
            .getUIString(Anniv8Mod.makeID("BalatroQuestCombatReward"));

    public static final TextureRegion REWARD_ICON = TexLoader
            .getTextureAsAtlasRegion(Anniv8Mod.modID + "Resources/images/indi_keurodz/Aura.png");

    public BalatroQuestCombatReward() {
        super(REWARD_ICON, uiStrings.TEXT[0], StickersInRewardsPatch.BALATRO_QUEST_REWARD);
    }

    @Override
    public boolean claimReward() {
        this.cardsSelected = false;
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }

        if (AbstractDungeon.player.masterDeck.isEmpty()) {
            cardsSelected = true;
        } else {
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 3,
                    uiStrings.TEXT[1], false, false, false, false);
            AbstractDungeon.effectsQueue.add(new AbstractGameEffect() {
                @Override
                public void update() {
                    if (!cardsSelected && AbstractDungeon.gridSelectScreen.selectedCards.size() == 3) {
                        cardsSelected = true;
                        for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                            c.untip();
                            c.unhover();

                            int roll = AbstractDungeon.cardRng.random(3);
                            switch (roll) {
                                case 0:
                                    CardModifierManager.addModifier(c, new PolychromeModifier());
                                    break;
                                case 1:
                                    CardModifierManager.addModifier(c, new NegativeModifier());
                                    break;
                                case 2:
                                    CardModifierManager.addModifier(c, new HoloModifier());
                                    break;
                                case 3:
                                    CardModifierManager.addModifier(c, new FoilModifier());
                                    break;
                            }

                            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                        }

                        AbstractDungeon.gridSelectScreen.selectedCards.clear();
                        isDone = true;
                    }
                }

                @Override
                public void render(SpriteBatch sb) {
                }

                @Override
                public void dispose() {
                }
            });
        }
        return true;
    }

}
