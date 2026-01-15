
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class WindowIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Window");
    private static WindowIcon singleton;

    public WindowIcon() {
        super(ID, BossBlind.Window.frames.get(0));
    }

    public static WindowIcon get() {
        if (singleton == null) {
            singleton = new WindowIcon();
        }
        return singleton;
    }
}
