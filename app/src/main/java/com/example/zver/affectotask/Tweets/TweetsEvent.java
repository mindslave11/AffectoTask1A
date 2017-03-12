package com.example.zver.affectotask.Tweets;

import java.util.List;

/**
 * Created by Zver on 3/12/2017.
 */

public class TweetsEvent {

    public Tweet tweet;

    public TweetsEvent(Tweet tweet){
        this.tweet = tweet;
    }

    public Tweet getTweet() {
        return tweet;
    }
}
