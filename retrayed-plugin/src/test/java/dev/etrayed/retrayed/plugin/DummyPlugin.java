package dev.etrayed.retrayed.plugin;

import dev.etrayed.retrayed.api.PluginPurpose;
import dev.etrayed.retrayed.api.Replay;
import dev.etrayed.retrayed.plugin.event.EventIteratorFactory;
import dev.etrayed.retrayed.plugin.event.EventRegistry;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Etrayed
 */
public class DummyPlugin implements IRetrayedPlugin {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(0);

    private final EventIteratorFactory eventIteratorFactory = new EventIteratorFactory(eventRegistry());

    @Override
    public Future<Replay> initReplay(int replayId, PluginPurpose purpose) {
        return null;
    }

    @Override
    public Replay currentReplay() {
        return null;
    }

    @Override
    public PluginPurpose pluginPurpose() {
        return PluginPurpose.NONE;
    }

    @Override
    public ScheduledExecutorService executorService() {
        return executorService;
    }

    @Override
    public EventIteratorFactory eventIteratorFactory() {
        return eventIteratorFactory;
    }

    @Override
    public Class<?> customStorageClass() {
        return null;
    }

    @Override
    public EventRegistry eventRegistry() {
        return null;
    }
}
