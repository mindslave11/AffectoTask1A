package com.example.zver.affectotask.Tweets;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Zver on 3/11/2017.
 */

public class Bus {
    EventBus eventBus = new EventBus().getDefault();

    public EventBus getEventBus() {
        return eventBus;
    }
}
