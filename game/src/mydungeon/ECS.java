package mydungeon;

import controller.Game;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.entitys.Entity;
import ecs.entitys.Hero;
import ecs.systems.DrawSystem;
import ecs.systems.MovementSystem;
import ecs.systems.SystemController;
import java.util.*;
import level.LevelAPI;
import level.elements.ILevel;
import level.generator.postGeneration.WallGenerator;
import level.generator.randomwalk.RandomWalkGenerator;
import starter.DesktopLauncher;
import tools.Point;

public class ECS extends Game {
    /** Map with all PositionComponents in the ECS. TODO: HOW TO DELETE? */
    public static Map<Entity, PositionComponent> positionComponentMap;
    /** Map with all VelocityComponent in the ECS. TODO: HOW TO DELETE? */
    public static Map<Entity, VelocityComponent> velocityComponentMap;

    /** Map with all AnimationComponent in the ECS. TODO: HOW TO DELETE? */
    public static Map<Entity, AnimationComponent> animationComponentMap;

    /** List of all Systems in the ECS */
    public static SystemController systems;

    public static ILevel currentLevel;

    private Hero hero;

    @Override
    protected void setup() {
        controller.clear();
        systems = new SystemController();
        controller.add(systems);
        setupComponentMaps();
        hero = new Hero(new Point(0, 0));
        levelAPI = new LevelAPI(batch, painter, new WallGenerator(new RandomWalkGenerator()), this);
        levelAPI.loadLevel();

        new MovementSystem();
        new DrawSystem(painter);
    }

    private void setupComponentMaps() {
        positionComponentMap = new HashMap<>();
        velocityComponentMap = new HashMap<>();
        animationComponentMap = new HashMap<>();
    }

    @Override
    protected void frame() {
        camera.setFocusPoint(hero.getPositionComponent().getPosition());
    }

    @Override
    public void onLevelLoad() {
        currentLevel = levelAPI.getCurrentLevel();
        hero.getPositionComponent()
                .setPosition(currentLevel.getStartTile().getCoordinate().toPoint());
    }

    public static void main(String[] args) {
        // start the game
        DesktopLauncher.run(new ECS());
    }
}
