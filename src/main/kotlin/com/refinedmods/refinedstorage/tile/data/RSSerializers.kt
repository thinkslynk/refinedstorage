package com.refinedmods.refinedstorage.tile.data

import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.tile.ClientNode
import com.refinedmods.refinedstorage.util.AccessTypeUtils
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.util.*

object RSSerializers {
    fun registerAll() {
        listOf(
                CLIENT_NODE_SERIALIZER,
                ACCESS_TYPE_SERIALIZER,
                LONG_SERIALIZER,
                DOUBLE_SERIALIZER,
                OPTIONAL_RESOURCE_LOCATION_SERIALIZER,
                LIST_OF_SET_SERIALIZER
        ).forEach{
            TrackedDataHandlerRegistry.register(it)
        }
    }

    val CLIENT_NODE_SERIALIZER = object: TrackedDataHandler<List<ClientNode>> {
        override fun write(data: PacketByteBuf, nodes: List<ClientNode>) {
            data.writeInt(nodes.size)
            for (node in nodes) {
                data.writeItemStack(node.stack)
                data.writeInt(node.amount)
                data.writeDouble(node.energyUsage)
            }
        }

        override fun read(data: PacketByteBuf): List<ClientNode> {
            val nodes: MutableList<ClientNode> = ArrayList()
            val size: Int = data.readInt()
            for (i in 0 until size) {
                nodes.add(ClientNode(data.readItemStack(), data.readInt(), data.readDouble()))
            }
            return nodes
        }

        override fun copy(nodes: List<ClientNode>): List<ClientNode> {
            return nodes
        }
    }

    // TODO fluid
//    val FLUID_STACK_SERIALIZER: TrackedDataHandler<FluidStack> = object : TrackedDataHandler<FluidStack?>() {
//        override fun write(buf: PacketByteBuf?, value: FluidStack) {
//            value.writeToPacket(buf)
//        }
//
//        override fun read(buf: PacketByteBuf?): FluidStack {
//            return FluidStack.readFromPacket(buf)
//        }
//    }

    val ACCESS_TYPE_SERIALIZER = object : TrackedDataHandler<AccessType> {
        override fun write(buf: PacketByteBuf, value: AccessType) {
            buf.writeInt(value.getId())
        }

        override fun read(buf: PacketByteBuf): AccessType {
            return AccessTypeUtils.getAccessType(buf.readInt())
        }

        override fun copy(accessType: AccessType): AccessType {
            return accessType
        }
    }

    val LONG_SERIALIZER = object : TrackedDataHandler<Long> {
        override fun write(buf: PacketByteBuf, value: Long) {
            buf.writeLong(value)
        }

        override fun read(buf: PacketByteBuf): Long {
            return buf.readLong()
        }

        override fun copy(v: Long): Long {
            return v
        }
    }

    val DOUBLE_SERIALIZER: TrackedDataHandler<Double> = object : TrackedDataHandler<Double> {
        override fun write(packetByteBuf: PacketByteBuf, double: Double) {
            packetByteBuf.writeDouble(double)
        }

        override fun read(packetByteBuf: PacketByteBuf): Double {
            return packetByteBuf.readDouble()
        }

        override fun copy(double: Double): Double {
            return double
        }
    }

    val OPTIONAL_RESOURCE_LOCATION_SERIALIZER = object : TrackedDataHandler<Optional<Identifier>> {
        override fun write(buf: PacketByteBuf, value: Optional<Identifier>) {
            buf.writeBoolean(value.isPresent)
            value.ifPresent{
                buf.writeIdentifier(it)
            }
        }

        override fun read(buf: PacketByteBuf): Optional<Identifier> {
            return if (!buf.readBoolean()) {
                Optional.empty<Identifier>()
            } else Optional.of(buf.readIdentifier())
        }

        override fun copy(v: Optional<Identifier>): Optional<Identifier> {
            return v
        }
    }

    val LIST_OF_SET_SERIALIZER = object : TrackedDataHandler<List<Set<Identifier>>>{
        override fun write(buf: PacketByteBuf, value: List<Set<Identifier>>) {
            buf.writeInt(value.size)
            for (values in value) {
                buf.writeInt(values.size)
                values.forEach{
                    buf.writeIdentifier(it)
                }
            }
        }

        override fun read(buf: PacketByteBuf): List<Set<Identifier>> {
            val value: MutableList<Set<Identifier>> = ArrayList()
            val size: Int = buf.readInt()
            for (i in 0 until size) {
                val setSize: Int = buf.readInt()
                val values: MutableSet<Identifier> = HashSet()
                for (j in 0 until setSize) {
                    values.add(buf.readIdentifier())
                }
                value.add(values)
            }
            return value
        }

        override fun copy(v: List<Set<Identifier>>): List<Set<Identifier>> {
            return v
        }
    }
}