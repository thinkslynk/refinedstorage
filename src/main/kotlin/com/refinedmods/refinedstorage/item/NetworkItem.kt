package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.item.INetworkItemProvider
import com.refinedmods.refinedstorage.render.Styles
import com.refinedmods.refinedstorage.util.NetworkUtils.getNodeFromBlockEntity
import com.refinedmods.refinedstorage.util.NetworkUtils.networkNode
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

typealias Consumer<T> = (T) -> Unit

abstract class NetworkItem(
    settings: Settings,
    creative: Boolean,
    energyCapacity: Int
) : EnergyItem(settings, creative, energyCapacity),
    INetworkItemProvider {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack: ItemStack = user.getStackInHand(hand)
        if (!world.isClient) {
            applyNetwork(
                world.server!!,
                stack,
                {
                    /*it
                        .getNetworkItemManager()
                        .open(user, stack, user.inventory.currentItem)*/
                },
                { user.sendMessage(it, true) })
        }
        return TypedActionResult.success(stack)
    }

    inline fun applyNetwork(
        server: MinecraftServer,
        stack: ItemStack,
        onNetwork: Consumer<INetwork>,
        onError: Consumer<Text>
    ) {
        val notFound = TranslatableText("misc.refinedstorage.network_item.not_found")
        if (!isValid(stack)) {
            return onError(notFound)
        }
        val dimension: RegistryKey<World> = getDimension(stack) ?: return onError(notFound)
        val nodeWorld: ServerWorld = server.getWorld(dimension) ?: return onError(notFound)

        nodeWorld.getBlockEntity(
            BlockPos(
                getX(stack),
                getY(stack),
                getZ(stack)
            )
        )

        val network: INetwork = nodeWorld.getBlockEntity(BlockPos(getX(stack), getY(stack), getZ(stack)))
            ?.networkNode?.network ?: return onError(notFound)

        onNetwork(network)
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        if (isValid(stack)) {
            tooltip.add(
                TranslatableText(
                    "misc.refinedstorage.network_item.tooltip",
                    getX(stack),
                    getY(stack),
                    getZ(stack)
                ).setStyle(Styles.GRAY)
            )
        }
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val player = context.player ?: return ActionResult.FAIL
        val stack: ItemStack = player.getStackInHand(context.hand)
        val network: INetwork? = getNodeFromBlockEntity(
            context
                .world
                .getBlockEntity(context.blockPos)
        )?.network
        if (network != null) {
            var tag: CompoundTag? = stack.tag
            if (tag == null) {
                tag = CompoundTag()
            }
            tag.putInt(NBT_NODE_X, network.position.x)
            tag.putInt(NBT_NODE_Y, network.position.y)
            tag.putInt(NBT_NODE_Z, network.position.z)
            tag.putString(
                NBT_DIMENSION,
                context.world.registryKey.value.toString()
            )
            stack.tag = tag
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    /*
    fun shouldCauseReequipAnimation(oldStack: ItemStack?, newStack: ItemStack?, slotChanged: Boolean): Boolean {
        return false
    }*/

    companion object {
        private val NBT_NODE_X: String? = "NodeX"
        private val NBT_NODE_Y: String? = "NodeY"
        private val NBT_NODE_Z: String? = "NodeZ"
        private val NBT_DIMENSION: String? = "Dimension"

        fun getDimension(stack: ItemStack): RegistryKey<World>? {
            if (stack.hasTag() && stack.tag!!.contains(NBT_DIMENSION)) {
                val name: Identifier = Identifier.tryParse(
                    stack.tag!!.getString(NBT_DIMENSION)
                ) ?: return null
                return RegistryKey.of(Registry.DIMENSION, name)
            }
            return null
        }

        fun getX(stack: ItemStack): Int {
            return stack.tag!!.getInt(NBT_NODE_X)
        }

        fun getY(stack: ItemStack): Int {
            return stack.tag!!.getInt(NBT_NODE_Y)
        }

        fun getZ(stack: ItemStack): Int {
            return stack.tag!!.getInt(NBT_NODE_Z)
        }

        fun isValid(stack: ItemStack): Boolean {
            return (stack.hasTag()
                    && stack.tag!!.contains(NBT_NODE_X)
                    && stack.tag!!.contains(NBT_NODE_Y)
                    && stack.tag!!.contains(NBT_NODE_Z)
                    && stack.tag!!.contains(NBT_DIMENSION))
        }
    }
}