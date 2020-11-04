package dev.etrayed.retrayed.plugin.listener;

import dev.etrayed.retrayed.api.Replay;
import dev.etrayed.retrayed.plugin.RetrayedPlugin;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.record.TickCounter;
import dev.etrayed.retrayed.plugin.replay.RecordingReplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * @author Etrayed
 */
abstract class RecordingListener implements Listener {

    final RetrayedPlugin plugin;

    RecordingListener(RetrayedPlugin plugin) {
        this.plugin = plugin;
    }

    void addEvent(AbstractEvent event, Player player) {
        Replay replay = plugin.currentReplay();

        if(replay instanceof RecordingReplay) {
            ((RecordingReplay) replay).addEvent(TickCounter.currentTick(), event, player.getUniqueId());
        }
    }
}
