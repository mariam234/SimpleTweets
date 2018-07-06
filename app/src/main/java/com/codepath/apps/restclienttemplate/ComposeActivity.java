package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ComposeActivity extends AppCompatActivity implements View.OnClickListener {

    private TwitterClient client;
    EditText etNewTweet;
    Button btSubmitTweet;
    TextView tvCounter;
    TextView tvUsernameCompose;
    TextView tvHandleCompose;
    ImageView ivProfileImageCompose;
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // get max tweet length
        int max_tweet_length = getResources().getInteger(R.integer.max_tweet_length);

        // bind variables to views and set initial values and listeners
        tvUsernameCompose = findViewById(R.id.tvUsernameCompose);
        tvHandleCompose = findViewById(R.id.tvHandleCompose);
        ivProfileImageCompose = findViewById(R.id.ivProfileImageCompose);
        btSubmitTweet = findViewById(R.id.btSubmitTweet);
        etNewTweet = findViewById(R.id.etNewTweet);
        tvCounter = findViewById(R.id.tvCounter);

        tvCounter.setText(String.valueOf(max_tweet_length));
        btSubmitTweet.setOnClickListener(this);
        etNewTweet.addTextChangedListener(mTextEditorWatcher);

        // set user's info
        setUserInfo();
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int max_tweet_length = getResources().getInteger(R.integer.max_tweet_length);
            int counterValue = max_tweet_length - s.length();

            // once max is reached, set counter to be red
            if (counterValue < 0) {
                tvCounter.setTextColor(getResources().getColor(R.color.medium_red));
            }
            else {
                tvCounter.setTextColor(getResources().getColor(R.color.medium_gray));
            }

            // set counter to count down from max tweet length
            tvCounter.setText(String.valueOf(counterValue));

        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
/*        // Extract the action-view from the menu item
        ProgressBar progressBar = (ProgressBar) miActionProgressItem.getActionView();*/
        // show progress bar
        showProgressBar();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // send tweet if submit button is pressed
            case  R.id.btSubmitTweet: {
                sendTweet();
                break;
            }
        }
    }

    // method for configuring user's profile picture, username, and handle
    public void setUserInfo() {
        // set up client and send network request to get user's info tweet
        client = TwitterApp.getRestClient(this);
        client.getUserInfo(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // get user from response and set name and handle
                    User user = User.fromJSON(response);
                    tvUsernameCompose.setText(user.name);
                    tvHandleCompose.setText(user.screenName);
                    tvUsernameCompose.setVisibility(View.VISIBLE);
                    tvHandleCompose.setVisibility(View.VISIBLE);

                    // rounded corners transformation
                    RoundedCornersTransformation transformation = new RoundedCornersTransformation(
                            20,
                            20
                    );

                    // put glide image options in variable
                    RequestOptions options = RequestOptions.bitmapTransform(transformation);

                    // load image using glide
                    Glide.with(getBaseContext())
                            .load(user.profileImageUrl)
                            .apply(options)
                            .into(ivProfileImageCompose);
                    ivProfileImageCompose.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }

        });
    }

    // method for sending tweet and going back to timeline activity
    public void sendTweet() {
        // show progress bar
        showProgressBar();

        // get message that user wrote
        etNewTweet = findViewById(R.id.etNewTweet);
        String message = etNewTweet.getText().toString();

        // set up client and send network request to post new tweet
        client = TwitterApp.getRestClient(this);
        client.sendTweet(message, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // turn response into tweet object
                    Tweet newTweet = Tweet.fromJSON(response);
                    // Prepare data intent
                    Intent data = new Intent();
                    // Pass relevant data back as a result
                    data.putExtra("newTweet", Parcels.wrap(newTweet));
                    data.putExtra("code", 20); // REQUEST_CODE from timeline activity
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    hideProgressBar();
                    finish(); // closes the activity, pass data to parent
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideProgressBar();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // tell user error message and log it
                try {
                    String errorMessage = errorResponse.getJSONArray("errors")
                            .getJSONObject(0)
                            .getString("message");
                    Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideProgressBar();
                }
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }

        });
    }

    public void onSubmit(View v) {
        this.finish();
    }

}
