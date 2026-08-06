package com.witchcraft.coven;

import com.witchcraft.Witchcraft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Listens for chat messages to detect coven spell incantations.
 */
public class CovenSpellListener implements Listener {

    private final Witchcraft plugin;

    public CovenSpellListener(Witchcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Check if player is in a coven
        if (plugin.getCovenManager().getCovenForMember(player.getUniqueId()) == null) {
            return;
        }

        // Check if this message matches any coven spell incantation line
        CovenSpell spell = plugin.getCovenSpellRegistry().findByMessage(message);
        if (spell == null) {
            return;
        }

        // Don't cancel the message - let it broadcast
        // The CovenSpell handler will broadcast its own messages
        // We just need to process the incantation line
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            spell.onPlayerSpeak(player, message);
        });
    }
}
