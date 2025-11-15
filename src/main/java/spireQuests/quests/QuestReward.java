package spireQuests.quests;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Sozu;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import spireQuests.Anniv8Mod;
import spireQuests.util.TexLoader;
import spireQuests.util.Wiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeUIPath;

public abstract class QuestReward {
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(makeID("QuestReward")).TEXT;
    private static final Map<String, RewardLoader> rewardLoaders = new HashMap<>();
    static {
        addRewardSaver(new RewardLoader(GoldReward.class, (save) -> new GoldReward(Integer.parseInt(save))));
        addRewardSaver(new RewardLoader(RelicReward.class, (save) -> new RelicReward(RelicLibrary.getRelic(save).makeCopy())));
        addRewardSaver(new RewardLoader(RandomRelicReward.class, (save) -> new RandomRelicReward(RelicLibrary.getRelic(save).makeCopy())));
        addRewardSaver(new RewardLoader(PotionReward.class, (save) -> new PotionReward(PotionHelper.getPotion(save))));
    }

    private static void addRewardSaver(RewardLoader loader) {
        rewardLoaders.put(loader.key, loader);
    }

    public static QuestReward fromSave(QuestRewardSave save) {
        RewardLoader loader = rewardLoaders.get(save.type);
        if (loader == null) {
            Anniv8Mod.logger.error("Unable to load saved reward of type " + save.type);
            return null;
        }
        return loader.loader.apply(save.param);
    }



    public String rewardText;

    public QuestReward(String rewardText) {
        this.rewardText = rewardText;
    }

    public QuestRewardSave getSave() {
        return new QuestRewardSave(getClass().getSimpleName(), saveParam());
    }

    public abstract TextureRegion icon();
    protected abstract String saveParam();

    @Override
    public String toString() {
        return rewardText;
    }

    public abstract void obtainRewardItem();
    public abstract void obtainInstant();

    public void addTooltip(List<PowerTip> previewTooltips) { }


    public static class GoldReward extends QuestReward {
        private static final TextureRegion img = new TextureRegion(ImageMaster.UI_GOLD, 8, 0, 48, 48);
        private final int amount;

        public GoldReward(int amount) {
            super(String.format(TEXT[0], amount));
            this.amount = amount;
        }

        @Override
        public TextureRegion icon() {
            return img;
        }

        @Override
        public void obtainRewardItem() {
            AbstractDungeon.combatRewardScreen.rewards.add(0, new RewardItem(amount));
            AbstractDungeon.combatRewardScreen.positionRewards();
        }

        @Override
        public void obtainInstant() {
            AbstractDungeon.effectList.add(new RainingGoldEffect(amount));
            AbstractDungeon.player.gainGold(amount);
        }

        @Override
        public String saveParam() {
            return String.valueOf(amount);
        }
    }

    public static class RelicReward extends QuestReward {
        private final AbstractRelic relic;
        private final TextureRegion img;

        public RelicReward(AbstractRelic r) {
            super(String.format(TEXT[1], r.name));
            this.relic = r;
            this.img = new TextureRegion(this.relic.img, 28, 28, 72, 72);
        }

        @Override
        public TextureRegion icon() {
            return img;
        }

        @Override
        public void addTooltip(List<PowerTip> tips) {
            tips.add(new PowerTip(relic.name, relic.description));
        }

        @Override
        public void obtainRewardItem() {
            AbstractDungeon.combatRewardScreen.rewards.add(0, new RewardItem(relic));
            AbstractDungeon.combatRewardScreen.positionRewards();
        }

        @Override
        public void obtainInstant() {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH * 0.95F, Settings.HEIGHT / 2.0F, relic);
        }

        @Override
        protected String saveParam() {
            return relic.relicId;
        }
    }

    public static class RandomRelicReward extends QuestReward {
        private final AbstractRelic.RelicTier tier;
        private final AbstractRelic relic;

        public RandomRelicReward(AbstractRelic.RelicTier tier) {
            super(text(tier));
            this.tier = tier;
            this.relic = AbstractDungeon.returnRandomScreenlessRelic(tier); //roll immediately to avoid weirdness with manipulating rng when claiming reward
        }

        private RandomRelicReward(AbstractRelic relic) {
            super(text(relic.tier));
            this.tier = relic.tier;
            this.relic = relic;
        }

        @Override
        public TextureRegion icon() {
            return ImageMaster.COPPER_COIN_1;
        }

        @Override
        public void obtainRewardItem() {
            AbstractDungeon.combatRewardScreen.rewards.add(0, new RewardItem(relic));
            AbstractDungeon.combatRewardScreen.positionRewards();
        }

        @Override
        public void obtainInstant() {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH * 0.95F, Settings.HEIGHT / 2.0F, relic);
        }

        @Override
        protected String saveParam() {
            return tier.name();
        }

        private static String text(AbstractRelic.RelicTier tier) {
            String relicTier;
            switch (tier) {
                case COMMON:
                    relicTier = TEXT[2];
                    break;
                case UNCOMMON:
                    relicTier = TEXT[3];
                    break;
                default:
                    relicTier = TEXT[4];
                    break;
            }
            return String.format(TEXT[1], relicTier);
        }
    }

    public static class PotionReward extends QuestReward {
        private final AbstractPotion potion;
        private final TextureRegion img;

        public PotionReward(AbstractPotion p) {
            super(String.format(TEXT[1], p.name));
            this.potion = p;
            this.img = TexLoader.getTextureAsAtlasRegion(makeUIPath("potion_reward.png"));
        }

        @Override
        public TextureRegion icon() {
            return img;
        }

        @Override
        public void addTooltip(List<PowerTip> tips) {
            tips.addAll(potion.tips);
        }

        @Override
        public void obtainRewardItem() {
            AbstractDungeon.combatRewardScreen.rewards.add(0, new RewardItem(potion));
            AbstractDungeon.combatRewardScreen.positionRewards();
        }

        @Override
        public void obtainInstant() {
            if (AbstractDungeon.player.hasRelic(Sozu.ID)) {
                AbstractDungeon.player.getRelic(Sozu.ID).flash();
                return;
            }
            Wiz.p().obtainPotion(this.potion);
        }

        @Override
        protected String saveParam() {
            return potion.ID;
        }
    }

    private static class RewardLoader {
        public final String key;
        public final Function<String, ? extends QuestReward> loader;

        public <T extends QuestReward> RewardLoader(Class<T> type, Function<String, T> loader) {
            this.key = type.getSimpleName();
            this.loader = loader;
        }
    }

    public static class QuestRewardSave {
        public String type;
        public String param;

        public QuestRewardSave() {

        }

        public QuestRewardSave(String type, String param) {
            this.type = type;
            this.param = param;
        }
    }
}
