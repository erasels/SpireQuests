package spireQuests.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowRoom;
import spireQuests.ui.QuestBoardProp;

public class NeowPatch {
    @SpirePatch(
            clz = NeowRoom.class,
            method = "render",
            paramtypez = SpriteBatch.class
    )
    public static class PostRender {
        @SpirePostfixPatch()
        public static void Render(NeowRoom original, SpriteBatch sb) {
            if (QuestBoardProp.questBoardProp != null) {
                QuestBoardProp.questBoardProp.render(sb);
            }
        }
    }

    @SpirePatch(
            clz = NeowRoom.class,
            method = "update"
    )
    public static class PostUpdate {
        @SpirePostfixPatch()
        public static void Update() {
            if (QuestBoardProp.questBoardProp != null) {
                QuestBoardProp.questBoardProp.update();
            }
        }
    }

    @SpirePatch(clz = NeowRoom.class, method = SpirePatch.CONSTRUCTOR)
    public static class PostConstructor {
        @SpirePostfixPatch
        public static void Constructor() {
            QuestBoardProp.questBoardProp = new QuestBoardProp((float) Settings.WIDTH * 0.5F - 425.0F * Settings.xScale, AbstractDungeon.floorY + 189.0F * Settings.yScale, true);
        }
    }
}