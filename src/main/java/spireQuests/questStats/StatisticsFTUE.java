package spireQuests.questStats;

import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton.ClickResult;
import com.megacrit.cardcrawl.ui.buttons.GotItButton;

import spireQuests.Anniv8Mod;
import spireQuests.util.TexLoader;


// Most code straight up copy and pasted from Packmaster FTUE.
public class StatisticsFTUE {
    
    private static final String ID = Anniv8Mod.makeID(StatisticsFTUE.class.getSimpleName());
    private GotItButton enableButton;
    private Texture TROPHY_ARRAY = TexLoader.getTexture(Anniv8Mod.makeUIPath("stats/medal_array.png"));
    private Texture FTUE = ImageMaster.FTUE;
    private float xPos, yPos;
    
    public boolean isDone = false;
    private float ftueCenterX;
    private float ftueCenterY;
    private static UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);

    public StatisticsFTUE(float x, float y) {
        this.xPos = x;
        this.yPos = y;
        this.ftueCenterX = x + ((FTUE.getWidth() * Settings.scale) / 2.0F);
        this.ftueCenterY = y;
        enableButton = new GotItButton(ftueCenterX, ftueCenterY);
    }

    public void update() {
        enableButton.update();
        if (enableButton.hb.clicked || CInputActionSet.proceed.isJustPressed()) {
            CInputActionSet.proceed.unpress();
            CardCrawlGame.sound.play("DECK_OPEN");
            isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);

        // Draw FTUE on right of xPos
        sb.draw(FTUE, xPos, yPos - ((FTUE.getHeight() * Settings.scale) / 2), FTUE.getWidth() * Settings.scale, FTUE.getHeight() * Settings.scale);

        sb.setColor(new Color(1f, 1f, 1f, 0.7f + (MathUtils.cosDeg(System.currentTimeMillis() / 2f % 360) + 1.25f) / 5f));
        enableButton.render(sb);

        FontHelper.renderFontLeftTopAligned(
            sb,
            FontHelper.topPanelInfoFont,
            uiStrings.TEXT[0],
            ftueCenterX - 190f * Settings.scale,
            ftueCenterY + 80f * Settings.scale,
            Settings.GOLD_COLOR
        );

        FontHelper.renderSmartText(
            sb,
            FontHelper.tipBodyFont,
            uiStrings.TEXT[1],
            ftueCenterX - 250f * Settings.scale,
            ftueCenterY + 20f * Settings.scale,
            450f * Settings.scale,
            26f * Settings.scale,
            Settings.CREAM_COLOR
        );

        FontHelper.renderFontRightTopAligned(
            sb,
            FontHelper.topPanelInfoFont,
            uiStrings.TEXT[2],
            ftueCenterX + 194f * Settings.scale,
            ftueCenterY - 150f * Settings.scale,
            Settings.GOLD_COLOR
        );

        // Render Textbox Highlight
        List<MenuButton> statsButtons = CardCrawlGame.mainMenuScreen.buttons.stream().filter((button) -> button.result == ClickResult.STAT).collect(Collectors.toList());
        for (MenuButton b : statsButtons) {
            b.render(sb);
            b.hb.render(sb);
        }

        //Render texture
        if(TROPHY_ARRAY != null) {
            sb.setColor(Color.WHITE);
            sb.draw(
                TROPHY_ARRAY,
                xPos - (TROPHY_ARRAY.getWidth() * Settings.scale),
                yPos - ((TROPHY_ARRAY.getHeight() * Settings.scale) / 2f),
                TROPHY_ARRAY.getWidth() * Settings.scale,
                TROPHY_ARRAY.getHeight() * Settings.scale
            );
        }


        if (Settings.isControllerMode) {
            sb.setColor(Color.WHITE);
            sb.draw(
                CInputActionSet.proceed.getKeyImg(),
                enableButton.hb.cX - 32f + 130f * Settings.scale,
                enableButton.hb.cY - 32f + 2f * Settings.scale,
                32f,
                32f,
                64,
                64,
                Settings.scale,
                Settings.scale,
                0f,
                0,
                0,
                64,
                64,
                false,
                false
            );
        }
    }
}
