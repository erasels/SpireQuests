package spireQuests.quests.dayvig;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.JAX;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.EventRoom;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.dayvig.relics.MutagenBlood;

public class AddictQuest extends AbstractQuest {

    public Tracker addictTracker;
    public Tracker eventReplaceTracker;
    public Tracker jaxTracker;
    private boolean tookJax = false;
    public boolean hasReplacedEvent = false;

    public AddictQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        eventReplaceTracker = new TriggerTracker<>(QuestTriggers.ENTER_ROOM, 1)
                .triggerCondition(this::enteredEventRoom)
                .hide()
                .add(this);

        addictTracker = new TriggerTracker<>(QuestTriggers.COMBAT_END, 5)
                .triggerCondition((x) -> tookJax)
                .setResetTrigger(QuestTriggers.COMBAT_END, (x) -> !tookJax)
                .add(this);

        jaxTracker = new TriggerEvent<>(QuestTriggers.PLAY_CARD, this::playedJax)
                .add(this);

        addReward(new QuestReward.RelicReward(new MutagenBlood()));
    }

    private Boolean playedJax(AbstractCard card) {
       if (card.cardID.equals(JAX.ID)){
            tookJax = true;
            return true;
       }
       return false;
    }

    private boolean enteredEventRoom(MapRoomNode node){
        tookJax = false;
        return node.getRoom() instanceof EventRoom;
    }

    @Override
    public void refreshState(){
        super.refreshState();
        hasReplacedEvent = eventReplaceTracker.isComplete();
    }
}
