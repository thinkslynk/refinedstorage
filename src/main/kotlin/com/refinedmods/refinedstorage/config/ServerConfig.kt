package com.refinedmods.refinedstorage.config

import com.refinedmods.refinedstorage.RS
import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry

@Config(name = "server")
class ServerConfig : ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    val controller: Controller = Controller()

    @ConfigEntry.Gui.CollapsibleObject
    val cable: Cable = Cable()

    class Cable {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 0.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val diskDrive: DiskDrive = DiskDrive()

    class DiskDrive {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 0.0

        @ConfigEntry.Gui.Tooltip
        var diskUsage: Double = 1.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val grid: Grid = Grid()

    class Grid {
        @ConfigEntry.Gui.Tooltip
        var gridUsage: Double = 2.0

        @ConfigEntry.Gui.Tooltip
        var craftingGridUsage: Double = 4.0

        @ConfigEntry.Gui.Tooltip
        var patternGridUsage: Double = 4.0

        @ConfigEntry.Gui.Tooltip
        var fluidGridUsage: Double = 2.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val upgrades: Upgrades = Upgrades()

    class Upgrades {
        @ConfigEntry.Gui.Tooltip
        var rangeUpgradeUsage: Double = 8.0

        @ConfigEntry.Gui.Tooltip
        var speedUpgradeUsage: Double = 2.0

        @ConfigEntry.Gui.Tooltip
        var craftingUpgradeUsage: Double = 5.0

        @ConfigEntry.Gui.Tooltip
        var stackUpgradeUsage: Double = 12.0

        @ConfigEntry.Gui.Tooltip
        var silkTouchUpgradeUsage: Double = 15.0

        @ConfigEntry.Gui.Tooltip
        var fortune1UpgradeUsage: Double = 10.0

        @ConfigEntry.Gui.Tooltip
        var fortune2UpgradeUsage: Double = 12.0

        @ConfigEntry.Gui.Tooltip
        var fortune3UpgradeUsage: Double = 14.0

        @ConfigEntry.Gui.Tooltip
        var regulatorUpgradeUsage: Double = 15.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val storageBlock: StorageBlock = StorageBlock()

    @ConfigEntry.Gui.CollapsibleObject
    val fluidStorageBlock: FluidStorageBlock = FluidStorageBlock()

    @ConfigEntry.Gui.CollapsibleObject
    val externalStorage: ExternalStorage = ExternalStorage()

    class ExternalStorage {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 6.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val importer: Importer = Importer()

    class Importer {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 1.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val exporter: Exporter = Exporter()

    class Exporter {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 1.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val networkReceiver: NetworkReceiver = NetworkReceiver()

    class NetworkReceiver {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 0.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val networkTransmitter: NetworkTransmitter = NetworkTransmitter()

    class NetworkTransmitter {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 64.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val relay: Relay = Relay()

    class Relay {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 1.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val detector: Detector = Detector()

    class Detector {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 2.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val securityManager: SecurityManager = SecurityManager()

    @ConfigEntry.Gui.CollapsibleObject
    val _interface: Interface = Interface()

    class Interface {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 2.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val fluidInterface: FluidInterface = FluidInterface()

    class FluidInterface {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 2.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val wirelessTransmitter: WirelessTransmitter = WirelessTransmitter()

    class WirelessTransmitter {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 8.0

        @ConfigEntry.Gui.Tooltip
        var baseRange: Int = 16

        @ConfigEntry.Gui.Tooltip
        var rangePerUpgrade: Int = 8
    }

    @ConfigEntry.Gui.CollapsibleObject
    val storageMonitor: StorageMonitor = StorageMonitor()

    class StorageMonitor {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 3.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val wirelessGrid: WirelessGrid = WirelessGrid()

    class WirelessGrid {
        @ConfigEntry.Gui.Tooltip
        var useEnergy: Boolean = true

        @ConfigEntry.Gui.Tooltip
        var capacity: Double = 3200.0

        @ConfigEntry.Gui.Tooltip
        var openUsage: Double = 30.0

        @ConfigEntry.Gui.Tooltip
        var extractUsage: Double = 5.0

        @ConfigEntry.Gui.Tooltip
        var insertUsage: Double = 5.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val wirelessFluidGrid: WirelessFluidGrid = WirelessFluidGrid()

    class WirelessFluidGrid {
        @ConfigEntry.Gui.Tooltip
        var useEnergy: Boolean = true

        @ConfigEntry.Gui.Tooltip
        var capacity: Double = 3200.0

        @ConfigEntry.Gui.Tooltip
        var openUsage: Double = 30.0

        @ConfigEntry.Gui.Tooltip
        var extractUsage: Double = 5.0

        @ConfigEntry.Gui.Tooltip
        var insertUsage: Double = 5.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val portableGrid: PortableGrid = PortableGrid()

    class PortableGrid {
        @ConfigEntry.Gui.Tooltip
        var useEnergy: Boolean = true

        @ConfigEntry.Gui.Tooltip
        var capacity: Double = 3200.0

        @ConfigEntry.Gui.Tooltip
        var openUsage: Double = 30.0

        @ConfigEntry.Gui.Tooltip
        var extractUsage: Double = 5.0

        @ConfigEntry.Gui.Tooltip
        var insertUsage: Double = 5.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val constructor: Constructor = Constructor()

    class Constructor {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 3.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val destructor: Destructor = Destructor()

    class Destructor {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 3.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val diskManipulator: DiskManipulator = DiskManipulator()

    class DiskManipulator {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 4.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val crafter: Crafter = Crafter()

    class Crafter {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 4.0

        @ConfigEntry.Gui.Tooltip
        var patternUsage: Double = 1.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val crafterManager: CrafterManager = CrafterManager()

    class CrafterManager {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 8.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val wirelessCraftingMonitor: WirelessCraftingMonitor = WirelessCraftingMonitor()

    class WirelessCraftingMonitor {
        @ConfigEntry.Gui.Tooltip
        var useEnergy: Boolean = true

        @ConfigEntry.Gui.Tooltip
        var capacity: Double = 3200.0

        @ConfigEntry.Gui.Tooltip
        var openUsage: Double = 30.0

        @ConfigEntry.Gui.Tooltip
        var cancelUsage: Double = 5.0

        @ConfigEntry.Gui.Tooltip
        var cancelAllUsage: Double = 10.0
    }

    @ConfigEntry.Gui.CollapsibleObject
    val autocrafting: Autocrafting = Autocrafting()

    class Autocrafting {
        @ConfigEntry.Gui.Tooltip
        var calculationTimeoutMs: Int = 5000
    }

    class FluidStorageBlock {
        @ConfigEntry.Gui.Tooltip
        var sixtyFourKUsage: Double = 2.0

        @ConfigEntry.Gui.Tooltip
        var twoHundredFiftySixKUsage: Double = 4.0

        @ConfigEntry.Gui.Tooltip
        var thousandTwentyFourKUsage: Double = 6.0

        @ConfigEntry.Gui.Tooltip
        var fourThousandNinetySixKUsage: Double = 8.0

        @ConfigEntry.Gui.Tooltip
        var creativeUsage: Double = 10.0
    }

    class StorageBlock {
        @ConfigEntry.Gui.Tooltip
        var oneKUsage: Double = 2.0

        @ConfigEntry.Gui.Tooltip
        var fourKUsage: Double = 4.0

        @ConfigEntry.Gui.Tooltip
        var sixteenKUsage: Double = 6.0

        @ConfigEntry.Gui.Tooltip
        var sixtyFourKUsage: Double = 8.0

        @ConfigEntry.Gui.Tooltip
        var creativeUsage: Double = 10.0
    }

    class SecurityManager {
        @ConfigEntry.Gui.Tooltip
        var usage: Double = 4.0

        @ConfigEntry.Gui.Tooltip
        var usagePerCard: Double = 10.0
    }


    class Controller {
        @ConfigEntry.Gui.Tooltip
        var useEnergy: Boolean = true

        @ConfigEntry.Gui.Tooltip
        var capacity: Double = 32_000.0

        @ConfigEntry.Gui.Tooltip
        var baseUsage: Double = 0.0

        @ConfigEntry.Gui.Tooltip
        var maxTransfer: Double = Double.MAX_VALUE
    }

//    override fun validatePostLoad() {
//
//    }


}