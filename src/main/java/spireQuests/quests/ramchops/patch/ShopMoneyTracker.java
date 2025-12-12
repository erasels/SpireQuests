package spireQuests.quests.ramchops.patch;

import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;

import static spireQuests.util.Wiz.curRoom;

public class ShopMoneyTracker{

    @SpirePatch(
            clz = AbstractRoom.class,
            method = SpirePatch.CLASS
    )
    public static class MoneySpentField{
        public static SpireField<Integer> moneySpentInShop = new SpireField<>(() -> 0);
    }

    @SpirePatch2(
            clz = AbstractPlayer.class,
            method = "loseGold",
            paramtypez = int.class)
    public static class SpendGoldPatch{
        @SpirePrefixPatch
        public static void LoseGoldPatch(AbstractPlayer __instance, int goldAmount){

            if (curRoom() instanceof ShopRoom) {

                int money = getMoneySpentInRoom(curRoom());
                setMoneySpentInRoom(curRoom(), money + goldAmount);

            }

        }
    }

    public static int getMoneySpentInRoom(AbstractRoom r){
        return MoneySpentField.moneySpentInShop.get(r);
    }

    public static void setMoneySpentInRoom(AbstractRoom r, int money){
        MoneySpentField.moneySpentInShop.set(r, money);
    }
}
