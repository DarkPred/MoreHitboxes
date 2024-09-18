package com.github.darkpred.multipartsupport.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class Example extends Mob implements MultiPartEntity {

    private float headRadius;
    private float frustumWidthRadius;
    private float frustumHeightRadius;
    public long attackBoxEndTime;
    public final Map<String, EntityHitboxManager.HitboxData> attackBoxes = new HashMap<>();
    public final Map<EntityHitboxManager.HitboxData, Vec3> activeAttackBoxes = new HashMap<>();
    private AABB attackBounds = new AABB(0, 0, 0, 0, 0, 0);
    private AABB cullingBounds = new AABB(0, 0, 0, 0, 0, 0);
    private final PlacholderName<Example> placholderName = PlacholderName.get(this, resourceLocation);

    protected Example(EntityType<? extends Example> entityType, Level level) {
        super(entityType, level);
        this.attackBounds = makeAttackBounds();
        this.cullingBounds = makeBoundingBoxForCulling();
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

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide && !activeAttackBoxes.isEmpty()) {
            if (level.getGameTime() > attackBoxEndTime) {
                activeAttackBoxes.clear();
            }
            for (Map.Entry<EntityHitboxManager.HitboxData, Vec3> entry : activeAttackBoxes.entrySet()) {
                EntityHitboxManager.HitboxData hitboxData = entry.getKey();
                EntityDimensions size = EntityDimensions.scalable(hitboxData.width(), hitboxData.height()).scale(getScale());
                AABB aabb = size.makeBoundingBox(entry.getValue());
                if (Minecraft.getInstance().player.getBoundingBox().intersects(aabb)) {
                    activeAttackBoxes.clear();
                    MessageHandler.SYNC_CHANNEL.sendToServer(new C2SHitPlayerMessage(this, Minecraft.getInstance().player));
                    break;
                }
            }
        }
    }

    @Override
    public boolean partHurt(MultiPart<?> multiPart, @NotNull DamageSource source, float amount) {
        return hurt(source, amount);
    }
}
