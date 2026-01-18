package spireQuests.quests.indi_keurodz;

import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;
import spireQuests.quests.indi_keurodz.modifiers.EternalModifier;
import spireQuests.quests.indi_keurodz.modifiers.PerishableModifier;
import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;

public class QuestIcons {

    public static class EternalIcon extends AbstractCustomIcon {
        public static final String ID = Anniv8Mod.makeID("Eternal");
        private static EternalIcon singleton;
        public EternalIcon() { super(ID, EternalModifier.icon); }
        public static EternalIcon get() { if (singleton == null) singleton = new EternalIcon(); return singleton; }
    }

    public static class PerishableIcon extends AbstractCustomIcon {
        public static final String ID = Anniv8Mod.makeID("Perishable");
        private static PerishableIcon singleton;
        public PerishableIcon() { super(ID, PerishableModifier.icon); }
        public static PerishableIcon get() { if (singleton == null) singleton = new PerishableIcon(); return singleton; }
    }

    public static class RentalIcon extends AbstractCustomIcon {
        public static final String ID = Anniv8Mod.makeID("Rental");
        private static RentalIcon singleton;
        public RentalIcon() { super(ID, RentalModifier.icon); }
        public static RentalIcon get() { if (singleton == null) singleton = new RentalIcon(); return singleton; }
    }


    private static abstract class BossBlindIcon extends AbstractCustomIcon {
        public BossBlindIcon(String name, BossBlind blind) {
            super(Anniv8Mod.makeID(name), blind.frames.get(0));
        }
    }

    public static class ArmIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Arm");
        private static ArmIcon singleton;
        public ArmIcon() { super("Arm", BossBlind.Arm); }
        public static ArmIcon get() { if (singleton == null) singleton = new ArmIcon(); return singleton; }
    }

    public static class ClubIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Club");
        private static ClubIcon singleton;
        public ClubIcon() { super("Club", BossBlind.Club); }
        public static ClubIcon get() { if (singleton == null) singleton = new ClubIcon(); return singleton; }
    }

    public static class EyeIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Eye");
        private static EyeIcon singleton;
        public EyeIcon() { super("Eye", BossBlind.Eye); }
        public static EyeIcon get() { if (singleton == null) singleton = new EyeIcon(); return singleton; }
    }

    public static class FishIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Fish");
        private static FishIcon singleton;
        public FishIcon() { super("Fish", BossBlind.Fish); }
        public static FishIcon get() { if (singleton == null) singleton = new FishIcon(); return singleton; }
    }

    public static class FlintIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Flint");
        private static FlintIcon singleton;
        public FlintIcon() { super("Flint", BossBlind.Flint); }
        public static FlintIcon get() { if (singleton == null) singleton = new FlintIcon(); return singleton; }
    }

    public static class GoadIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Goad");
        private static GoadIcon singleton;
        public GoadIcon() { super("Goad", BossBlind.Goad); }
        public static GoadIcon get() { if (singleton == null) singleton = new GoadIcon(); return singleton; }
    }

    public static class HeadIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Head");
        private static HeadIcon singleton;
        public HeadIcon() { super("Head", BossBlind.Head); }
        public static HeadIcon get() { if (singleton == null) singleton = new HeadIcon(); return singleton; }
    }

    public static class HookIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Hook");
        private static HookIcon singleton;
        public HookIcon() { super("Hook", BossBlind.Hook); }
        public static HookIcon get() { if (singleton == null) singleton = new HookIcon(); return singleton; }
    }

    public static class HouseIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("House");
        private static HouseIcon singleton;
        public HouseIcon() { super("House", BossBlind.House); }
        public static HouseIcon get() { if (singleton == null) singleton = new HouseIcon(); return singleton; }
    }

    public static class ManacleIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Manacle");
        private static ManacleIcon singleton;
        public ManacleIcon() { super("Manacle", BossBlind.Manacle); }
        public static ManacleIcon get() { if (singleton == null) singleton = new ManacleIcon(); return singleton; }
    }

    public static class MarkIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Mark");
        private static MarkIcon singleton;
        public MarkIcon() { super("Mark", BossBlind.Mark); }
        public static MarkIcon get() { if (singleton == null) singleton = new MarkIcon(); return singleton; }
    }

    public static class MouthIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Mouth");
        private static MouthIcon singleton;
        public MouthIcon() { super("Mouth", BossBlind.Mouth); }
        public static MouthIcon get() { if (singleton == null) singleton = new MouthIcon(); return singleton; }
    }

    public static class NeedleIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Needle");
        private static NeedleIcon singleton;
        public NeedleIcon() { super("Needle", BossBlind.Needle); }
        public static NeedleIcon get() { if (singleton == null) singleton = new NeedleIcon(); return singleton; }
    }

    public static class OxIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Ox");
        private static OxIcon singleton;
        public OxIcon() { super("Ox", BossBlind.Ox); }
        public static OxIcon get() { if (singleton == null) singleton = new OxIcon(); return singleton; }
    }

    public static class PillarIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Pillar");
        private static PillarIcon singleton;
        public PillarIcon() { super("Pillar", BossBlind.Pillar); }
        public static PillarIcon get() { if (singleton == null) singleton = new PillarIcon(); return singleton; }
    }

    public static class PlantIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Plant");
        private static PlantIcon singleton;
        public PlantIcon() { super("Plant", BossBlind.Plant); }
        public static PlantIcon get() { if (singleton == null) singleton = new PlantIcon(); return singleton; }
    }

    public static class PsychicIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Psychic");
        private static PsychicIcon singleton;
        public PsychicIcon() { super("Psychic", BossBlind.Psychic); }
        public static PsychicIcon get() { if (singleton == null) singleton = new PsychicIcon(); return singleton; }
    }

    public static class SerpentIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Serpent");
        private static SerpentIcon singleton;
        public SerpentIcon() { super("Serpent", BossBlind.Serpent); }
        public static SerpentIcon get() { if (singleton == null) singleton = new SerpentIcon(); return singleton; }
    }

    public static class ToothIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Tooth");
        private static ToothIcon singleton;
        public ToothIcon() { super("Tooth", BossBlind.Tooth); }
        public static ToothIcon get() { if (singleton == null) singleton = new ToothIcon(); return singleton; }
    }

    public static class WallIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Wall");
        private static WallIcon singleton;
        public WallIcon() { super("Wall", BossBlind.Wall); }
        public static WallIcon get() { if (singleton == null) singleton = new WallIcon(); return singleton; }
    }

    public static class WaterIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Water");
        private static WaterIcon singleton;
        public WaterIcon() { super("Water", BossBlind.Water); }
        public static WaterIcon get() { if (singleton == null) singleton = new WaterIcon(); return singleton; }
    }

    public static class WheelIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Wheel");
        private static WheelIcon singleton;
        public WheelIcon() { super("Wheel", BossBlind.Wheel); }
        public static WheelIcon get() { if (singleton == null) singleton = new WheelIcon(); return singleton; }
    }

    public static class WindowIcon extends BossBlindIcon {
        public static final String ID = Anniv8Mod.makeID("Window");
        private static WindowIcon singleton;
        public WindowIcon() { super("Window", BossBlind.Window); }
        public static WindowIcon get() { if (singleton == null) singleton = new WindowIcon(); return singleton; }
    }
}
