package com.github.darkpred.morehitboxes.internal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mixin interface injected into GeoEntityRenderer.
 * In this version of the library {@link #updateTickForEntity(Entity)} has to be manually called at the end of {@link software.bernie.geckolib3.renderers.geo.IGeoRenderer#render(software.bernie.geckolib3.geo.render.built.GeoModel, Object, float, RenderType, PoseStack, MultiBufferSource, VertexConsumer, int, int, float, float, float, float) IGeoRenderer#render}
 * <pre>
 *     {@code
 *
 *     if (this instanceof MultiPartGeoEntityRenderer renderer) {
 *         renderer.updateTickForEntity(animatable);
 *     }}
 * </pre>
 */
@ApiStatus.NonExtendable
@ApiStatus.ScheduledForRemoval(inVersion = "Minecraft >=1.19.3")
public interface MultiPartGeoEntityRenderer {

    @ApiStatus.Internal
    void removeTickForEntity(Entity entity);

    /**
     * @see MultiPartGeoEntityRenderer
     */
    void updateTickForEntity(Entity entity);
}
