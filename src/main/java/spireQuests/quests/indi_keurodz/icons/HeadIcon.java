
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class HeadIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Head");
    private static HeadIcon singleton;

    public HeadIcon() {
        super(ID, BossBlind.Head.frames.get(0));
    }

    public static HeadIcon get() {
        if (singleton == null) {
            singleton = new HeadIcon();
        }
        return singleton;
    }
}
