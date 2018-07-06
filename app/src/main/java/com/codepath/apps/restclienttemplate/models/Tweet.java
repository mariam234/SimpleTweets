package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {

    // list out the attributes
    public String body;
    public long uid; // database ID for the tweet
    public User user;
    public String createdAt;
    public int retweet_count;
    public int likes_count;
    public boolean retweeted;
    public boolean favorited;

    // empty constructor needed by the Parceler library
    public Tweet() {
    }

    // deserialize the JSON
    // throws exception back to caller
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        // extract the values from JSON
        // make sure keys are spelled correctly
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.retweet_count = jsonObject.getInt("retweet_count");
        tweet.likes_count = jsonObject.getInt("favourites_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");

        return tweet;
    }
}
