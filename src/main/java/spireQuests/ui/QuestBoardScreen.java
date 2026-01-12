package spireQuests.ui;

import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;

import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
import java.util.List;

import static spireQuests.Anniv8Mod.makeUIPath;

public class QuestBoardScreen extends CustomScreen {
    public static final String ID = spireQuests.Anniv8Mod.makeID("QuestBoard");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    protected static float boardY;
    protected static ArrayList<QuestBoardQuest> questBoardQuests = new ArrayList<>();
    protected static final String questBoardImagePath = makeUIPath("quest_board.png");
    protected static Texture questBoardImg;
    public static QuestBoardProp parentProp;

    public static class Enum {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen QUEST_BOARD;
    }

    public QuestBoardScreen() {
        questBoardImg = TexLoader.getTexture(questBoardImagePath);
    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return Enum.QUEST_BOARD;
    }

    private void open() {
        reopen();
    }

    @Override
    public void reopen() {
        boardY = Settings.HEIGHT;
        AbstractDungeon.player.releaseCard();
        AbstractDungeon.screen = Enum.QUEST_BOARD;
        AbstractDungeon.overlayMenu.showBlackScreen();
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.cancelButton.show(TEXT[0]);

        if (MathUtils.randomBoolean()) {
            CardCrawlGame.sound.play("MAP_OPEN", 0.1f);
        } else {
            CardCrawlGame.sound.play("MAP_OPEN_2", 0.1f);
        }
    }

    @Override
    public void openingSettings() {
    }

    @Override
    public void close() {
        genericScreenOverlayReset();
        AbstractDungeon.overlayMenu.cancelButton.hide();
    }

    @Override
    public void update() {
        updateBoard();
        for (QuestBoardQuest questBoardQuest : questBoardQuests) {
            questBoardQuest.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.draw(questBoardImg, 0.0F, boardY, Settings.WIDTH, Settings.HEIGHT);
        for (QuestBoardQuest questBoardQuest : questBoardQuests) {
            questBoardQuest.render(sb, boardY);
        }
        FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, TEXT[2] + parentProp.numQuestsPickable + TEXT[3], (float) Settings.WIDTH / 2, AbstractDungeon.floorY - 250.0F * Settings.yScale + boardY, Color.WHITE, 1.2f);
        renderQuestLimitWarning(sb);
    }

    @Override
    public boolean allowOpenDeck() {
        return true;
    }

    @Override
    public boolean allowOpenMap() {
        return true;
    }

    protected static void updateBoard() {
        if (boardY != 0.0F) {
            boardY = MathUtils.lerp(boardY, Settings.HEIGHT / 2.0F - 540.0F * Settings.yScale, Gdx.graphics.getDeltaTime() * 5.0F);
            if (boardY < 0.5F) {
                boardY = 0.0F;
            }
        }
    }

    public static void init(QuestBoardProp prop, ArrayList<AbstractQuest> quests) {
        parentProp = prop;
        questBoardQuests.clear();
        float xIncrease = 550.0F * Settings.xScale;
        float x = (float) Settings.WIDTH / 10 - 45.0F * Settings.xScale;
        float y = (float) Settings.HEIGHT / 2;
        for (AbstractQuest quest : quests) {
            QuestBoardQuest questBoardQuest = new QuestBoardQuest(quest, x, y);
            questBoardQuests.add(questBoardQuest);
            x += xIncrease;
        }
    }

    private void renderQuestLimitWarning(SpriteBatch sb) {
        List<AbstractQuest> activeQuests = QuestManager.quests();
        if (activeQuests == null || activeQuests.size() < QuestManager.QUEST_LIMIT) {
            return;
        }

        float warningY = AbstractDungeon.floorY - 300F * Settings.yScale + boardY;
        FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, TEXT[5], (float) Settings.WIDTH / 2, warningY, Settings.RED_TEXT_COLOR, 0.7F);
    }
}