package com.filloax.fxlib.platform

import com.filloax.fxlib.MixinHelpersFabric
import com.filloax.fxlib.api.fabric.EventOnce
import com.filloax.fxlib.api.platform.PlatformAbstractions
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ChunkPos
import java.util.*

class PlatformAbstractionsFabric : PlatformAbstractions {
    override fun isDevEnvironment(): Boolean {
        return FabricLoader.getInstance().isDevelopmentEnvironment
    }

    override fun getServer() = MixinHelpersFabric.SERVER_HOLDER.get()

    /**
     * Run the action on the entity immediately if loaded, as soon as it's loaded
     * otherwise. Will not persist on game reload.
     */
    override fun runOnEntityWhenPossible(level: ServerLevel, entityUUID: UUID, action: (Entity) -> Unit) {
        EventOnce.runOnEntityWhenPossible(level, entityUUID, action)
    }

    /**
     * Run now if server started, or wait for server to start then run otherwise.
     * @param onServerThread If set, run on server thread, in case you want to be
     *  safe around multithreaded messing.
     */
    override fun runWhenServerStarted(
        server: MinecraftServer,
        onServerThread: Boolean,
        action: (MinecraftServer) -> Unit,
    ) {
        EventOnce.runWhenServerStarted(server, onServerThread, action)
    }

    override fun runAtServerTickEnd(action: (MinecraftServer) -> Unit) {
        EventOnce.runEventOnce(ServerTickEvents.END_SERVER_TICK, ServerTickEvents.EndTick { action(it) }, clearOnServerShutdown = true)
    }

    override fun runAtNextServerTickStart(action: (MinecraftServer) -> Unit) {
        EventOnce.runEventOnce(ServerTickEvents.START_SERVER_TICK, ServerTickEvents.StartTick { action(it) }, clearOnServerShutdown = true)
    }

    override fun runWhenChunkLoaded(level: ServerLevel, chunkPos: ChunkPos, action: (ServerLevel) -> Unit) {
        EventOnce.runWhenChunkLoaded(level, chunkPos, action)
    }

    /**
     * Execute code when all the chunks in the surrounding area are loaded, or immediately if loaded already.
     * Note that this isn't assured to ever run depending on area, as if big enough chunks on one end might be
     * unloaded when the other end is loaded; use forced chunks for this, in case.
     */
    override fun runWhenChunksLoaded(
        level: ServerLevel,
        minChunkPos: ChunkPos,
        maxChunkPos: ChunkPos,
        action: (ServerLevel) -> Unit,
    ) {
        EventOnce.runWhenChunksLoaded(level, minChunkPos, maxChunkPos, action)
    }
}