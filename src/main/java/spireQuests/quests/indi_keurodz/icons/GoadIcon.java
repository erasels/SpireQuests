package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class GoadIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Goad");
    private static GoadIcon singleton;

    public GoadIcon() {
        super(ID, BossBlind.Goad.frames.get(0));
    }

    public static GoadIcon get() {
        if (singleton == null) {
            singleton = new GoadIcon();
        }
        return singleton;
    }
}
