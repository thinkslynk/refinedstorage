package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode
import com.refinedmods.refinedstorage.block.ConstructorBlock
import com.refinedmods.refinedstorage.container.ConstructorScreenHandler
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.thinkslynk.fabric.annotations.registry.RegisterBlockEntity
import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@RegisterBlockEntity(RS.ID, ConstructorBlock.ID, ["CONSTRUCTOR_BLOCK"])
class ConstructorTile:
        NetworkNodeTile<ConstructorNetworkNode, ConstructorTile>(BlockEntityRegistryGenerated.CONSTRUCTOR_TILE),
        NamedScreenHandlerFactory
{
    override fun createNode(world: World, pos: BlockPos): ConstructorNetworkNode {
        return ConstructorNetworkNode(world, pos)
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return ConstructorScreenHandler(ScreenHandlerContext.create(world, pos), player, syncId)
    }

    override fun getDisplayName(): Text {
        return TranslatableText("gui.refinedstorage.constructor")
    }

    companion object {
        val log = getCustomLogger(ConstructorTile::class)
        val COMPARE: TileDataParameter<Int, ConstructorTile> = IComparable.createParameter()
        val TYPE: TileDataParameter<Int, ConstructorTile> = IType.createParameter()
        val DROP = TileDataParameter<Boolean, ConstructorTile>(
                false,
                TrackedDataHandlerRegistry.BOOLEAN,
                { t: ConstructorTile -> t.node.isDrop },
                { t: ConstructorTile, v: Boolean ->
                    t.node.isDrop = v
                    t.node.markDirty()
                }
        )
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(TYPE)
        dataManager.addWatchedParameter(DROP)
    }

}