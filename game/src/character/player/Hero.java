package character.player;

import character.DungeonCharacter;
import character.skills.BaseMeleeSkill;
import character.skills.BaseSkill;
import collision.CharacterDirection;
import collision.Collidable;
import collision.Hitbox;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import graphic.Animation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import level.elements.ILevel;
import mydungeon.Starter;
import textures.TextureHandler;
import tools.Point;

/** Player-Character. */
public class Hero extends DungeonCharacter {

    private final Animation IDLE_ANIMATION;
    private final Animation RUN_LEFT_ANIMATION;
    private final Animation RUN_RIGHT_ANIMATION;
    private CharacterDirection lastDirection;
    private BaseSkill attackSkill;

    public Hero() {
        // 16x28
        super(5, 0.3f, new Hitbox(6, 6));
        int frameTime = 5;
        List<String> texturePaths =
                TextureHandler.getInstance().getTexturePaths("knight_m_idle_anim_f");
        Animation animation = new Animation(texturePaths, frameTime * 2);
        IDLE_ANIMATION = animation;

        texturePaths = TextureHandler.getInstance().getTexturePaths("knight_m_run_anim_f\\d+");
        animation = new Animation(texturePaths, frameTime);
        RUN_RIGHT_ANIMATION = animation;

        texturePaths =
                TextureHandler.getInstance().getTexturePaths("knight_m_run_anim_mirrored_f\\d+");
        animation = new Animation(texturePaths, frameTime);
        RUN_LEFT_ANIMATION = animation;

        currentAnimation = IDLE_ANIMATION;

        Map<CharacterDirection, List<String>> textures = new HashMap<>();
        textures.put(
                CharacterDirection.LEFT,
                TextureHandler.getInstance().getTexturePaths("attack_left_"));
        Map<CharacterDirection, Point> offsets = new HashMap<>();
        offsets.put(CharacterDirection.LEFT, new Point(0, 0));
        Map<CharacterDirection, Hitbox[]> hitboxes = new HashMap<>();
        hitboxes.put(
                CharacterDirection.LEFT,
                new Hitbox[] {new Hitbox(5, 5), new Hitbox(11, 5), new Hitbox(15, 5)});
        attackSkill = new BaseMeleeSkill(this, offsets, textures, hitboxes);
    }

    @Override
    /** Movement based on Key-Inputs */
    protected CharacterDirection getDirection() {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) return CharacterDirection.UP;
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) return CharacterDirection.DOWN;
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) return CharacterDirection.RIGHT;
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) return CharacterDirection.LEFT;
        else return CharacterDirection.NONE;
    }

    @Override
    protected void setAnimation(CharacterDirection direction) {
        if (direction != CharacterDirection.NONE) lastDirection = direction;
        if (direction == CharacterDirection.LEFT) currentAnimation = RUN_LEFT_ANIMATION;
        else if (direction == CharacterDirection.RIGHT) currentAnimation = RUN_RIGHT_ANIMATION;
        else currentAnimation = IDLE_ANIMATION;
    }

    @Override
    public void setLevel(ILevel level) {
        currentLevel = level;
        currentPosition = level.getStartTile().getCoordinate().toPoint();
    }

    @Override
    public void colide(Collidable other, CharacterDirection from) {
        // todo
    }

    @Override
    public void update() {
        super.update();
        skills();
    }

    public void skills() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
            Starter.Game.spawnEffect(attackSkill.cast(CharacterDirection.LEFT));
    }
}
