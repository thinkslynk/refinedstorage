@file:Suppress("unused")

package com.refinedmods.refinedstorage.extensions

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import com.thinkslynk.fabric.generated.ItemRegistryGenerated
import com.thinkslynk.fabric.generated.MyItemGroups
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.*

@Deprecated("fabric", replaceWith = ReplaceWith("this.getStackInHand(hand)"))
fun PlayerEntity.getHeldItem(hand: Hand): ItemStack = getStackInHand(hand)
@Deprecated("fabric", replaceWith = ReplaceWith("this.isClient")) @JvmName("isRemoteJava")
fun World.isRemote(): Boolean = this.isClient
@Deprecated("fabric", replaceWith = ReplaceWith("this.isClient"))
val World.isRemote: Boolean get() = this.isClient
@Deprecated("fabric", replaceWith = ReplaceWith("this.isSneaking"))
fun PlayerEntity.isCrouching(): Boolean = isSneaking
@Deprecated("fabric", replaceWith = ReplaceWith("this.increment(amount)"))
fun ItemStack.grow(amount: Int) = increment(amount)
@Deprecated("fabric", replaceWith = ReplaceWith("this.decrement(amount)"))
fun ItemStack.shrink(amount: Int) = decrement(amount)
@Deprecated("fabric", replaceWith = ReplaceWith("Settings","net.minecraft.item.Item.Settings"))
typealias Properties = Item.Settings
@Deprecated("fabric", replaceWith = ReplaceWith("CompoundTag","net.minecraft.nbt.CompoundTag"))
typealias CompoundNBT = CompoundTag
@Deprecated("fabric", replaceWith = ReplaceWith("ListTag","net.minecraft.nbt.ListTag"))
typealias ListNBT = ListTag
@Deprecated("fabric", replaceWith = ReplaceWith("Identifier","net.minecraft.util.Identifier"))
typealias ResourceLocation = Identifier
@Deprecated("fabric", replaceWith = ReplaceWith("this.insertStack(itemStack)"))
fun PlayerInventory.addItemStackToInventory(itemStack: ItemStack) = this.insertStack(itemStack)
@Deprecated("fabric", replaceWith = ReplaceWith("this.putUuid(key, uuid)"))
fun CompoundTag.putUniqueId(key: String, uuid: UUID) = this.putUuid(key, uuid)
@Deprecated("fabric", replaceWith = ReplaceWith("this.containsUuid(key)"))
fun CompoundTag.hasUniqueId(key: String): Boolean = this.containsUuid(key)
@Deprecated("fabric", ReplaceWith("net.minecraft.item.ItemPlacementContext"))
typealias BlockItemUseContext = ItemPlacementContext
@Deprecated("fabric", ReplaceWith("net.minecraft.util.hit.BlockHitResult"))
typealias BlockRayTraceResult = BlockHitResult
@Deprecated("fabric", ReplaceWith("net.minecraft.entity.data.TrackedDataHandlerRegistry"))
typealias DataSerializers = net.minecraft.entity.data.TrackedDataHandlerRegistry
@Deprecated("fabric", ReplaceWith("this.offsetX"))
fun Direction.getXOffset() = offsetX
@Deprecated("fabric", ReplaceWith("this.offsetY"))
fun Direction.getYOffset() = offsetY
@Deprecated("fabric", ReplaceWith("this.offsetZ"))
fun Direction.getZOffset() = offsetZ
@Deprecated("fabric", ReplaceWith("getStack(i)"))
fun Inventory.getStackInSlot(i: Int) = getStack(i)

        @Deprecated("generated", replaceWith = ReplaceWith("MyItemGroups.CURED_STORAGE","com.thinkslynk.fabric.generated.MyItemGroups"))//, level = DeprecationLevel.ERROR)
val RS.Companion.MAIN_GROUP: ItemGroup
    get() = MyItemGroups.CURED_STORAGE


@Deprecated("replace with ItemRegistryGenerated", replaceWith = ReplaceWith("ItemRegistryGenerated","com.thinkslynk.fabric.generated.ItemRegistryGenerated"))
val RSItems = ItemRegistryGenerated

@Deprecated("kotlin", replaceWith = ReplaceWith("this.name"))
fun ItemStorageType.getName() = name

@Deprecated("idk")
object ItemHandlerHelper{
    @Deprecated("idk")
    fun copyStackWithSize(stack: ItemStack, size:Int) = stack.copy().also{it.count = size}!!
}
