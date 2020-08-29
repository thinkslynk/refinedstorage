package com.refinedmods.refinedstorage.util

import com.mojang.authlib.GameProfile
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.network.ServerPlayerInteractionManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.TranslatableText
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

object WorldUtils {

    fun updateBlock(world: World, pos: BlockPos) {
        if (world.canSetBlock(pos)) {
            val state = world.getBlockState(pos)
            world.updateListeners(pos, state, state, 0b11)
        }
    }

    // TODO Item capability
//    fun getItemHandler(tile: BlockEntity, side: Direction): IItemHandler {
//
//        var handler: IItemHandler? = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null)
//        if (handler == null) {
//            if (side != null && tile is ISidedInventory) {
//                handler = SidedInvWrapper(tile as ISidedInventory?, side)
//            } else if (tile is IInventory) {
//                handler = InvWrapper(tile as IInventory?)
//            }
//        }
//        return handler
//    }

    // TODO Fluid
//    fun getFluidHandler(tile: BlockEntity?, side: Direction?): IFluidHandler? {
//        return if (tile != null) {
//            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).orElse(null)
//        } else null
//    }

    fun getFakePlayer(world: ServerWorld, owner: UUID?): ServerPlayerEntity {
        if (owner != null) {
            world.server.playerManager.getPlayer(owner)
        }

        val fakeProfile = GameProfile(null, "cured_storage")
        return ServerPlayerEntity(world.server, world, fakeProfile, ServerPlayerInteractionManager(world))
    }

    fun sendNoPermissionMessage(player: PlayerEntity) {
        player.sendMessage(
            TranslatableText("misc.refinedstorage.security.no_permission")
                .setStyle(Styles.RED), true
        )
    }

    fun rayTracePlayer(world: World, player: PlayerEntity): BlockHitResult? {
        return null // TODO Rethink this a bit
//        val reachDistance: Double = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue()
//        val base: Vector3d = player.getEyePosition(1.0f)
//        val look: Vector3d = player.getLookVec()
//        val target: Vector3d = base.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance)
//        return world.rayTraceBlocks(RayTraceContext(base, target, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player))
    }

}