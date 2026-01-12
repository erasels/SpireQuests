
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class HookIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Hook");
    private static HookIcon singleton;

    public HookIcon() {
        super(ID, BossBlind.Hook.frames.get(0));
    }

    public static HookIcon get() {
        if (singleton == null) {
            singleton = new HookIcon();
        }
        return singleton;
    }
}
