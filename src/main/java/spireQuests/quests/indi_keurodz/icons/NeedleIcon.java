
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class NeedleIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Needle");
    private static NeedleIcon singleton;

    public NeedleIcon() {
        super(ID, BossBlind.Needle.frames.get(0));
    }

    public static NeedleIcon get() {
        if (singleton == null) {
            singleton = new NeedleIcon();
        }
        return singleton;
    }
}
