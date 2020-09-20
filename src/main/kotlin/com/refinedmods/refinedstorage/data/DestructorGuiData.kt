package com.refinedmods.refinedstorage.data

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.data.sync.SimpleObservable
import com.refinedmods.refinedstorage.data.sync.SimpleObserver
import com.refinedmods.refinedstorage.data.sync.Trackable
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import java.lang.ref.WeakReference
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

data class DestructorGuiData(
    val entity: BaseBlockEntityData = BaseBlockEntityData(),
    val iType: Int = 0,
    val itemFilters: BaseItemHandler = BaseItemHandler(9),
    val fluidFilters: BaseItemHandler = BaseItemHandler(9),
    val upgrades: BaseItemHandler = BaseItemHandler(4)
): SimpleObservable, Trackable<DestructorGuiData> {
    override val observers: HashSet<WeakReference<SimpleObserver>> = hashSetOf()
    override fun getSerializer(): TrackedDataHandler<DestructorGuiData> {
        return DESTRUCTOR_GUI_DATA_SERIALIZER
    }

    private val changeListener = object : InventoryListener<BaseItemHandler> {
        override fun onChanged(handler: BaseItemHandler, slot: Int, reading: Boolean) {
            notifyObservers()
        }
    }

    private val observer = object : SimpleObserver {
        override fun onUpdate() {
            notifyObservers()
        }
    }

    init {
        entity.observers.add(WeakReference(observer))
        itemFilters.addListener(changeListener)
        fluidFilters.addListener(changeListener)
        upgrades.addListener(changeListener)
    }

    companion object{
        val ID = Identifier(RS.ID, "destructor_gui_data")
        val DESTRUCTOR_GUI_DATA_SERIALIZER = object : TrackedDataHandler<DestructorGuiData> {
            override fun write(data: PacketByteBuf, o: DestructorGuiData) {
                BaseBlockEntityData.SERIALIZER.write(data, o.entity)
                TrackedDataHandlerRegistry.INTEGER.write(data, o.iType)
                RSSerializers.INVENTORY_SERIALIZER.write(data, o.itemFilters)
                RSSerializers.INVENTORY_SERIALIZER.write(data, o.fluidFilters)
                RSSerializers.INVENTORY_SERIALIZER.write(data, o.upgrades)
            }

            override fun read(data: PacketByteBuf): DestructorGuiData {
               return DestructorGuiData(
                   BaseBlockEntityData.SERIALIZER.read(data),
                   TrackedDataHandlerRegistry.INTEGER.read(data),
                   RSSerializers.INVENTORY_SERIALIZER.read(data) as BaseItemHandler,
                   RSSerializers.INVENTORY_SERIALIZER.read(data) as BaseItemHandler,
                   RSSerializers.INVENTORY_SERIALIZER.read(data) as BaseItemHandler
               )
            }

            override fun copy(o: DestructorGuiData): DestructorGuiData {
                return DestructorGuiData(
                    BaseBlockEntityData.SERIALIZER.copy(o.entity),
                    o.iType,
                    RSSerializers.INVENTORY_SERIALIZER.copy(o.itemFilters) as BaseItemHandler,
                    RSSerializers.INVENTORY_SERIALIZER.copy(o.fluidFilters) as BaseItemHandler,
                    RSSerializers.INVENTORY_SERIALIZER.copy(o.upgrades) as BaseItemHandler
                )
            }

        }
    }
}