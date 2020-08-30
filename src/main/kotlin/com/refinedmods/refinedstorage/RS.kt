package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.apiimpl.network.NetworkListener
import com.refinedmods.refinedstorage.block.ConstructorBlock
import com.refinedmods.refinedstorage.config.ClientConfig
import com.refinedmods.refinedstorage.config.ServerConfig
import com.refinedmods.refinedstorage.container.ConstructorScreenHandler
import com.refinedmods.refinedstorage.container.FilterContainer
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.network.NetworkHandler
import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import com.thinkslynk.fabric.generated.BlockItemRegistryGenerated
import com.thinkslynk.fabric.generated.BlockRegistryGenerated
import com.thinkslynk.fabric.generated.ItemRegistryGenerated
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import reborncore.common.config.Configuration

class RS: ModInitializer {
    companion object{
        val log = getCustomLogger(RS::class)
        const val ID = "refinedstorage"

        val FILTER_SCREEN_HANDLER: ScreenHandlerType<FilterContainer> = ScreenHandlerRegistry.registerSimple(Identifier(RS.ID, "filter_screen"))
        { windowId, playerInventory->
            FilterContainer(playerInventory.player, ItemStack.EMPTY, windowId)
        }

        val CONSTRUCTOR_SCREEN_HANDLER: ScreenHandlerType<ConstructorScreenHandler> = ScreenHandlerRegistry.registerSimple(Identifier(ID, ConstructorBlock.ID))
        { windowId, playerInventory->
            ConstructorScreenHandler(ScreenHandlerContext.EMPTY, playerInventory.player, windowId)
        }
        val NETWORK_HANDLER = NetworkHandler()
    }

    override fun onInitialize() {
        Configuration(ServerConfig::class.java, ID)
        Configuration(ClientConfig::class.java, ID)

        ItemRegistryGenerated.register()
        BlockRegistryGenerated.register()
        BlockItemRegistryGenerated.register()
        BlockEntityRegistryGenerated.register()
        RSSerializers.registerAll()

        ServerTickEvents.END_WORLD_TICK.register(NetworkListener())


        // TODO Register stuff!
//        DistExecutor.safeRunWhenOn(Dist.CLIENT, { { ClientSetup() } })
//        MinecraftForge.EVENT_BUS.register(ServerSetup())
//        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec())
//        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec())
//        val commonSetup = CommonSetup()
//        FMLJavaModLoadingContext.get().getModEventBus().addListener({ e: ? -> commonSetup.onCommonSetup(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block::class.java, { e:<net.minecraft.block.Block?> -> commonSetup.onRegisterBlocks(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(BlockEntityType::class.java, { e:<<>?> -> commonSetup.onRegisterTiles(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item::class.java, { e:<net.minecraft.item.Item?> -> commonSetup.onRegisterItems(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer::class.java, { e:<<>?> -> commonSetup.onRegisterRecipeSerializers(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType::class.java, { e:<<>?> -> commonSetup.onRegisterContainers(e) })
//        deliver()
        log.info("Initialized!")
    }
}