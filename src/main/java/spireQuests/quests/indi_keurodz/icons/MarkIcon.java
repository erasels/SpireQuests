
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class MarkIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Mark");
    private static MarkIcon singleton;

    public MarkIcon() {
        super(ID, BossBlind.Mark.frames.get(0));
    }

    public static MarkIcon get() {
        if (singleton == null) {
            singleton = new MarkIcon();
        }
        return singleton;
    }
}
