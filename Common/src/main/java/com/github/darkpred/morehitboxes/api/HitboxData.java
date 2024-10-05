package com.github.darkpred.morehitboxes.api;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Static information hitboxes loaded at resource reload.
 *
 * @param name        the name of the hitbox cube
 * @param pos         the local position of the hitbox cube
 * @param width       the x and z width of the hitbox
 * @param height      the height of the hitbox
 * @param ref         the name of the GeckoLib bone this should be attached to or "" if none
 * @param isAttackBox whether the mob can use this hitbox to hit player
 */
public record HitboxData(String name, Vec3 pos, float width, float height, String ref, boolean isAttackBox) {

    @ApiStatus.Internal
    public float getFrustumWidthRadius() {
        return (float) Math.max(Math.abs(pos.x) + width / 2, Math.abs(pos.z) + width / 2);
    }

    @ApiStatus.Internal
    public float getFrustumHeight() {
        return (float) pos.y + height;
    }

    @ApiStatus.Internal
    public static HitboxData readBuf(FriendlyByteBuf buf) {
        return new HitboxData(buf.readUtf(), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readFloat(), buf.readFloat(), buf.readUtf(), buf.readBoolean());
    }

    @ApiStatus.Internal
    public static void writeBuf(FriendlyByteBuf buf, HitboxData hitbox) {
        buf.writeUtf(hitbox.name);
        buf.writeDouble(hitbox.pos.x);
        buf.writeDouble(hitbox.pos.y);
        buf.writeDouble(hitbox.pos.z);
        buf.writeFloat(hitbox.width);
        buf.writeFloat(hitbox.height);
        buf.writeUtf(hitbox.ref);
        buf.writeBoolean(hitbox.isAttackBox);
    }
}
