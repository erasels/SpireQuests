package spireQuests.quests.modargo;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.relics.MulticlassEmblem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MulticlassQuest extends AbstractQuest {
    public AbstractPlayer.PlayerClass playerClass;

    public MulticlassQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.ADD_CARD, 3)
                .triggerCondition(c -> c.color == getCardColor(this.playerClass))
                .add(this);
        new PlayerClassTracker(null).add(this);

        addReward(new QuestReward.RelicReward(new MulticlassEmblem()));

        AbstractPlayer.PlayerClass playerClass = null;
        if (CardCrawlGame.isInARun()) {
            Random rng = new Random(Settings.seed + 6701L);
            // Characters with unusual card pools are excluded because we can't handle them properly
            List<String> excludedCharacters = Arrays.asList(AbstractDungeon.player.chosenClass.name(), "THE_PACKMASTER", "THE_SISTERS", "Librarian", "THE_RAINBOW");
            List<AbstractPlayer.PlayerClass> playerClasses = CardCrawlGame.characterManager.getAllCharacters().stream()
                    .map(c -> c.chosenClass)
                    .filter(pc -> !excludedCharacters.contains(pc.name()))
                    .collect(Collectors.toList());
            playerClass = playerClasses.get(rng.random(playerClasses.size() - 1));
        }
        setPlayerClass(playerClass);
    }

    @Override
    public String getDescription() {
        return this.playerClass == null ? questStrings.EXTRA_TEXT[2] : String.format(questStrings.EXTRA_TEXT[3], FontHelper.colorString(getCharacter(this.playerClass).getTitle(this.playerClass), "y"));
    }

    @Override
    public boolean canSpawn() {
        return !AbstractDungeon.player.hasRelic(PrismaticShard.ID);
    }

    @Override
    public void loadSave(String[] questData, QuestReward.QuestRewardSave[] questRewardSaves) {
        super.loadSave(questData, questRewardSaves);
        AbstractPlayer.PlayerClass playerClass = this.getPlayerClassTracker().playerClass;
        this.setPlayerClass(playerClass);
    }

    public void setPlayerClass(AbstractPlayer.PlayerClass playerClass) {
        this.playerClass = playerClass;
        this.name = this.playerClass == null ? questStrings.EXTRA_TEXT[0] : String.format(questStrings.EXTRA_TEXT[1], getCharacter(this.playerClass).getTitle(this.playerClass));
        this.getPlayerClassTracker().playerClass = playerClass;
        MulticlassEmblem relic = new MulticlassEmblem();
        relic.setPlayerClass(this.playerClass);
        this.questRewards.set(0, new QuestReward.RelicReward(relic));
    }

    private PlayerClassTracker getPlayerClassTracker() {
        return (PlayerClassTracker)this.trackers.stream().filter(t -> t instanceof PlayerClassTracker).findFirst().orElseThrow(() -> new RuntimeException("Could not find player class tracker"));
    }

    public static AbstractPlayer getCharacter(AbstractPlayer.PlayerClass playerClass) {
        return playerClass == null ? null : CardCrawlGame.characterManager.getCharacter(playerClass);
    }

    public static AbstractCard.CardColor getCardColor(AbstractPlayer.PlayerClass playerClass) {
        AbstractPlayer character = getCharacter(playerClass);
        return character == null ? null : character.getCardColor();
    }

    // We hijack the tracker system to store a value that saves and loads together with the quest
    public static class PlayerClassTracker extends Tracker {
        private AbstractPlayer.PlayerClass playerClass;

        public PlayerClassTracker(AbstractPlayer.PlayerClass playerClass) {
            this.playerClass = playerClass;
            this.hide();
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public String progressString() {
            return "";
        }

        @Override
        public String saveData() {
            return this.playerClass.name();
        }

        @Override
        public void loadData(String data) {
            this.playerClass = AbstractPlayer.PlayerClass.valueOf(data);
        }
    }
}
