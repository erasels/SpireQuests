
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class MouthIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Mouth");
    private static MouthIcon singleton;

    public MouthIcon() {
        super(ID, BossBlind.Mouth.frames.get(0));
    }

    public static MouthIcon get() {
        if (singleton == null) {
            singleton = new MouthIcon();
        }
        return singleton;
    }
}
