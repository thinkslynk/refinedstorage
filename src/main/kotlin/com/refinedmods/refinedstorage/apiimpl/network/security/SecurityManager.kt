package com.refinedmods.refinedstorage.apiimpl.network.security

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.security.ISecurityCard
import com.refinedmods.refinedstorage.api.network.security.ISecurityCardContainer
import com.refinedmods.refinedstorage.api.network.security.ISecurityManager
import com.refinedmods.refinedstorage.api.network.security.Permission
import net.minecraft.entity.player.PlayerEntity
import java.util.*
import kotlin.collections.HashMap

class SecurityManager(
        private val network: INetwork
) : ISecurityManager {
    private val cards: MutableMap<UUID?, ISecurityCard> = HashMap()
    private var globalCard: ISecurityCard? = null
    override fun hasPermission(permission: Permission, player: PlayerEntity): Boolean {
        if (player.server?.playerManager?.opList?.get(player.gameProfile) != null) {
            return true
        }

        val uuid: UUID = player.gameProfile.id
        return if (!cards.containsKey(uuid)) {
            globalCard?.hasPermission(permission) ?: true
        } else cards[uuid]?.hasPermission(permission) ?: true
    }

    override fun invalidate() {
        cards.clear()
        globalCard = null
        for (node in network.nodeGraph.all()) {
            if (node is ISecurityCardContainer && node.isActive) {
                for (card in node.cards) {
                    checkNotNull(card.owner) { "Owner in #getCards() cannot be null!" }
                    cards[card.owner] = card
                }
                if (node.globalCard != null) {
                    globalCard = node.globalCard
                }
            }
        }
    }

}