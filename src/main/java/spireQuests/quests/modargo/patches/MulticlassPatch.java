package spireQuests.quests.modargo.patches;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.modargo.MulticlassQuest;
import spireQuests.quests.modargo.relics.MulticlassEmblem;

import java.util.ArrayList;
import java.util.stream.Collectors;

@SpirePatch2(clz = AbstractDungeon.class, method = "getCard", paramtypez = {AbstractCard.CardRarity.class})
@SpirePatch2(clz = AbstractDungeon.class, method = "getCard", paramtypez = {AbstractCard.CardRarity.class, Random.class})
public class MulticlassPatch {
    @SpireInstrumentPatch
    public static ExprEditor multiclassRewards() {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(CardGroup.class.getName()) && m.getMethodName().equals("getRandomCard")) {
                    m.replace(String.format("{ $0 = %s.getAugmentedCardPool($0, rarity); $_ = $proceed($$); }", MulticlassPatch.class.getName()));
                }
            }
        };
    }

    public static CardGroup getAugmentedCardPool(CardGroup g, AbstractCard.CardRarity rarity) {
        AbstractCard.CardColor color = getMulticlassColor();
        if (color == null) {
            return g;
        }
        ArrayList<AbstractCard> cards = new ArrayList<>();
        cards.addAll(g.group);
        cards.addAll(CardLibrary.getAllCards().stream().filter(c -> c.color == color && c.rarity == rarity && !c.getClass().isAnnotationPresent(NoPools.class)).collect(Collectors.toList()));
        CardGroup group = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        group.group = cards;
        return group;
    }

    private static AbstractCard.CardColor getMulticlassColor() {
        AbstractRelic multiclassRelic = AbstractDungeon.player.getRelic(MulticlassEmblem.ID);
        AbstractQuest multiclassQuest = QuestManager.quests().stream().filter(q -> q instanceof MulticlassQuest).findFirst().orElse(null);
        AbstractPlayer.PlayerClass playerClass = multiclassRelic != null ? ((MulticlassEmblem)multiclassRelic).playerClass
                : multiclassQuest != null ? ((MulticlassQuest)multiclassQuest).playerClass
                : null;
        if (playerClass == null) {
            return null;
        }
        return MulticlassQuest.getCardColor(playerClass);
    }
}
