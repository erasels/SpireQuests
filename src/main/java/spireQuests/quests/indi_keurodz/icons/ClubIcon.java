package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class ClubIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Club");
    private static ClubIcon singleton;

    public ClubIcon() {
        super(ID, BossBlind.Club.frames.get(0));
    }

    public static ClubIcon get() {
        if (singleton == null) {
            singleton = new ClubIcon();
        }
        return singleton;
    }
}
