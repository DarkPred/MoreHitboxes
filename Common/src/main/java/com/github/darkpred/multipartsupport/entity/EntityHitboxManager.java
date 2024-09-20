package com.github.darkpred.multipartsupport.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;


//TODO: Hitbox system explanation

/**
 *
 */
//TODO: Link blockbench plugin or add to repo

public class EntityHitboxManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    public static final EntityHitboxManager HITBOX_DATA = new EntityHitboxManager(GSON);
    private ImmutableMap<ResourceLocation, List<HitboxData>> entities = ImmutableMap.of();

    public EntityHitboxManager(Gson gson) {
        super(gson, "hitboxes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, List<HitboxData>> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> fileEntry : jsons.entrySet()) {
            if (!(fileEntry.getValue() instanceof JsonObject root)) {
                continue;
            }
            JsonArray elements = GsonHelper.getAsJsonArray(root, "elements");
            ImmutableList.Builder<HitboxData> listBuilder = ImmutableList.builder();
            for (JsonElement element : elements) {
                JsonObject elemObject = element.getAsJsonObject();
                double[] pos = new double[3];
                JsonArray posArray = GsonHelper.getAsJsonArray(elemObject, "pos");
                for (int i = 0; i < pos.length; ++i) {
                    pos[i] = GsonHelper.convertToDouble(posArray.get(i), "pos[" + i + "]");
                }
                float width = GsonHelper.getAsFloat(elemObject, "width") / 16;
                float height = GsonHelper.getAsFloat(elemObject, "height") / 16;

                JsonElement refElement = elemObject.get("ref");
                String ref = refElement == null ? "" : refElement.getAsString();
                boolean isAttack = ref != null && ref.equals("attack_hitbox");
                listBuilder.add(new HitboxData(elemObject.get("name").getAsString(), new Vec3(pos[0] / 16, pos[1] / 16, pos[2] / 16), width, height, ref, isAttack));
            }
            builder.put(fileEntry.getKey(), listBuilder.build());
        }
        entities = builder.build();
    }

    public void replaceData(Map<ResourceLocation, List<HitboxData>> dataMap) {
        entities = ImmutableMap.copyOf(dataMap);
    }

    public static List<HitboxData> readBuf(FriendlyByteBuf buf) {
        int size = buf.readInt();
        ImmutableList.Builder<HitboxData> listBuilder = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            listBuilder.add(HitboxData.readBuf(buf));
        }
        return listBuilder.build();
    }

    private static void writeBuf(FriendlyByteBuf buf, List<HitboxData> hitboxes) {
        for (HitboxData hitbox : hitboxes) {
            HitboxData.writeBuf(buf, hitbox);
        }
    }

    public FriendlyByteBuf writeBuf(FriendlyByteBuf buf) {
        buf.writeMap(entities, (buffer, key) -> buf.writeResourceLocation(key), EntityHitboxManager::writeBuf);
        return buf;
    }

    public List<HitboxData> getHitboxes(ResourceLocation entity) {
        return entities.get(entity);
    }

    public Map<ResourceLocation, List<HitboxData>> getEntities() {
        return entities;
    }

    public record HitboxData(String name, Vec3 pos, float width, float height, String ref, boolean isAttackBox) {
        public float getFrustumWidthRadius() {
            return (float) Math.max(Math.abs(pos.x) + width / 2, Math.abs(pos.z) + width / 2);
        }

        public float getFrustumHeightRadius() {
            return (float) pos.y + height;
        }

        public static HitboxData readBuf(FriendlyByteBuf buf) {
            return new HitboxData(buf.readUtf(), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readFloat(), buf.readFloat(), buf.readUtf(), buf.readBoolean());
        }

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
}
