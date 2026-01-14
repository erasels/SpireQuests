
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class ToothIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Tooth");
    private static ToothIcon singleton;

    public ToothIcon() {
        super(ID, BossBlind.Tooth.frames.get(0));
    }

    public static ToothIcon get() {
        if (singleton == null) {
            singleton = new ToothIcon();
        }
        return singleton;
    }
}
