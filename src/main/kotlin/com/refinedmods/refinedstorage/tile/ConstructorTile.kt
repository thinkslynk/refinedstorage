package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.Network
import com.refinedmods.refinedstorage.apiimpl.network.NetworkNodeManager
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode
import com.refinedmods.refinedstorage.block.ConstructorBlock
import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import com.refinedmods.refinedstorage.data.ConstructorGuiData
import com.refinedmods.refinedstorage.data.sync.SimpleObserver
import com.refinedmods.refinedstorage.gui.screenhandlers.ConstructorScreenHandler
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.thinkslynk.fabric.annotations.registry.RegisterBlockEntity
import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import java.lang.ref.WeakReference
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.BiConsumer
import java.util.function.Function
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

@RegisterBlockEntity(RS.ID, ConstructorBlock.ID, ["CONSTRUCTOR_BLOCK"])
class ConstructorTile:
    NetworkNodeTile<ConstructorNetworkNode>(BlockEntityRegistryGenerated.CONSTRUCTOR_TILE),
    ExtendedScreenHandlerFactory
{
    private val guiListener = object: SimpleObserver{
        override fun onUpdate() {
            world?.let {
                if(!it.isClient) {
                    API.getNetworkNodeManager(it as ServerWorld).markDirty()
                }
            }
        }
    }

    private fun asGuiData(): ConstructorGuiData {
        val ret = ConstructorGuiData(
            entity = BaseBlockEntityData(pos, node.direction),
            iType = node.type,
            itemFilters = node.itemFilters,
            upgrades = node.upgrades
        )

        ret.observers.add(WeakReference(guiListener))
        return ret
    }

    override fun createNode(world: World, pos: BlockPos): ConstructorNetworkNode {
        return ConstructorNetworkNode(world, pos)
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return ConstructorScreenHandler(
            syncId,
            player,
            data = asGuiData()
        )
    }


    override fun getDisplayName(): Text {
        return TranslatableText("gui.refinedstorage.constructor")
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        val constructorData = asGuiData()
        constructorData.save(buf, constructorData)
    }

    companion object {
        val COMPARE: TileDataParameter<Int, ConstructorTile> = IComparable.createParameter()
        val TYPE: TileDataParameter<Int, ConstructorTile> = IType.createParameter()
        val DROP = TileDataParameter<Boolean, ConstructorTile>(
                false,
                TrackedDataHandlerRegistry.BOOLEAN,
                Function { t: ConstructorTile -> t.node.isDrop },
                BiConsumer { t: ConstructorTile, v: Boolean ->
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