package dev.etrayed.retrayed.plugin;

import dev.etrayed.retrayed.api.PluginPurpose;
import dev.etrayed.retrayed.api.Replay;
import dev.etrayed.retrayed.api.RetrayedAPI;
import dev.etrayed.retrayed.plugin.event.EventIteratorFactory;
import dev.etrayed.retrayed.plugin.event.EventRegistry;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Etrayed
 */
public interface IRetrayedPlugin extends RetrayedAPI {

    @Override
    Future<Replay> initReplay(int replayId, PluginPurpose purpose);

    @Override
    Replay currentReplay();

    @Override
    PluginPurpose pluginPurpose();

    ScheduledExecutorService executorService();

    EventIteratorFactory eventIteratorFactory();

    Class<?> customStorageClass();

    EventRegistry eventRegistry();
}
