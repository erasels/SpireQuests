package spireQuests.ui;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import spireQuests.questStats.QuestStatManager;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestGenerator;
import spireQuests.quests.QuestManager;
import spireQuests.util.TexLoader;

import java.util.ArrayList;

import static spireQuests.Anniv8Mod.makeUIPath;

public class QuestBoardProp {
    public final float drawX;
    public final float drawY;
    public Hitbox hb;
    public ArrayList<AbstractQuest> quests;
    protected static final String questBoardPropImagePath = makeUIPath("bulletin_board.png");
    private final Texture sprite;
    public int numQuestsPickable;

    public static QuestBoardProp questBoardProp;

    public QuestBoardProp(float drawX, float drawY, boolean fromNeow) {
        this.drawX = drawX;
        this.drawY = drawY;
        this.quests = QuestGenerator.generateRandomQuests(fromNeow);
        // See comments in QuestGenerator for why the Neow case has special handling
        numQuestsPickable = fromNeow ? Math.max(2 - QuestManager.quests().size(), 0) : 2;
        this.sprite = TexLoader.getTexture(questBoardPropImagePath);
        this.hb = new Hitbox(sprite.getWidth() * Settings.xScale, sprite.getHeight() * Settings.yScale);
        this.hb.move(drawX + ((float) sprite.getWidth() / 2) * Settings.xScale, drawY + ((float) sprite.getHeight() / 2) * Settings.yScale);

        for (AbstractQuest q : this.quests){
            QuestStatManager.markSeen(q.id);
        }
    }

    public void update() {
        this.hb.update();
        if ((this.hb.hovered && InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) && !AbstractDungeon.isScreenUp && !AbstractDungeon.isFadingOut && !AbstractDungeon.player.viewingRelics) {
            QuestBoardScreen.init(this, quests);
            BaseMod.openCustomScreen(QuestBoardScreen.Enum.QUEST_BOARD);
            this.hb.hovered = false;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);

        // Reduce larger image down to 350px width and preserve aspect ratio
        float targetWidth = 350f * Settings.xScale;
        float scale = targetWidth / (sprite.getWidth() * Settings.xScale);

        float w = sprite.getWidth()  * Settings.xScale * scale;
        float h = sprite.getHeight() * Settings.yScale * scale;

        sb.draw(sprite, drawX, drawY, w, h);

        if (hb.hovered) {
            sb.setBlendFunction(770, 1);
            sb.setColor(Color.GOLD);
            sb.draw(sprite, drawX, drawY, w, h);
            sb.setBlendFunction(770, 771);
        }

        hb.render(sb);
    }
}
