package online.refract.client;

import online.refract.game.server.ServerAssetCache;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.game.state.ClocktowerRole;
import online.refract.game.state.ClocktowerState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import online.refract.network.C2SPackets.AssetRequestPayload;
import online.refract.network.S2CPackets.AssetResponsePayload;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.platform.NativeImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side asset cache. In-memory only — no disk persistence.
 *
 * Lifecycle:
 *   onStateReceived()    — diff new hashes against what we have, request missing
 *   onAssetChunkReceived() — assemble chunks, register texture when complete
 *   clear()              — call on world disconnect to free GL textures
 */
public class ClientAssetCache {

    private static final Logger LOGGER = LoggerFactory.getLogger("sttk/ClientAssetCache");


    private final Map<String, ResourceLocation> textureRegistry = new ConcurrentHashMap<>();
    private final Set<String> pending = ConcurrentHashMap.newKeySet();
    private final Map<String, byte[][]> chunkBuffers = new ConcurrentHashMap<>();


    public void onStateReceived(ClocktowerState state) {
        Set<String> needed = new HashSet<>();

        for (String url: state.allAssetUrls()) {
            if (textureRegistry.containsKey(url)) continue;
            if (pending.contains(url)) return;   
            pending.add(url);
            needed.add(url);
        }

        if (!needed.isEmpty()) {
            LOGGER.info("Requesting {} missing assets from server", needed.size());
            ClientPlayNetworking.send(new AssetRequestPayload(new ArrayList<>(needed)));
        }
    }




    public void onAssetChunkReceived(String url, int chunkIndex, int totalChunks, byte[] chunkData) {
        chunkBuffers.computeIfAbsent(url, k -> new byte[totalChunks][]);
        chunkBuffers.get(url)[chunkIndex] = chunkData;

        byte[][] chunks = chunkBuffers.get(url);
        for (byte[] chunk : chunks) {
            if (chunk == null) return; 
        }

        chunkBuffers.remove(url);
        byte[] full = assembleChunks(chunks);
        registerTexture(url, full);
    }


    @Nullable
    public ResourceLocation getTexture(String url) {
        return textureRegistry.get(url);
    }

    public boolean isPending(String url) {
        return pending.contains(url);
    }


    public void clear() {
        Minecraft.getInstance().execute(() -> {
            textureRegistry.forEach((url, id) -> Minecraft.getInstance().getTextureManager().release(id));
            textureRegistry.clear();
            pending.clear();
            chunkBuffers.clear();
        });
    }


    private void registerTexture(String url, byte[] pngData) {
        String hashedUrl = ServerAssetCache.sha256(url);
        Minecraft.getInstance().execute(() -> {
            try {
                NativeImage image = NativeImage.read(new ByteArrayInputStream(pngData));
                DynamicTexture texture = new DynamicTexture(() -> "sttk_" + hashedUrl, image);             
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath("sttk", "icon/" + hashedUrl);
                Minecraft.getInstance().getTextureManager().register(id, texture);
                textureRegistry.put(url, id);
                pending.remove(url);
                LOGGER.info("Registered client texture for {}", url);
            } catch (IOException e) {
                LOGGER.error("Failed to read image data for {}: {}", url, e.getMessage());
                pending.remove(url);
            }
        });
    }

    private byte[] assembleChunks(byte[][] chunks) {
        int total = Arrays.stream(chunks).mapToInt(c -> c.length).sum();
        byte[] out = new byte[total];
        int pos = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, out, pos, chunk.length);
            pos += chunk.length;
        }
        return out;
    }
}