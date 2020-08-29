package com.refinedmods.refinedstorage

//import com.refinedmods.refinedstorage.config.ClientConfig
//import com.refinedmods.refinedstorage.config.ServerConfig
//import com.refinedmods.refinedstorage.network.NetworkHandler
import com.refinedmods.refinedstorage.config.ClientConfig
import com.refinedmods.refinedstorage.config.ServerConfig
import com.refinedmods.refinedstorage.extensions.DOUBLE
import com.thinkslynk.fabric.generated.BlockRegistryGenerated
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import com.thinkslynk.fabric.generated.ItemRegistryGenerated
import com.thinkslynk.fabric.generated.BlockItemRegistryGenerated
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import reborncore.common.config.Configuration

class RS: ModInitializer {
    companion object{
        val log = getCustomLogger(RS::class)
        const val ID = "refinedstorage"
    }
//    val NETWORK_HANDLER = NetworkHandler()
//    val SERVER_CONFIG = ServerConfig()
//    val CLIENT_CONFIG = ClientConfig()

    override fun onInitialize() {
        Configuration(ServerConfig::class.java, ID)
        Configuration(ClientConfig::class.java, ID)

//        BlockRegistryGenerated.register()
        ItemRegistryGenerated.register()
        BlockRegistryGenerated.register()
        BlockItemRegistryGenerated.register()
        BlockEntityRegistryGenerated.register()
        TrackedDataHandlerRegistry.register(DOUBLE)
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