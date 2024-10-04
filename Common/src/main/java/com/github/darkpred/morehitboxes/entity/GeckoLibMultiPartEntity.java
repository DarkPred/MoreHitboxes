package com.github.darkpred.multipartsupport.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * This interface provides support for hitbox parts that follow GeckoLib animations
 * <p>
 * Required steps to use:
 * <ol>
 *     <li>Add one or more hitboxes in data/{modId}/hitboxes/{entityTypeKey}.json with {@link com.github.darkpred.multipartsupport.entity.EntityHitboxManager.HitboxData#ref()} set to a bone name</li>
 *     <li>Add a geckolib animation controlling the bone. See {@link com.github.darkpred.multipartsupport.entity.AnimationOverride} for supported features</li>
 *     <li>Implement this interface</li>
 *     <li>Call and save {@link com.github.darkpred.multipartsupport.api.PlaceHolderNameFactory#create(Mob) PlaceHolderNameFactory#create(Mob)}</li>
 *     <li>See {@link com.github.darkpred.multipartsupport.entity.MultiPartGeoEntityRenderer} </li>
 * </ol>
 *
 * @param <T> the type of the mob implementing this interface
 */
public interface GeckoLibMultiPartEntity<T extends Mob & com.github.darkpred.multipartsupport.entity.MultiPartEntity<T>> extends com.github.darkpred.multipartsupport.entity.MultiPartEntity<T> {

    /**
     * This method will be called if {@link GeckoLibMultiPartEntity#canSetAnchorPos(String)} returned {@code true} and should
     * be used to pass a bone position to the entity
     * <p>
     * Possible use cases are the positioning of geckolib particle listeners or of the riding player
     *
     * @param boneName the name of the bone
     * @param localPos the position of the bone relative to the mobs position
     */
    void setAnchorPos(String boneName, Vec3 localPos);

    /**
     * Called to check if the position for the given bone should be calculated and passed to {@link GeckoLibMultiPartEntity#setAnchorPos(String, Vec3)}
     *
     * @param boneName the name of the bone
     * @return {@code true} if the position for the given bone should be calculated
     * @apiNote this method will be called for every bone that is not attached to a {@link MultiPart}
     */
    boolean canSetAnchorPos(String boneName);
}
