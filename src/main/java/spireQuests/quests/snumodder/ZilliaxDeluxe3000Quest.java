package spireQuests.quests.snumodder;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.snumodder.cards.*;
import spireQuests.quests.snumodder.monsters.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;

public class ZilliaxDeluxe3000Quest extends AbstractQuest {
    private static final String ID = makeID(ZilliaxDeluxe3000Quest.class.getSimpleName());

    public ZilliaxDeluxe3000Quest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);
        new TriggeredUpdateTracker<>(QuestTriggers.DECK_CHANGE,0,2, () -> {
            return (int) AbstractDungeon.player.masterDeck.group.stream()
                    .filter(c -> c instanceof AbstractModuleCard)
                    .map(c -> ((AbstractModuleCard) c).getModule())
                    .distinct()
                    .count();
        }).add(this);

        addReward(new QuestReward.CardReward(new ZilliaxDeluxe3000()));

        new TriggerEvent<>(QuestTriggers.BEFORE_COMBAT_START, t -> {
            ArrayList<ZilliaxModules> candidates = new ArrayList<>();
            for (ZilliaxModules m : ZilliaxModules.values()) {
                if (!this.getSpawnedModulesTracker().spawnedModulesSave.contains(m)) {
                    candidates.add(m);
                }
            }
            ZilliaxModules chosen;
            if (candidates.isEmpty()) {
                chosen = ZilliaxModules.values()[AbstractQuest.rng.random(ZilliaxModules.values().length - 1)];
            } else {
                chosen = candidates.get(AbstractQuest.rng.random(candidates.size() - 1));
            }
            this.getSpawnedModulesTracker().spawnedModulesSave.add(chosen);
            if (chosen == ZilliaxModules.TWIN) {
                spawn(new TwinModuleMonster(-450f, 450f, true));
                spawn(new TwinModuleMonster(-440f, 200f, false));
            } else if (chosen.monsterSupplier != null) {
                spawn(chosen.monsterSupplier.get());
            }
            if (chosen.rewardSupplier != null)
                setModuleReward(chosen.rewardSupplier.get());

        }).add(this);

        new SpawnedModulesTracker().add(this);
    }

    private void spawn(AbstractSQMonster m) {
        AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(m, false));
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                m.usePreBattleAction();
                isDone = true;
            }
        });
    }

    private void setModuleReward(AbstractCard module) {
        RewardItem item = new RewardItem();
        item.cards.clear();
        item.cards.add(module);
        AbstractDungeon.getCurrRoom().addCardReward(item);
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum < 3;
    }

    @Override
    public void onComplete() {
        ArrayList<AbstractModuleCard> modules = new ArrayList<>();
        AbstractModuleCard first = null, second = null;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c instanceof AbstractModuleCard) {
                modules.add((AbstractModuleCard) c);
            }
        }
        for (AbstractModuleCard m : modules) {
            if (first == null) {
                first = m;
            } else if (m.getModule() != first.getModule()) {
                second = m;
                break;
            }
        }
        questRewards.clear();
        questRewards.add(new QuestReward.CardReward(new ZilliaxDeluxe3000(first, second)));
        for (AbstractModuleCard m : modules) {
            AbstractDungeon.player.masterDeck.removeCard(m);
        }
    }

    private SpawnedModulesTracker getSpawnedModulesTracker() {
        return (SpawnedModulesTracker) this.trackers.stream().filter(t -> t instanceof SpawnedModulesTracker).findFirst().orElse(null);
    }

    public static class SpawnedModulesTracker extends Tracker {
        private ArrayList<ZilliaxModules> spawnedModulesSave;

        public SpawnedModulesTracker() {
            this.spawnedModulesSave = new ArrayList<>();
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
            if (spawnedModulesSave.isEmpty()) {
                return "";
            }
            else if (spawnedModulesSave.size() <= 7) {
                return spawnedModulesSave.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(","));
            } else {
                return "ALL," + spawnedModulesSave.get(spawnedModulesSave.size() - 1).name();
            }
        }

        @Override
        public void loadData(String data) {
            spawnedModulesSave.clear();
            if (data == null || data.isEmpty()) return;
            String[] parts = data.split(",");
            if ("ALL".equals(parts[0])) {
                spawnedModulesSave.addAll(Arrays.asList(ZilliaxModules.values()));
                if (parts.length > 1) {
                    ZilliaxModules last = ZilliaxModules.valueOf(parts[1]);
                    spawnedModulesSave.add(last);
                }
            } else {
                for (String name : parts) {
                    spawnedModulesSave.add(ZilliaxModules.valueOf(name));
                }
            }
        }
    }

    public enum ZilliaxModules {
        POWER(
                () -> new PowerModuleMonster(-450f, 300f),
                () -> new PowerModule()
        ),
        RECURSIVE(
                () -> new RecursiveModuleMonster(-450f, 300f),
                () -> new RecursiveModule()
        ),
        TWIN(
                null,
                () -> new TwinModule()
        ),
        PERFECT(
                () -> new PerfectModuleMonster(-450f, 300f),
                () -> new PerfectModule()
        ),
        TICKING(
                () -> new TickingModuleMonster(-450f, 300f),
                () -> new TickingModule()
        ),
        VIRUS(
                () -> new VirusModuleMonster(-450f, 300f),
                () -> new VirusModule()
        ),
        HAYWIRE(
                () -> new HaywireModuleMonster(-450f, 300f),
                () -> new HaywireModule()
        ),
        PYLON(
                () -> new PylonModuleMonster(-450f, 300f),
                () -> new PylonModule()
        );
        public final Supplier<AbstractSQMonster> monsterSupplier;
        public final Supplier<AbstractModuleCard> rewardSupplier;

        ZilliaxModules(Supplier<AbstractSQMonster> monsterSupplier,
                       Supplier<AbstractModuleCard> rewardSupplier) {
            this.monsterSupplier = monsterSupplier;
            this.rewardSupplier = rewardSupplier;
        }
    }

    @SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class ZilliaxRewardPatch {
        @SpirePostfixPatch
        public static void patch(CombatRewardScreen __instance) {
            if (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoom || AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite || AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss)) return;
            ZilliaxDeluxe3000Quest q = (ZilliaxDeluxe3000Quest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id))
                    .findAny()
                    .orElse(null);
            if (q == null) return;

            boolean hasModuleReward = __instance.rewards.stream()
                    .anyMatch(r -> r.type == RewardItem.RewardType.CARD &&
                            r.cards.stream().anyMatch(c -> c instanceof AbstractModuleCard));

            if (!hasModuleReward) {
                ZilliaxDeluxe3000Quest.ZilliaxModules module = q.getSpawnedModulesTracker().spawnedModulesSave.get(q.getSpawnedModulesTracker().spawnedModulesSave.size() - 1);
                AbstractModuleCard card = module.rewardSupplier.get();
                RewardItem item = new RewardItem();
                item.cards.clear();
                item.cards.add(card);
                AbstractDungeon.combatRewardScreen.rewards.add(0, item);
                AbstractDungeon.combatRewardScreen.positionRewards();
            }
        }
    }
}
