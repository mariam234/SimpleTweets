package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    // list of tweets and context
    List<Tweet> mTweets;
    Context context;

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // get context and create inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvHandle.setText("@" + tweet.user.screenName);
        long retweet_count = tweet.retweet_count;
        long likes_count = tweet.likes_count;
        holder.tvRetweetCount.setText(AbbreviateNumber.format(retweet_count));
        holder.tvLikesCount.setText(AbbreviateNumber.format(likes_count));

        ParseRelativeDate parseRelativeDate = new ParseRelativeDate();
        holder.tvTimestamp.setText(parseRelativeDate.getRelativeTimeAgo(tweet.createdAt));

        // rounded corners transformation
        RoundedCornersTransformation transformation = new RoundedCornersTransformation(
                20,
                20
        );

        // put glide image options in variable
        RequestOptions options = RequestOptions.bitmapTransform(transformation);

        // load image using glide
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .apply(options)
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create the ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfileImage;
        TextView tvUsername;
        TextView tvBody;
        TextView tvTimestamp;
        TextView tvHandle;
        TextView tvRetweetCount;
        TextView tvLikesCount;

        public ViewHolder (View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUserName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);

            // set this as itemView's onclicklistener
            itemView.setOnClickListener(this);

        }

        // go to tweet details activity on tweet click
        @Override
        public void onClick(View view) {
            // get item position
            int position = getAdapterPosition();
            // get the tweet at the position from tweets array
            Tweet tweet = mTweets.get(position);
            // create intent for the new activity
            Intent intent = new Intent (context, DetailsActivity.class);
            // serialize the tweet using parceler, use its short name as a key
            intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            // show the activity
            context.startActivity(intent);
        }
    }
}
