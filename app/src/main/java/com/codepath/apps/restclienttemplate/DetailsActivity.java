package com.codepath.apps.restclienttemplate;

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

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailsActivity extends AppCompatActivity {

    MenuItem miActionProgressItem;
    TextView tvBody;
    TextView tvUsername;
    TextView tvHandle;
    TextView tvTimestamp;
    TextView tvRetweetCount;
    TextView tvLikesCount;
    ImageView ivProfileImage;

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
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
        setTweetDetails();
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
        // tvLikesCount.setText(tweet.likesCount);
        // tvRetweetCount.setText(tweet.retweetCount);

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
    }
}
