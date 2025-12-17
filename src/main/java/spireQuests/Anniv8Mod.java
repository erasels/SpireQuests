package spireQuests;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CtClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.cardvars.SecondDamage;
import spireQuests.cardvars.SecondMagicNumber;
import spireQuests.commands.AddQuestCommand;
import spireQuests.commands.SpawnQuestCommand;
import spireQuests.patches.QuestRunHistoryPatch;
import spireQuests.questStats.QuestStatManager;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestGenerator;
import spireQuests.quests.QuestManager;
import spireQuests.quests.coda.potions.NuclearJuicePotion;
import spireQuests.quests.enbeon.monsters.WatcherEliteMonster;
import spireQuests.quests.gk.monsters.ICEliteMonster;
import spireQuests.quests.modargo.RealityTwistQuest;
import spireQuests.quests.modargo.monsters.DefectEliteMonster;
import spireQuests.quests.ramchops.EvilSentryQuest;
import spireQuests.quests.ramchops.monsters.EvilSentry;
import spireQuests.rewards.SingleCardReward;
import spireQuests.ui.FixedModLabeledToggleButton.FixedModLabeledToggleButton;
import spireQuests.ui.QuestBoardScreen;
import spireQuests.util.CompatUtil;
import spireQuests.util.QuestStringsUtils;
import spireQuests.util.TexLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"unused"})
@SpireInitializer
public class Anniv8Mod implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber,
        AddAudioSubscriber,
        PostDungeonInitializeSubscriber,
        StartGameSubscriber,
        PostRenderSubscriber,
        PostDeathSubscriber {

    public static final Logger logger = LogManager.getLogger("SpireQuests");

    public static Settings.GameLanguage[] SupportedLanguages = {
            Settings.GameLanguage.ENG
    };

    public static Anniv8Mod thismod;
    public static final String HARD_MODE_CONFIG = "hardModeConfig";
    public static boolean hardModeConfig = false;
    public static SpireConfig modConfig = null;
    public static final String QUESTBOUND_CONFIG = "questboundConfig";
    public static boolean questboundConfig = true;
    public static final String TROPHY_TOOLTIP_CONFIG = "trophyTooltipsConfig";
    public static boolean trophyTooltipsConfig = false;


    public static final String modID = "anniv8";

    public static boolean initializedStrings = false;

    public static final Map<String, Keyword> keywords = new HashMap<>();

    public static HashSet<String> questPackages = new HashSet<>();

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }


    public Anniv8Mod() {
        BaseMod.subscribe(this);
    }

    public static String makePath(String resourcePath) {
        return modID + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return modID + "Resources/images/" + resourcePath;
    }

    public static String makeContributionPath(String packageName, String resourcePath) {
        return modID + "Resources/images/" + packageName + "/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return modID + "Resources/images/ui/" + resourcePath;
    }

    public static String makeShaderPath(String resourcePath) {
        return modID + "Resources/shaders/" + resourcePath;
    }

    public static void initialize() {
        thismod = new Anniv8Mod();

        try {
            Properties defaults = new Properties();
            defaults.put(HARD_MODE_CONFIG, false);
            defaults.put(QUESTBOUND_CONFIG, true);
            modConfig = new SpireConfig(modID, "anniv8Config", defaults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveEditRelics() {
        new AutoAdd(modID)
                .packageFilter(Anniv8Mod.class)
                .any(AbstractSQRelic.class, (info, relic) -> {
                    if (relic.color == null) {
                        BaseMod.addRelic(relic, RelicType.SHARED);
                    } else {
                        BaseMod.addRelicToCustomPool(relic, relic.color);
                    }
                    if (!info.seen) {
                        UnlockTracker.markRelicAsSeen(relic.relicId);
                    }
                });
    }

    @Override
    public void receiveEditCards() {
        new AutoAdd(modID)
                .packageFilter(Anniv8Mod.class)
                .setDefaultSeen(true)
                .cards();

        BaseMod.addDynamicVariable(new SecondMagicNumber());
        BaseMod.addDynamicVariable(new SecondDamage());
    }

    @Override
    public void receivePostInitialize() {
        initializedStrings = true;

        QuestManager.initialize();
        QuestGenerator.initialize();
        QuestRunHistoryPatch.initialize();
        QuestStatManager.initialize();
        addPotions();
        addMonsters();
        addSaveFields();
        initializeSavedData();
        initializeConfig();

        BaseMod.addCustomScreen(new QuestBoardScreen());

        ConsoleCommand.addCommand("addquest", AddQuestCommand.class);
        ConsoleCommand.addCommand("spawnquest", SpawnQuestCommand.class);

        CompatUtil.postInit();
    }

    public static void addPotions() {

        BaseMod.addPotion(NuclearJuicePotion.class, null, null, null, NuclearJuicePotion.POTION_ID);

        if (Loader.isModLoaded("widepotions")) {
            Consumer<String> whitelist = getWidePotionsWhitelistMethod();
        }

    }

    public static void addMonsters() {
        RealityTwistQuest.addMonsters();
        BaseMod.addMonster(ICEliteMonster.ID, () -> new ICEliteMonster());
        BaseMod.addMonster(DefectEliteMonster.ID, () -> new DefectEliteMonster());
        BaseMod.addMonster(WatcherEliteMonster.ID, () -> new WatcherEliteMonster());
        BaseMod.addMonster(EvilSentry.ID, QuestStringsUtils.getQuestString(makeID(EvilSentryQuest.class.getSimpleName())).TITLE, () -> new MonsterGroup(new AbstractMonster[]{
                new EvilSentry(-330.0F, 25.0F),
                new EvilSentry(-85.0F, 10.0F),
                new EvilSentry(140.0F, 30.0F)
        }));
    }

    private static Consumer<String> getWidePotionsWhitelistMethod() {
        // To avoid the need for a dependency of any kind, we call Wide Potions through reflection
        try {
            Method whitelistMethod = Class.forName("com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod").getMethod("whitelistSimplePotion", String.class);
            return s -> {
                try {
                    whitelistMethod.invoke(null, s);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error trying to whitelist wide potion for " + s, e);
                }
            };
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not find method WidePotionsMod.whitelistSimplePotion", e);
        }
    }

    @Deprecated
    private String getLangString() {
        for (Settings.GameLanguage lang : SupportedLanguages) {
            if (lang.equals(Settings.language)) {
                return Settings.language.name().toLowerCase(Locale.ROOT);
            }
        }
        return "eng";
    }

    @Override
    public void receiveEditStrings() {
        Collection<CtClass> questClasses = new AutoAdd(modID)
                .packageFilter(Anniv8Mod.class)
                .findClasses(AbstractQuest.class);

        questClasses.stream().forEach(ctClass -> questPackages.add(ctClass.getPackageName().substring(ctClass.getPackageName().lastIndexOf('.') + 1)));

        loadStrings("eng");

        loadQuestStrings(questPackages, "eng");
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadStrings(Settings.language.toString().toLowerCase());
            loadQuestStrings(questPackages, Settings.language.toString().toLowerCase());
        }
    }


    private void loadStrings(String langKey) {
        if (!Gdx.files.internal(modID + "Resources/localization/" + langKey + "/").exists()) return;
        loadStringsFile(langKey, CardStrings.class);
        loadStringsFile(langKey, RelicStrings.class);
        loadStringsFile(langKey, PowerStrings.class);
        loadStringsFile(langKey, UIStrings.class);
        loadStringsFile(langKey, StanceStrings.class);
        loadStringsFile(langKey, OrbStrings.class);
        loadStringsFile(langKey, PotionStrings.class);
        loadStringsFile(langKey, MonsterStrings.class);
        loadStringsFile(langKey, BlightStrings.class);
    }


    public void loadQuestStrings(Set<String> packages, String langKey) {

        for (String packageName : packages) {
            String languageAndFolder = langKey + "/" + packageName;
            String filepath = modID + "Resources/localization/" + languageAndFolder + "/";
            if (!Gdx.files.internal(filepath).exists()) {
                continue;
            }
            logger.info("Loading strings for package " + packageName + "from \"resources/localization/" + languageAndFolder + "\"");

            loadStringsFile(languageAndFolder, CardStrings.class);
            loadStringsFile(languageAndFolder, RelicStrings.class);
            loadStringsFile(languageAndFolder, PowerStrings.class);
            loadStringsFile(languageAndFolder, UIStrings.class);
            loadStringsFile(languageAndFolder, StanceStrings.class);
            loadStringsFile(languageAndFolder, OrbStrings.class);
            loadStringsFile(languageAndFolder, PotionStrings.class);
            loadStringsFile(languageAndFolder, MonsterStrings.class);
            loadStringsFile(languageAndFolder, BlightStrings.class);
            QuestStringsUtils.registerQuestStrings(filepath);
        }
    }

    private void loadStringsFile(String key, Class<?> stringType) {
        String filepath = modID + "Resources/localization/" + key + "/" + stringType.getSimpleName().replace("Strings", "strings") + ".json";
        if (Gdx.files.internal(filepath).exists()) {
            try {
                BaseMod.loadCustomStringsFile(stringType, filepath);
            }
            catch (Exception e) {
                throw new RuntimeException("Error loading strings file " + filepath, e);
            }
        }
    }

    @Override
    public void receiveEditKeywords() {
        loadKeywords(questPackages, "eng");
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadKeywords(questPackages, Settings.language.toString().toLowerCase());
        }
    }

    private void loadKeywords(Set<String> packages, String langKey) {
        String filepath = modID + "Resources/localization/" + langKey + "/Keywordstrings.json";
        Gson gson = new Gson();
        List<Keyword> keywords = new ArrayList<>();
        if (Gdx.files.internal(filepath).exists()) {
            String json = Gdx.files.internal(filepath).readString(String.valueOf(StandardCharsets.UTF_8));
            keywords.addAll(Arrays.asList(gson.fromJson(json, Keyword[].class)));
        }
        for (String packageName : packages) {
            String languageAndFolder = langKey + "/" + packageName;
            String questJson = modID + "Resources/localization/" + languageAndFolder + "/Keywordstrings.json";
            FileHandle handle = Gdx.files.internal(questJson);
            if (handle.exists()) {
                logger.info("Loading keywords for quest package " + packageName + "from \"resources/localization/" + languageAndFolder + "\"");
                questJson = handle.readString(String.valueOf(StandardCharsets.UTF_8));
                List<Keyword> questKeywords = new ArrayList<>(Arrays.asList(gson.fromJson(questJson, Keyword[].class)));
                keywords.addAll(questKeywords);
            }
        }

        for (Keyword keyword : keywords) {
            BaseMod.addKeyword(modID, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            if (!keyword.ID.isEmpty()) {
                Anniv8Mod.keywords.put(keyword.ID, keyword);
            }
        }
    }


    @Override
    public void receiveAddAudio() {
    }

    @Override
    public void receivePostDungeonInitialize() {
        if (!CardCrawlGame.isInARun()) {

        }
    }

    public static SingleCardReward hoverRewardWorkaround;

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (hoverRewardWorkaround != null) {
            hoverRewardWorkaround.renderCardOnHover(sb);
            hoverRewardWorkaround = null;
        }
    }

    private ModPanel settingsPanel;


    private void initializeConfig() {
        UIStrings configStrings = CardCrawlGame.languagePack.getUIString(makeID("ConfigMenuText"));

        Texture badge = TexLoader.getTexture(makeImagePath("ui/badge.png"));

        settingsPanel = new ModPanel();
        FixedModLabeledToggleButton toggleHardModeButton = new FixedModLabeledToggleButton(configStrings.TEXT[3],
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                hardModeConfig,
                settingsPanel,
                (label) -> {},
                (button) -> {
                    hardModeConfig = button.enabled;
                    saveConfig();
                });
        settingsPanel.addUIElement(toggleHardModeButton);

        FixedModLabeledToggleButton toggleQuestboundButton = new FixedModLabeledToggleButton(configStrings.TEXT[4],
                350.0f, 600.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                questboundConfig,
                settingsPanel,
                (label) -> {},
                (button) -> {
                    questboundConfig = button.enabled;
                    saveConfig();
                });
        settingsPanel.addUIElement(toggleQuestboundButton);

        FixedModLabeledToggleButton toggleTrophyTooltipsButton = new FixedModLabeledToggleButton(configStrings.TEXT[5],
                350.0f, 500.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                trophyTooltipsConfig,
                settingsPanel,
                (label) -> {},
                (button) -> {
                    trophyTooltipsConfig = button.enabled;
                    saveConfig();
                });
        settingsPanel.addUIElement(toggleTrophyTooltipsButton);

        BaseMod.registerModBadge(badge, configStrings.TEXT[0], configStrings.TEXT[1], configStrings.TEXT[2], settingsPanel);
    }

    private void initializeSavedData() {
        hardModeConfig = modConfig.getBool(HARD_MODE_CONFIG);
        questboundConfig = modConfig.getBool(QUESTBOUND_CONFIG);
        trophyTooltipsConfig = modConfig.getBool(TROPHY_TOOLTIP_CONFIG);
    }

    public static void addSaveFields() {
        RealityTwistQuest.addSaveFields();
    }

    @Override
    public void receiveStartGame() {

    }

    public static boolean questsHaveCost() {
        return hardModeConfig;
    }

    public static boolean questboundEnabled() {
        return questboundConfig;
    }

    public static boolean trophyTooltipsEnabled() {
        return trophyTooltipsConfig;
    }

    public static void saveConfig() {
        try {
            modConfig.setBool(HARD_MODE_CONFIG, hardModeConfig);
            modConfig.setBool(QUESTBOUND_CONFIG, questboundConfig);
            modConfig.setBool(TROPHY_TOOLTIP_CONFIG, trophyTooltipsConfig);
            modConfig.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void receivePostDeath() {
        QuestManager.failAllActiveQuests();
    }


    public static boolean isStatsFTUEComplete() {
        if (modConfig == null) {
            return true;
        }
        return modConfig.getBool("CompletedStatsFTUE");
    }


    public static void completeStatsFTUE() {
        if (modConfig == null) {
            return;
        }
        try {
            modConfig.setBool("CompletedStatsFTUE", true);
            modConfig.save();
        } catch (IOException e) {
            logger.error(e);
        }
    }

}



