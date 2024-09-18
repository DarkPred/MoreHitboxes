package com.github.darkpred.multipartsupport.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Example extends Mob implements MultiPartEntity {

    private float headRadius;
    private float frustumWidthRadius;
    private float frustumHeightRadius;
    public long attackBoxEndTime;
    public final Map<String, EntityHitboxManager.Hitbox> attackBoxes = new HashMap<>();
    public final Map<EntityHitboxManager.Hitbox, Vec3> activeAttackBoxes = new HashMap<>();
    private AABB attackBounds = new AABB(0, 0, 0, 0, 0, 0);
    private AABB cullingBounds = new AABB(0, 0, 0, 0, 0, 0);
    private final Store store = Store.get(self);

    protected Example(EntityType<? extends Example> entityType, Level level) {
        super(entityType, level);
        this.attackBounds = makeAttackBounds();
        this.cullingBounds = makeBoundingBoxForCulling();
    }

    /**
     * @return The child parts of this entity.
     * @implSpec On the forge classpath this implementation should return objects that inherit from PartEntity instead of Entity.
     */
    public List<MultiPart> getCustomParts() {
        return parts;
    }

    /**
     * @param ref the name of the bone the hitbox is attached to
     * @return the hitbox attached to the given bone
     */
    @Nullable
    public MultiPart getCustomPart(String ref) {
        return partsByRef.get(ref);
    }

    @Override
    public void refreshDimensions() {
        double oldY = getY();
        if (isCustomMultiPart()) {
            super.refreshDimensions();
            setPos(getX(), oldY, getZ());
            for (MultiPart part : parts) {
                part.getEntity().refreshDimensions();
            }
        } else {
            super.refreshDimensions();
            setPos(getX(), oldY, getZ());
        }
    }

    @Override
    public boolean isPickable() {
        return !isCustomMultiPart();
    }

    @SuppressWarnings("java:S2589")
    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (parts != null) {
            this.attackBounds = makeAttackBounds();
            this.cullingBounds = makeBoundingBoxForCulling();
        }
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return cullingBounds;
    }

    public AABB getAttackBounds() {
        return attackBounds;
    }

    private AABB makeBoundingBoxForCulling() {
        if (isCustomMultiPart()) {
            float x = frustumWidthRadius * getScale();
            float y = frustumHeightRadius * getScale();
            Vec3 pos = position();
            return new AABB(pos.x - x, pos.y, pos.z - x, pos.x + x, pos.y + y, pos.z + x);
        }
        return super.getBoundingBoxForCulling();
    }

    private AABB makeAttackBounds() {
        if (headRadius != 0) {
            float radius = headRadius * getScale() * 0.9f;
            return inflateAABB(getBoundingBox(), radius, radius * 0.55, radius);
        }
        float increase = Math.min(getBbWidth() / 2, 2.25f);
        return inflateAABB(getBoundingBox(), increase, increase, increase);
    }

    private AABB inflateAABB(AABB base, double x, double y, double z) {
        return new AABB(base.minX - x, base.minY - Math.min(1, y), base.minZ - z, base.maxX + x, base.maxY + y, base.maxZ + z);
    }

    public float getHeadRadius() {
        return headRadius * getScale();
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < parts.size(); ++i) {
            parts.get(i).getEntity().setId(id + i + 1);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (isCustomMultiPart()) {
            //Ensures that the callbacks get called. Probably not necessary because the multiparts are not added to the server
            for (MultiPart part : parts) {
                part.getEntity().remove(reason);
            }
        }
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        if (isCustomMultiPart()) {
            //Ensures that the callbacks get called on the client side
            for (MultiPart part : parts) {
                part.getEntity().remove(RemovalReason.DISCARDED);
            }
        }
        ((PrehistoricGeoRenderer<? extends Example>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).removeTickForEntity(this);
    }

    public boolean hurt(Entity part, DamageSource source, float damage) {
        return hurt(source, damage);
    }


    @Override
    public void aiStep() {
        super.aiStep();
        if (isCustomMultiPart()) {
            for (MultiPart part : parts) {
                part.updatePosition();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide && !activeAttackBoxes.isEmpty()) {
            if (level.getGameTime() > attackBoxEndTime) {
                activeAttackBoxes.clear();
            }
            for (Map.Entry<EntityHitboxManager.Hitbox, Vec3> entry : activeAttackBoxes.entrySet()) {
                EntityHitboxManager.Hitbox hitbox = entry.getKey();
                EntityDimensions size = EntityDimensions.scalable(hitbox.width(), hitbox.height()).scale(getScale());
                AABB aabb = size.makeBoundingBox(entry.getValue());
                if (Minecraft.getInstance().player.getBoundingBox().intersects(aabb)) {
                    activeAttackBoxes.clear();
                    MessageHandler.SYNC_CHANNEL.sendToServer(new C2SHitPlayerMessage(this, Minecraft.getInstance().player));
                    break;
                }
            }
        }
    }
}
