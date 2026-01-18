package spireQuests.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import spireQuests.Anniv8Mod;
import spireQuests.ui.QuestBoardProp;
import spireQuests.util.ActUtil;

public class ShopPatch {
    @SpirePatch(
            clz = ShopRoom.class,
            method = "render",
            paramtypez = SpriteBatch.class
    )
    public static class PostRender {
        @SpirePostfixPatch()
        public static void Render(ShopRoom original, SpriteBatch sb) {
            if (QuestBoardProp.questBoardProp != null) {
                QuestBoardProp.questBoardProp.render(sb);
            }
        }
    }

    @SpirePatch(clz = ShopRoom.class, method = "update")
    public static class PostUpdate {
        @SpirePostfixPatch()
        public static void Update() {
            if (QuestBoardProp.questBoardProp != null) {
                QuestBoardProp.questBoardProp.update();
            }
        }
    }

    @SpirePatch(clz = ShopRoom.class, method = "onPlayerEntry")
    public static class PostPlayerEntry {
        @SpirePostfixPatch
        public static void PlayerEntry() {
            if (ActUtil.getRealActNum() >= 4) return;
            QuestBoardProp.questBoardProp = new QuestBoardProp((float) Settings.WIDTH * 0.5F - 350.0F * Settings.xScale, AbstractDungeon.floorY + 10.0F * Settings.yScale, false);
        }
    }
}