package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.api.storage.StorageType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import com.refinedmods.refinedstorage.extensions.isServer
import com.refinedmods.refinedstorage.render.Styles
import com.thinkslynk.fabric.generated.ItemRegistryGenerated
import com.thinkslynk.fabric.generated.MyItemGroups
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import java.util.*


open class StorageDiskItem(private val type: ItemStorageType) : Item(
    Settings().group(MyItemGroups.CURED_STORAGE).maxCount(1)
), IStorageDiskProvider {
    override fun inventoryTick(
        stack: ItemStack,
        world: World,
        entity: Entity,
        slot: Int,
        selected: Boolean
    ) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (world.isServer() && !stack.hasTag()) {
            val id = UUID.randomUUID()
            val manager = API.getStorageDiskManager(world)
            manager[id] = API.createDefaultItemDisk(world, getCapacity(stack))
            manager.markForSaving()
            setId(stack, id)
        }
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        if (isValid(stack)) {
            val id = getId(stack)
            API.storageDiskSync.sendRequest(id)
            val data: StorageDiskSyncData? = API.storageDiskSync.getData(id)
            if (data != null) {
                if (data.getCapacity() == -1) {
                    tooltip.add(
                        TranslatableText(
                            "misc.refinedstorage.storage.stored",
                            API.quantityFormatter.format(data.getStored())
                        ).setStyle(Styles.GRAY)
                    )
                } else {
                    tooltip.add(
                        TranslatableText(
                            "misc.refinedstorage.storage.stored_capacity",
                            API.quantityFormatter.format(data.getStored()),
                            API.quantityFormatter
                                .format(data.getCapacity())
                        ).setStyle(Styles.GRAY)
                    )
                }
            }
            if (context.isAdvanced) {
                tooltip.add(LiteralText(id.toString()).setStyle(Styles.GRAY))
            }
        }
    }

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val diskStack: ItemStack = player.getStackInHand(hand)
        if (world.isServer() && player.isSneaking && type != ItemStorageType.CREATIVE) {
            val disk = API.getStorageDiskManager(world).getByStack(diskStack)
            if (disk != null && disk.getStored() == 0) {
                val storagePart = ItemStack(StoragePartItem.getByType(type), diskStack.count)
                if (!player.inventory.insertStack(storagePart.copy())) {
                    ItemScatterer.spawn(world, player.x, player.y, player.z, storagePart)
                }
                API.getStorageDiskManager(world).remove(getId(diskStack))
                API.getStorageDiskManager(world).markForSaving()
                return TypedActionResult.success(ItemStack(ItemRegistryGenerated.STORAGE_HOUSING_ITEM))
            }
        }
        return TypedActionResult.pass(diskStack)
    }

    /* TODO look into adding this, essentially makes the item not despawn
    fun getEntityLifespan(stack: ItemStack?, world: World?): Int {
        return Int.MAX_VALUE
    }*/

    override fun getId(disk: ItemStack): UUID {
        return disk.tag!!.getUuid(NBT_ID)
    }

    override fun setId(disk: ItemStack, id: UUID) {
        disk.tag = CompoundTag()
        disk.tag!!.putUuid(NBT_ID, id)
    }

    override fun isValid(disk: ItemStack): Boolean {
        return disk.hasTag() && disk.tag!!.containsUuid(NBT_ID)
    }

    override fun getCapacity(disk: ItemStack): Int {
        return type.capacity
    }

    override fun getType(): StorageType {
        return StorageType.ITEM
    }

    companion object {
        private val NBT_ID: String = "Id"
        private val LOGGER = LogManager.getLogger(StorageDiskItem::class.java)
    }
}





