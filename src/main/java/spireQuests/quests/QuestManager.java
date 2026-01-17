package spireQuests.quests;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import spireQuests.Anniv8Mod;
import spireQuests.cardmods.QuestboundMod;
import spireQuests.patches.QuestRunHistoryPatch;
import spireQuests.patches.QuestboundRelicsPatch;
import spireQuests.questStats.QuestStatManager;
import spireQuests.ui.QuestBoardScreen;
import spireQuests.util.RelicMiscUtil;
import spireQuests.util.Wiz;
import spireQuests.vfx.ShowCardandFakeObtainEffect;

import java.io.IOException;
import java.util.*;

import static spireQuests.Anniv8Mod.*;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = SpirePatch.CLASS
)
public class QuestManager {
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(makeID("QuestManager")).TEXT;

    public static final int QUEST_LIMIT = 5;

    private static final Map<String, AbstractQuest> quests = new HashMap<>();
    private static final EnumMap<AbstractQuest.QuestDifficulty, ArrayList<AbstractQuest>> questsByDifficulty = new EnumMap<>(AbstractQuest.QuestDifficulty.class);

    public static SpireField<List<AbstractQuest>> currentQuests = new SpireField<>(ArrayList::new);

    private static SpireConfig filterConfig;

    //Called once in postInitialize
    public static void initialize() {

        filterConfig = makeFilterConfig();

        for (AbstractQuest.QuestDifficulty diff : AbstractQuest.QuestDifficulty.values()) {
            questsByDifficulty.put(diff, new ArrayList<>());
        }

        new AutoAdd(modID)
                .packageFilter(Anniv8Mod.class)
                .any(AbstractQuest.class, QuestManager::registerQuest);
        Statistics.logStatistics(QuestManager.getAllQuests());

        BaseMod.addSaveField(makeID("QuestManager"), new CustomSavable<QuestSave>() {
            @Override
            public QuestSave onSave() {
                return new QuestSave(quests());
            }

            @Override
            public void onLoad(QuestSave questSave) {
                if (questSave == null) return;
                for (int i = 0; i < questSave.questIds.length; ++i) {
                    AbstractQuest quest = getQuest(questSave.questIds[i]);
                    if (quest == null) continue;
                    quest.refreshState();
                    quest.loadSave(questSave.questData[i], questSave.questRewards[i]);
                    if(!quest.complete() && !quest.fail()) {
                        quest.questboundRelics = new ArrayList<>();
                        if (questSave.questRelicIndex[i] != null) {
                            for(int y = 0; y < questSave.questRelicIndex[i].length; ++y) {
                                AbstractRelic r = null;
                                try {
                                    r = Wiz.adp().relics.get(questSave.questRelicIndex[i][y]);
                                }
                                catch (ArrayIndexOutOfBoundsException e) {
                                    Anniv8Mod.logger.warn("Relic was not found for Quest ({})", quest.name);
                                }
                                if(r != null) {
                                    quest.questboundRelics.add(r);
                                    QuestboundRelicsPatch.QuestboundRelicFields.isQuestbound.set(r, quest);
                                    String questName = FontHelper.colorString(quest.name, "y");
                                    r.tips.add(new PowerTip(keywords.get("Questbound").PROPER_NAME, String.format(CardCrawlGame.languagePack.getUIString(makeID("Questbound")).TEXT[2],questName)));
                                }
                            }
                        }
                    }
                    currentQuests.get(AbstractDungeon.player).add(quest);
                }
            }
        });
    }

    private static void registerQuest(AutoAdd.Info info, AbstractQuest quest) {
        AbstractQuest q = quests.put(quest.id, quest);
        ArrayList<AbstractQuest> questOfDifficulty = questsByDifficulty.get(quest.difficulty);
        questOfDifficulty.add(quest);
        questsByDifficulty.put(quest.difficulty, questOfDifficulty);

        if (q != null) {
            throw new RuntimeException("Duplicate quest ID " + q.id + " for classes " + q.getClass().getName() + " and " + quest.getClass().getName());
        }
    }

    public static AbstractQuest getQuest(String id) {
        AbstractQuest quest = quests.get(id);
        if (quest == null) {
            Anniv8Mod.logger.error("Quest not found: " + id);
            return null;
        }
        return quest.makeCopy();
    }

    public static Collection<AbstractQuest> getAllQuests() {
        return new ArrayList<>(quests.values());
    }

    public static ArrayList<AbstractQuest> getQuestsByDifficulty(AbstractQuest.QuestDifficulty difficulty) {
        return questsByDifficulty.get(difficulty);
    }

    public static List<AbstractQuest> quests() {
        return currentQuests.get(AbstractDungeon.player);
    }


    public static <T> void triggerTrackers(Trigger<T> trigger) {
        if (AbstractDungeon.player == null) return;

        for (AbstractQuest quest : quests()) {
            quest.triggerTrackers(trigger);
        }
    }

    public static void startQuest(String id) {
        startQuest(getQuest(id));
    }

    public static void startQuest(AbstractQuest quest) {
        List<AbstractQuest> questList = quests();

        questList.add(quest);
        questList.sort(null);
        quest.onStart();
        if (quest.questboundCards != null) {
            quest.questboundCards.forEach(c -> {
                CardModifierManager.addModifier(c, new QuestboundMod(quest));
                if (questboundEnabled()) {
                    AbstractDungeon.effectList.add(new ShowCardandFakeObtainEffect(c.makeStatEquivalentCopy(), (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
                }
            });
        }
        if (quest.questboundRelics != null) {
            quest.questboundRelics.forEach(r -> {
                QuestboundRelicsPatch.QuestboundRelicFields.isQuestbound.set(r, quest);
                if(quest.removeQuestboundDuplicate) {
                    RelicMiscUtil.removeRelicFromPool(r);
                }
                String questName = FontHelper.colorString(quest.name, "y");
                r.instantObtain();
                r.tips.add(new PowerTip(keywords.get("Questbound").PROPER_NAME, String.format(CardCrawlGame.languagePack.getUIString(makeID("Questbound")).TEXT[2],questName)));
            });
        }
        QuestStatManager.markTaken(quest.id);
        List<List<String>> questPickupPerFloor = QuestRunHistoryPatch.questPickupPerFloorLog.get(AbstractDungeon.player);
        if (!questPickupPerFloor.isEmpty()) {
            questPickupPerFloor.get(questPickupPerFloor.size() - 1).add(quest.id);
        } else {
            Anniv8Mod.logger.error("questPickupPerFloor was empty, not adding quest to run history.");
        }
        List<List<String>> questCostPerFloor = QuestRunHistoryPatch.questCostPerFloorLog.get(AbstractDungeon.player);
        if (!questCostPerFloor.isEmpty()) {
            String costString = !Anniv8Mod.questsHaveCost() || quest.getCost() == 0 ? QuestRunHistoryPatch.NO_COST : quest.getCost() + (quest.usingGoldCost ? QuestRunHistoryPatch.GOLD : QuestRunHistoryPatch.HP);
            questCostPerFloor.get(questCostPerFloor.size() - 1).add(costString);
        } else {
            Anniv8Mod.logger.error("questCostPerFloor was empty, not adding quest to run history.");
        }
    }

    public static void completeQuest(AbstractQuest quest) {
        if (!quest.complete() && !quest.fail()) {
            Anniv8Mod.logger.warn("completeQuest called when quest is not complete/failed!");
            return;
        }

        ArrayList<AbstractRelic> toRemove = new ArrayList<>();

        for (AbstractRelic myRelic : Wiz.adp().relics){
            if(QuestboundRelicsPatch.QuestboundRelicFields.isQuestbound.get(myRelic) == quest){
                toRemove.add(myRelic);
            }
        }

        for (AbstractRelic qbr : toRemove){
            RelicMiscUtil.removeSpecificRelic(qbr);
        }
        
        if (quest.fail()) {
            quests().remove(quest);
            quest.onFail();

            QuestStatManager.markFailed(quest.id);
            List<List<String>> questFailurePerFloor = QuestRunHistoryPatch.questFailurePerFloorLog.get(AbstractDungeon.player);
            if (!questFailurePerFloor.isEmpty()) {
                questFailurePerFloor.get(questFailurePerFloor.size() - 1).add(quest.id);
            } else {
                Anniv8Mod.logger.error("questFailurePerFloor was empty, not adding quest to run history.");
            }
            return;
        }

        int complainCode = -1;
        if (AbstractDungeon.currMapNode == null) complainCode = 0;
        else if (AbstractDungeon.currMapNode.room == null) complainCode = 0;
        else if (AbstractDungeon.currMapNode.room.phase == AbstractRoom.RoomPhase.COMBAT) complainCode = 1;
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.COMBAT_REWARD && quest.rewardScreenOnly) complainCode = 2;
        if(complainCode > -1) {
            AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, TEXT[complainCode], true));
            return;
        }

        quests().remove(quest);
        quest.obtainRewards();
        QuestStatManager.markComplete(quest.id);
        List<List<String>> questCompletionPerFloor = QuestRunHistoryPatch.questCompletionPerFloorLog.get(AbstractDungeon.player);
        questCompletionPerFloor.get(questCompletionPerFloor.size() - 1).add(quest.id);
    }

    public static void failQuest(AbstractQuest quest) {
        quest.forceFail();
        quest.onFail();
        completeQuest(quest);

        QuestStatManager.markFailed(quest.id);
        List<List<String>> questFailurePerFloor = QuestRunHistoryPatch.questFailurePerFloorLog.get(AbstractDungeon.player);
        if (!questFailurePerFloor.isEmpty()) {
            questFailurePerFloor.get(questFailurePerFloor.size() - 1).add(quest.id);
        } else {
            Anniv8Mod.logger.error("questFailurePerFloor was empty, not adding quest to run history.");
        }
    }

    public void update() {
        if (AbstractDungeon.player == null) return;

        //remove failed quests?
        for (AbstractQuest quest : quests()) {
            quest.update();
        }

    }


    public void render(SpriteBatch sb) {
        if (AbstractDungeon.player == null) {
        }
        //quest ui
    }

    public static void failAllIncompleteActiveQuests() {
        for (AbstractQuest q : quests()) {
            if (!q.complete()) {
                q.forceFail();
            }
        }
    }

    public static boolean canObtainQuests() {
        return (QuestBoardScreen.parentProp.numQuestsPickable > 0) && (quests().size() < QUEST_LIMIT);
    }

    private static SpireConfig makeFilterConfig() {
        try {
            SpireConfig config = new SpireConfig(Anniv8Mod.modID, "filterConfig");
            config.load();
            return config;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean getFilterConfig(String questID) {
        if (filterConfig != null) {
            if (filterConfig.has(questID)) {
                return filterConfig.getBool(questID);
            }
            return true;
        }

        Anniv8Mod.logger.info("Error loading SpireQuest quest filters. Config not initialized?");
        return true;
    }

    public static void setFilterConfig(String questID, boolean questEnabled) {
        if (filterConfig != null) {
            filterConfig.setBool(questID, questEnabled);
            try {
                filterConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Anniv8Mod.logger.info("Error loading SpireQuest quest filters. Config not initialized?");
        }
    }
}
