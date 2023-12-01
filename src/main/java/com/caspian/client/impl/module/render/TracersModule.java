package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.ColorConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.render.Interpolation;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.event.render.RenderWorldEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.world.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TracersModule extends ToggleModule
{
    //
    Config<Boolean> playersConfig = new BooleanConfig("Players",
            "Render tracers to player", true);
    Config<Color> playersColorConfig = new ColorConfig("PlayersColor", "The " +
            "render color for players", new Color(200, 60, 60, 255),
            () -> playersConfig.getValue());
    Config<Boolean> invisiblesConfig = new BooleanConfig("Invisibles",
            "Render tracers to invisible entities", true);
    Config<Color> invisiblesColorConfig = new ColorConfig("InvisiblesColor",
            "The render color for invisibles", new Color(200, 100, 0, 255),
            () -> invisiblesConfig.getValue());
    Config<Boolean> monstersConfig = new BooleanConfig("Monsters",
            "Render tracers to monsters", true);
    Config<Color> monstersColorConfig = new ColorConfig("MonstersColor", "The" +
            " render color for monsters", new Color(200, 60, 60, 255),
            () -> monstersConfig.getValue());
    Config<Boolean> animalsConfig = new BooleanConfig("Animals",
            "Render tracers to animals", true);
    Config<Color> animalsColorConfig = new ColorConfig("AnimalsColor", "The " +
            "render color for animals", new Color(0, 200, 0, 255),
            () -> animalsConfig.getValue());
    Config<Boolean> vehiclesConfig = new BooleanConfig("Vehicles",
            "Render tracers to vehicles", false);
    Config<Color> vehiclesColorConfig = new ColorConfig("VehiclesColor", "The" +
            " render color for vehicles", new Color(200, 100, 0, 255),
            () -> vehiclesConfig.getValue());
    Config<Boolean> itemsConfig = new BooleanConfig("Items",
            "Render tracers to items", false);
    Config<Color> itemsColorConfig = new ColorConfig("ItemsColor", "The" +
            " render color for items", new Color(255, 255, 255, 255),
            () -> itemsConfig.getValue());
    Config<Target> targetConfig = new EnumConfig<>("Target", "The body part " +
            "of the entity to target", Target.FEET, Target.values());
    Config<Float> widthConfig = new NumberConfig<>("Width", "The line width " +
            "of the tracer", 1.0f, 1.5f, 10.0f);

    /**
     *
     */
    public TracersModule()
    {
        super("Tracers", "Draws a tracer to all entities in render distance",
                ModuleCategory.RENDER);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (mc.player == null)
        {
            return;
        }
        float eyeHeight = mc.player.getEyeHeight(EntityPose.STANDING);
        Vec3d pos = new Vec3d(0.0, 0.0, 1.0)
                .rotateX(-((float) Math.toRadians(mc.player.getPitch())))
                .rotateY(-((float) Math.toRadians(mc.player.getYaw())))
                .add(0.0, eyeHeight, 0.0);
        for (Entity entity : mc.world.getEntities())
        {
            if (entity == null || !entity.isAlive() || entity == mc.player)
            {
                continue;
            }
            Color color = getTracerColor(entity);
            //
            if (color != null)
            {
                Vec3d entityPos = Interpolation.getRenderPosition(entity,
                        event.getTickDelta());
                Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
                entityPos = entityPos.subtract(cameraPos).add(0.0,
                        getTargetY(entity), 0.0);
                RenderManager.renderLine(event.getMatrices(), pos, entityPos,
                        widthConfig.getValue(), color.getRGB());
            }
        }
    }

    /**
     *
     * @param entity
     * @return
     */
    private Color getTracerColor(Entity entity)
    {
        if (entity.isInvisible() && invisiblesConfig.getValue())
        {
            return invisiblesColorConfig.getValue();
        }
        else if (entity instanceof PlayerEntity player && playersConfig.getValue())
        {
            if (Managers.SOCIAL.isFriend(player.getUuid()))
            {
                return new Color(85, 200, 200, 255);
            }
            return playersColorConfig.getValue();
        }
        else if (EntityUtil.isMonster(entity) && monstersConfig.getValue())
        {
            return monstersColorConfig.getValue();
        }
        else if ((EntityUtil.isPassive(entity) || EntityUtil.isNeutral(entity))
                && animalsConfig.getValue())
        {
            return animalsColorConfig.getValue();
        }
        else if (EntityUtil.isVehicle(entity) && vehiclesConfig.getValue())
        {
            return vehiclesColorConfig.getValue();
        }
        else if (entity instanceof ItemEntity && itemsConfig.getValue())
        {
            return itemsColorConfig.getValue();
        }
        return null;
    }

    /**
     *
     * @param entity
     * @return
     */
    private double getTargetY(Entity entity)
    {
        return switch (targetConfig.getValue())
        {
            case FEET -> 0.0;
            case TORSO -> entity.getHeight() / 2.0;
            case HEAD -> entity.getEyeHeight(EntityPose.STANDING);
        };
    }

    public enum Target
    {
        FEET,
        TORSO,
        HEAD
    }
}
