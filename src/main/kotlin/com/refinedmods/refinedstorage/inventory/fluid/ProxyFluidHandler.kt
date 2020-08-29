package com.refinedmods.refinedstorage.inventory.fluid
//
//import net.minecraft.fluid.Fluid
//import reborncore.common.fluid.container.FluidInstance
//import reborncore.common.util.Tank
//
//class ProxyFluidHandler(
//        private val insertHandler: Tank,
//        private val extractHandler: Tank
//) : IFluidHandler {
//    fun getTanks(): Int {
//        return 2
//    }
//
//    fun getFluidInTank(tank: Int): FluidInstance {
//        return if (tank == 0) insertHandler.getFluidInTank(0) else extractHandler.getFluidInTank(0)
//    }
//
//    fun getTankCapacity(tank: Int): Int {
//        return if (tank == 0) insertHandler.getTankCapacity(0) else extractHandler.getTankCapacity(0)
//    }
//
//    fun isFluidValid(tank: Int, stack: FluidInstance): Boolean {
//        return if (tank == 0) insertHandler.isFluidValid(0, stack) else extractHandler.isFluidValid(0, stack)
//    }
//
//    fun fill(resource: FluidInstance, action: FluidAction): Int {
//        return insertHandler.fill(resource, action)
//    }
//
//    fun drain(resource: FluidInstance?, action: FluidAction): FluidInstance {
//        return extractHandler.drain(resource, action)
//    }
//
//    fun drain(maxDrain: Int, action: Fluid FluidAction): FluidInstance {
//        return extractHandler.drain(maxDrain, action)
//    }
//
//}