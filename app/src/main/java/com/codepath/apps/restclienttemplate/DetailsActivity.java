package com.codepath.apps.restclienttemplate;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    MenuItem miActionProgressItem;
    TextView tvBody;
    TextView tvUsername;
    TextView tvHandle;
    TextView tvTimestamp;
    TextView tvRetweetCount;
    TextView tvLikesCount;
    ImageView ivProfileImage;
    ImageView ivRetweet;
    ImageView ivReply;
    ImageView ivLike;

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTweetDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        showProgressBar();

        hideProgressBar();
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            // go back to timeline if close button is pressed
            case R.id.miClose:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void setTweetDetails() {
        // unwrap passed in tweet
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        // lookup view ids and bind to variables
        tvBody = findViewById(R.id.tvBody);
        tvUsername = findViewById(R.id.tvUsername);
        tvHandle = findViewById(R.id.tvHandle);
        tvLikesCount = findViewById(R.id.tvLikesCount);
        tvRetweetCount = findViewById(R.id.tvRetweetCount);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivProfileImage = findViewById(R.id.ivProfileImage);

       // set detail fields according to tweet
        tvBody.setText(tweet.body);
        tvUsername.setText(tweet.user.name);
        tvHandle.setText("@" + tweet.user.screenName);
        tvTimestamp.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.createdAt));
        tvLikesCount.setText(insertCommas(String.valueOf(tweet.likesCount)));
        tvRetweetCount.setText(insertCommas(String.valueOf(tweet.retweetCount)));

        // rounded corners transformation
        RoundedCornersTransformation transformation = new RoundedCornersTransformation(
                20,
                20
        );

        // put glide image options in variable
        RequestOptions options = RequestOptions.bitmapTransform(transformation);

        // load image using glide
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .apply(options)
                .into(ivProfileImage);

        ivProfileImage.setVisibility(View.VISIBLE);
        tvUsername.setVisibility(View.VISIBLE);
        tvHandle.setVisibility(View.VISIBLE);
/*
        ivRetweet = findViewById(R.id.ivRetweet);
        ivLike = findViewById(R.id.ivLike);
        ivReply = findViewById(R.id.ivReply);

        ivRetweet.setOnClickListener(this);
        ivLike.setOnClickListener(this);
        ivReply.setOnClickListener(this);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.ivLike: {
                likeTweet();
                break;
            }
        }
    }

    void likeTweet() {
        TwitterClient client = TwitterApp.getRestClient(this);
        client.likeTweet(tweet.liked, tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (tweet.liked) {
                    ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_heart));
                }
                else {
                    ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_heart_stroke));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e ("Twitter Client", errorResponse.toString());
            }
        });
    }

    // methods for formatting likes/retweet numbers
    private static String insertCommas(BigDecimal number) {
        // for your case use this pattern -> #,##0.00
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(number);
    }

    private static String insertCommas(String number) {
        return insertCommas(new BigDecimal(number));
    }
}
