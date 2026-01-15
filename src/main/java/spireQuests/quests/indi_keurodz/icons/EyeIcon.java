package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class EyeIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Eye");
    private static EyeIcon singleton;

    public EyeIcon() {
        super(ID, BossBlind.Eye.frames.get(0));
    }

    public static EyeIcon get() {
        if (singleton == null) {
            singleton = new EyeIcon();
        }
        return singleton;
    }
}
