package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.util.NetworkUtils.getNodeFromBlockEntity
import com.refinedmods.refinedstorage.util.WorldUtils.sendNoPermissionMessage
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import com.thinkslynk.fabric.generated.MyItemGroups
import net.minecraft.block.BlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockRotation

@RegisterItem(RS.ID, "wrench")
class WrenchItem : Item(Settings().group(MyItemGroups.CURED_STORAGE).maxCount(1)) {

    override fun useOnBlock(ctx: ItemUsageContext): ActionResult {
        if (ctx.world.isClient) {
            return ActionResult.CONSUME
        }

        val network: INetwork? = getNodeFromBlockEntity(
            ctx
                .world
                .getBlockEntity(ctx.blockPos)
        )?.network
        val player = ctx.player
        if (network != null && player != null /*&& !network.getSecurityManager().hasPermission(Permission.BUILD, ctx.getPlayer())*/) {
            sendNoPermissionMessage(player)
            return ActionResult.FAIL
        }
        val state: BlockState = ctx.world.getBlockState(ctx.blockPos)
        ctx.world.setBlockState(ctx.blockPos, state.rotate(BlockRotation.CLOCKWISE_90))

        return ActionResult.CONSUME
    }

}