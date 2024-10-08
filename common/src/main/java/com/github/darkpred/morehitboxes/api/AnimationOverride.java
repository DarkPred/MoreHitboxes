package com.github.darkpred.morehitboxes.api;

import net.minecraft.world.phys.Vec3;

/**
 * This record stores the position and scale that is calculated by the currently running GeckoLib animation.
 * <p>
 * For this to work well, I would recommend adding an empty bone to the model and using its pivot point as the starting
 * position of the hitbox. This position will be the bottom center of the hitbox entity.
 * <p>
 * A Blockbench plugin to help with adding and saving hitboxes is available on the GitHub page
 *
 * @param localPos the local position of the GeckoLib bone referenced by {@link HitboxData#ref()}
 * @param scaleW   the x scale of the bone
 * @param scaleH   the y scale of the bone
 */
public record AnimationOverride(Vec3 localPos, float scaleW, float scaleH) {
}
