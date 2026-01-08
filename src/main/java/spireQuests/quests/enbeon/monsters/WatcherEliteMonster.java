package spireQuests.quests.enbeon.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Shuriken;
import com.megacrit.cardcrawl.vfx.WallopEffect;
import com.megacrit.cardcrawl.vfx.stance.DivinityParticleEffect;
import com.megacrit.cardcrawl.vfx.stance.StanceAuraEffect;
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.quests.enbeon.powers.FakeDevotionPower;
import spireQuests.quests.enbeon.powers.InvisibleDivinityForMonsterPower;
import spireQuests.quests.gk.vfx.FakePlayCardEffect;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;

public class WatcherEliteMonster extends AbstractSQMonster {
    public static final String ID = makeID(WatcherEliteMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final Byte RAPID_STRIKES = 0, WALLOP = 1, WAVE_PROTECT = 2;

    private final AbstractRelic relic;
    private int devotionAmt = 2;
    private int waveAmt = 1;

    private float particleTimer = 0.0f;
    private float auraTimer = 0.0f;
    private long sfxId;

    private final Bone eyeBone;
    protected Skeleton eyeSkeleton;
    public AnimationState eyeState;
    protected AnimationStateData eyeStateData;
    protected TextureAtlas eyeAtlas = null;

    public WatcherEliteMonster() {
        this(0, 0);
    }

    public WatcherEliteMonster(float x, float y) {
        super(NAME, ID, 80, -4f, -16f, 220f, 290f, null, x, y);
        type = EnemyType.ELITE;

        setHp(calcAscensionTankiness(144), calcAscensionTankiness(154));
        addMove(RAPID_STRIKES, Intent.ATTACK_BUFF, calcAscensionDamage(5), 6);
        addMove(WALLOP, Intent.ATTACK_DEFEND, calcAscensionDamage(9));
        addMove(WAVE_PROTECT, Intent.DEFEND_DEBUFF);

        devotionAmt = calcAscensionSpecial(devotionAmt);
        waveAmt = calcAscensionSpecial(waveAmt);

        loadAnimation("images/characters/watcher/idle/skeleton.atlas",
                "images/characters/watcher/idle/skeleton.json",
                1f);
        loadEyeAnimation();
        AnimationState.TrackEntry e = state.setAnimation(0, "Idle", true);
        stateData.setMix("Hit", "Idle", 0.1f);
        e.setTimeScale(0.6f);
        flipHorizontal = true;
        eyeBone = this.skeleton.findBone("eye_anchor");

        relic = new Shuriken();
    }

    private void loadEyeAnimation() {
        this.eyeAtlas = new TextureAtlas(Gdx.files.internal("images/characters/watcher/eye_anim/skeleton.atlas"));
        SkeletonJson json = new SkeletonJson(this.eyeAtlas);
        json.setScale(Settings.scale);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files
                .internal("images/characters/watcher/eye_anim/skeleton.json"));
        this.eyeSkeleton = new Skeleton(skeletonData);
        this.eyeSkeleton.setColor(Color.WHITE);
        this.eyeStateData = new AnimationStateData(skeletonData);
        this.eyeState = new AnimationState(this.eyeStateData);
        this.eyeStateData.setDefaultMix(0.2F);
        this.eyeState.setAnimation(0, "None", true);
    }

    @Override
    public void init() {
        super.init();
        relic.currentX = relic.targetX = hb.x + hb.width + (100f* Settings.xScale);
        relic.currentY = relic.targetY = hb.y;
    }

    @Override
    public void usePreBattleAction() {
        doFakePlay(new Devotion(), 18);
        AbstractPower devotion = new FakeDevotionPower(this, devotionAmt);
        addToBot(new ApplyPowerAction(this, this, devotion));
        // Need to activate Devotion immediately so Watcher enters Divinity on turn 5 (4)
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                devotion.atEndOfRound();
                this.isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        info.applyPowers(this, AbstractDungeon.player);
        switch (nextMove) {
            case 0: // Rapid Strikes
                useFastAttackAnimation();
                for (int i = 0; i < 6; i++) {
                    if (i < 3) {
                        doFakePlay(new Strike_Purple(), Integer.MAX_VALUE); // Don't upgrade
                    } else {
                        doFakePlay(new FlurryOfBlows(), 3);
                    }
                    addToBot(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 2)));
                addToBot(new RelicAboveCreatureAction(this, relic));
                break;
            case 1: // Wallop
                doFakePlay(new Wallop(), 3);
                useSlowAttackAnimation();
                addToBot(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractPlayer p = AbstractDungeon.player;
                        if (p.lastDamageTaken > 0) {
                            addToTop(new GainBlockAction(WatcherEliteMonster.this, p.lastDamageTaken));
                            if (p.hb != null) {
                                addToTop(new VFXAction(new WallopEffect(p.lastDamageTaken, p.hb.cX, p.hb.cY)));
                            }
                        }
                        this.isDone = true;
                    }
                });
                break;
            case 2: // Wave of the Hand / Protect
                doFakePlay(new WaveOfTheHand(), 18);
                addToBot(new ApplyPowerAction(Wiz.adp(), this, new WeakPower(Wiz.adp(), waveAmt, true)));
                doFakePlay(new Protect(), 8);
                addToBot(new GainBlockAction(this, calcAscensionTankiness(12)));
        }
        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        // Cycle Wallop -> Rapid Strikes -> Wave of the Hand / Protect
        // There's special behaviour when she enters Divinity, see the below method
        if (lastMove(WALLOP)) {
            setMoveShortcut(RAPID_STRIKES, MOVES[RAPID_STRIKES]);
        } else if (lastMove(RAPID_STRIKES)) {
            setMoveShortcut(WAVE_PROTECT, MOVES[WAVE_PROTECT]);
        } else {
            setMoveShortcut(WALLOP, MOVES[WALLOP]);
        }
    }

    // This is called in InvisibleDivinityForMonsterPower
    public void prepareDivinityMove() {
        // Upon entering Divinity, always use Wallop (then continue cycling from that point)
        setMoveShortcut(WALLOP, MOVES[WALLOP]);
        createIntent();
        addToBot(new SetMoveAction(this, MOVES[WALLOP], WALLOP, Intent.ATTACK));
    }

    @Override
    public void damage(DamageInfo info) {
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output - currentBlock > 0) {
            AnimationState.TrackEntry e = state.setAnimation(0, "Hit", false);
            state.addAnimation(0, "Idle", true, 0f);
            e.setTimeScale(0.6f);
        }

        super.damage(info);
    }

    private void doFakePlay(AbstractCard c, int ascLevelToUpgrade) {
        if (AbstractDungeon.ascensionLevel >= ascLevelToUpgrade) c.upgrade();
        Wiz.vfx(new FakePlayCardEffect(this, c));
    }

    private boolean isInDivinity() {
        return hasPower(InvisibleDivinityForMonsterPower.POWER_ID);
    }

    // Animation & sound logic

    @Override
    public void update() {
        super.update();
        // Do Divinity particles
        if (isInDivinity()) {
            if (!Settings.DISABLE_EFFECTS) {
                this.particleTimer -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer < 0.0F) {
                    this.particleTimer = 0.2F;
                    AbstractDungeon.effectsQueue.add(makeDivinityParticleEffect());
                }
            }
            this.auraTimer -= Gdx.graphics.getDeltaTime();
            if (this.auraTimer < 0.0F) {
                this.auraTimer = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(makeDivinityAuraEffect());
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        // Render staff eye
        this.eyeState.update(Gdx.graphics.getDeltaTime());
        this.eyeState.apply(this.eyeSkeleton);
        this.eyeSkeleton.updateWorldTransform();
        this.eyeSkeleton.setPosition(this.skeleton.getX() + this.eyeBone.getWorldX(), this.skeleton.getY() + this.eyeBone.getWorldY());
        this.eyeSkeleton.setColor(this.tint.color);
        this.eyeSkeleton.setFlip(this.flipHorizontal, this.flipVertical);
        sb.end();
        CardCrawlGame.psb.begin();
        sr.draw(CardCrawlGame.psb, this.eyeSkeleton);
        CardCrawlGame.psb.end();
        sb.begin();
        // Render Shuriken
        Color transparent = Color.WHITE.cpy();
        transparent.a = 0;
        relic.renderWithoutAmount(sb, transparent);
    }

    private DivinityParticleEffect makeDivinityParticleEffect() {
        DivinityParticleEffect effect = new DivinityParticleEffect();
        ReflectionHacks.setPrivate(effect, DivinityParticleEffect.class, "x",
                this.hb.cX + MathUtils.random(-this.hb.width / 2.0F - 50.0F * Settings.scale, this.hb.width / 2.0F + 50.0F * Settings.scale)
        );
        ReflectionHacks.setPrivate(effect, DivinityParticleEffect.class, "y",
                this.hb.cY + MathUtils.random(-this.hb.height / 2.0F + 10.0F * Settings.scale, this.hb.height / 2.0F - 20.0F * Settings.scale)
        );
        return effect;
    }

    private StanceAuraEffect makeDivinityAuraEffect() {
        StanceAuraEffect effect = new StanceAuraEffect("Divinity");
        ReflectionHacks.setPrivate(effect, StanceAuraEffect.class, "x",
                this.hb.cX + MathUtils.random(-this.hb.width / 16.0F, this.hb.width / 16.0F)
        );
        ReflectionHacks.setPrivate(effect, StanceAuraEffect.class, "y",
                this.hb.cY + MathUtils.random(-this.hb.height / 16.0F, this.hb.height / 16.0F)
        );
        return effect;
    }

    public void startIdleSfx() {
        sfxId = CardCrawlGame.sound.playAndLoop("STANCE_LOOP_DIVINITY");
    }

    public void stopIdleSfx() {
        if (sfxId != -1L) {
            CardCrawlGame.sound.stop("STANCE_LOOP_DIVINITY", sfxId);
            sfxId = -1L;
        }
    }
}
