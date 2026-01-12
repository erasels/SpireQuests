
package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class PsychicIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Psychic");
    private static PsychicIcon singleton;

    public PsychicIcon() {
        super(ID, BossBlind.Psychic.frames.get(0));
    }

    public static PsychicIcon get() {
        if (singleton == null) {
            singleton = new PsychicIcon();
        }
        return singleton;
    }
}
