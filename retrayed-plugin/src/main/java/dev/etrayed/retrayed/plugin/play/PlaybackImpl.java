package dev.etrayed.retrayed.plugin.play;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.etrayed.retrayed.api.event.TimedEvent;
import dev.etrayed.retrayed.api.play.Playback;
import dev.etrayed.retrayed.api.play.PlaybackPacketListener;
import dev.etrayed.retrayed.plugin.RetrayedPlugin;
import dev.etrayed.retrayed.plugin.event.AbstractEvent;
import dev.etrayed.retrayed.plugin.stage.ReplayStage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Etrayed
 */
public class PlaybackImpl implements Playback, Runnable {

    private final RetrayedPlugin plugin;

    private Multimap<Integer, TimedEvent> timedEventMultimap;

    private Map<UUID, ReplayStage> stages;

    private AtomicInteger currentTick;

    private AtomicBoolean paused;

    private Lock lock;

    private BukkitTask task;

    public PlaybackImpl(RetrayedPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        this.timedEventMultimap = ArrayListMultimap.create(plugin.currentReplay().events().size(), 3);
        this.stages = new HashMap<>();

        plugin.currentReplay().events().forEach(timedEvent -> timedEventMultimap.put(timedEvent.tick(), timedEvent));
        plugin.currentReplay().recordedPlayers().forEach(uuid -> stages.put(uuid, new ReplayStage(this)));

        this.currentTick = new AtomicInteger();
        this.paused = new AtomicBoolean();
        this.lock = new ReentrantLock();
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 1, 1);
    }

    @Override
    public void run() {
        if(paused.get()) {
            return;
        }

        lockAndRun(this::runTick);
    }

    private void runTick() {
        int tick = currentTick.getAndIncrement();

        for (TimedEvent timedEvent : timedEventMultimap.get(tick)) {
            if(stages.containsKey(timedEvent.receiver())) {
                ((AbstractEvent) timedEvent.event()).recreate(stages.get(timedEvent.receiver()));
            }
        }
    }

    public void forward(int ticks) {
        if(ticks <= 0) {
            return;
        }

        lockAndRun(() -> {
            for (int i = 0; i < ticks; i++) {
                runTick();
            }
        });
    }

    public void rewind(int ticks) {
        if(ticks <= 0 || currentTick.get() <= 0) {
            return;
        }

        lockAndRun(() -> {
            for (int i = 0; i < ticks && currentTick.get() >= 0; i++) {
                int tick = currentTick.decrementAndGet();

                for (TimedEvent timedEvent : timedEventMultimap.get(tick)) {
                    if(stages.containsKey(timedEvent.receiver())) {
                        ((AbstractEvent) timedEvent.event()).undo(stages.get(timedEvent.receiver()));
                    }
                }
            }
        });
    }

    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    public boolean isPaused() {
        return paused.get();
    }

    public void reset() {
        lockAndRun(() -> {
            if(task != null) {
                task.cancel();
            }
        });
    }

    private void lockAndRun(Runnable runnable) {
        lock.lock();

        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    public void addListener(UUID recordedPlayer, PlaybackPacketListener listener) {
        if(stages.containsKey(recordedPlayer)) {
            stages.get(recordedPlayer).listeners().add(listener);
        }
    }

    public boolean isListening(UUID recordedPlayer, PlaybackPacketListener listener) {
        return stages.containsKey(recordedPlayer) && stages.get(recordedPlayer).listeners().contains(listener);
    }

    public void removeListener(UUID recordedPlayer, PlaybackPacketListener listener) {
        if(stages.containsKey(recordedPlayer)) {
            stages.get(recordedPlayer).listeners().remove(listener);
        }
    }
}
