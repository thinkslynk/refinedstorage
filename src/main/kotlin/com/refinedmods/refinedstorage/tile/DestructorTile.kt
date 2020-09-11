package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.node.DestructorNetworkNode
import com.refinedmods.refinedstorage.block.DestructorBlock
import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import com.refinedmods.refinedstorage.data.DestructorGuiData
import com.refinedmods.refinedstorage.data.sync.SimpleObserver
import com.refinedmods.refinedstorage.gui.screenhandlers.DestructorScreenHandler
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.thinkslynk.fabric.annotations.registry.RegisterBlockEntity
import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import java.lang.ref.WeakReference
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.BiConsumer
import java.util.function.Function
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

@RegisterBlockEntity(RS.ID, DestructorBlock.ID, ["DESTRUCTOR_BLOCK"])
class DestructorTile :
    NetworkNodeTile<DestructorNetworkNode>(BlockEntityRegistryGenerated.DESTRUCTOR_TILE),
    ExtendedScreenHandlerFactory
{

    companion object {
        val COMPARE: TileDataParameter<Int, DestructorTile> = IComparable.createParameter()
        val WHITELIST_BLACKLIST: TileDataParameter<Int, DestructorTile> = IWhitelistBlacklist.createParameter()
        val TYPE: TileDataParameter<Int, DestructorTile> = IType.createParameter()
        val PICKUP = TileDataParameter<Boolean, DestructorTile>(
                false,
                TrackedDataHandlerRegistry.BOOLEAN,
                Function { t: DestructorTile -> t.node.isPickupItem },
                BiConsumer { t: DestructorTile, v: Boolean ->
                    t.node.isPickupItem = v
                    t.node.markDirty()
                }
        )
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST)
        dataManager.addWatchedParameter(TYPE)
        dataManager.addWatchedParameter(PICKUP)
    }

    private val guiListener = object: SimpleObserver {
        override fun onUpdate() {
            world?.let {
                if(!it.isClient) {
                    API.getNetworkNodeManager(it as ServerWorld).markDirty()
                }
            }
        }
    }

    private fun asGuiData(): DestructorGuiData {
        val ret = DestructorGuiData(
            entity = BaseBlockEntityData(pos, node.direction),
            iType = node.type,
            itemFilters = node.itemFilters,
            upgrades = node.upgrades
        )

        ret.observers.add(WeakReference(guiListener))
        return ret
    }

    override fun createNode(world: World, pos: BlockPos): DestructorNetworkNode = DestructorNetworkNode(world, pos)


    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return DestructorScreenHandler(
            syncId,
            player,
            data = asGuiData()
        )
    }


    override fun getDisplayName(): Text {
        return TranslatableText("gui.refinedstorage.destructor")
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        val data = asGuiData()
        data.save(buf, data)
    }
}