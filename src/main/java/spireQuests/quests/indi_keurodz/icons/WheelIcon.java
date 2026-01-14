
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class WheelIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Wheel");
    private static WheelIcon singleton;

    public WheelIcon() {
        super(ID, BossBlind.Wheel.frames.get(0));
    }

    public static WheelIcon get() {
        if (singleton == null) {
            singleton = new WheelIcon();
        }
        return singleton;
    }
}
