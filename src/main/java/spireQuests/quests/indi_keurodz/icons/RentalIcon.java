package spireQuests.quests.indi_keurodz.icons;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;

import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;

public class RentalIcon extends AbstractCustomIcon {
    public static final String ID = Anniv8Mod.makeID("Rental");
    private static RentalIcon singleton;

    public RentalIcon() {
        super(ID, RentalModifier.icon);
    }

    public static RentalIcon get() {
        if (singleton == null) {
            singleton = new RentalIcon();
        }
        return singleton;
    }
}
