package com.example.mango.theguardian;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mango.theguardian.adapters.SimilarNewsAdapter;
import com.example.mango.theguardian.utils.News;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class NewsDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private  News mNews = null;
    private List<News> mAllNews;
    private RecyclerView releventNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        // Initial title is blank.
        setTitle("");

        // Setting ActionBar for Activity.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Enableing back button to go back.
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Creating object for CollapsingToolBarLayout.
        // We want to hide the title when toolbar is expanded.
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getIntent().getStringExtra("TITLE"));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });

        // Creating object of RecyclerView to implement SimilarNews.
        releventNews = (RecyclerView) findViewById(R.id.releventNews);
        releventNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        releventNews.setItemAnimator(new DefaultItemAnimator());

        // Handeling onClickEvent for profile click.
        (findViewById(R.id.profileSection)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mNews.getConributorWebTitle() != null){
                    Intent i = new Intent(NewsDetailActivity.this, ProfileActivity.class);
                    i.putExtra("PROFILEAPI", mNews.getContributorApiUrl());
                    startActivity(i);
                }
            }
        });

        // Bundle to send API for the individual news.
        Bundle bundle = new Bundle();
        bundle.putString("URL", getIntent().getStringExtra("APIURL") + "?api-key="+getString(R.string.API_KEY)+"&show-fields=thumbnail,body,trailText&show-tags=contributor");

        // Initiating Loader with ID 1.
        getSupportLoaderManager().initLoader(1, bundle, this);

    }

    // Inflating View to add Share and bookmark labels into menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_news_detail, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.action_bookmark:
                Toast.makeText(this, "BookMark", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_share:
                // onShare clicked create new intent to share news link.
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String body = mNews.getStandfirst() + " " + getString(R.string.share_body) + " " + mNews.getWebUrl();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, body);
                // Starting New Activity.
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_intent)));
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return  true;
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new JSONLoader(this, args.getString("URL"));
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            // Creating JSONObject to parse news information from json.
            JSONObject json = new JSONObject(data);
            if(json.getJSONObject("response").has("content")) {
                mNews = new News(json.getJSONObject("response").getJSONObject("content"));
                updateUI();
            }
            if(json.getJSONObject("response").has("results")) {
                mAllNews = new ArrayList<>();
                JSONArray resultArray = json.getJSONObject("response").getJSONArray("results");
                for(int i = 0; i < resultArray.length(); i++){
                    News tempNews = new News(resultArray.getJSONObject(i));
                    if(tempNews.getWebTitle().compareTo(mNews.getWebTitle()) != 0)
                        mAllNews.add(tempNews);
                }
                SimilarNewsAdapter similarNewsAdapter = new SimilarNewsAdapter(this, mAllNews);
                releventNews.setAdapter(similarNewsAdapter);
                similarNewsAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
    // Updating UI.
    private void updateUI(){
        Picasso.get().load(mNews.getThumbnail()).into((ImageView) findViewById(R.id.newsImage));
        if(mNews.getContributorImage() != null)
            Picasso.get().load(mNews.getContributorImage()).into(((ImageView) findViewById(R.id.contributor_image)));

        if(mNews.getConributorWebTitle() != null)
            ((TextView) findViewById(R.id.contributorName)).setText(mNews.getConributorWebTitle());
        if(mNews.getContributorTwitter() != null)
            ((TextView) findViewById(R.id.contributorTwitter)).setText( "@" + mNews.getContributorTwitter());

        ((TextView) findViewById(R.id.newsWebTitle)).setText(mNews.getWebTitle());
        ((TextView) findViewById(R.id.newsDate)).setText(mNews.getDate());

        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.N) {
            ((TextView) findViewById(R.id.newsHeadline)).setText(Html.fromHtml(mNews.getStandfirst(), Html.FROM_HTML_MODE_COMPACT));
            ((TextView) findViewById(R.id.newsContent)).setText(Html.fromHtml(mNews.getBody(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            ((TextView) findViewById(R.id.newsHeadline)).setText(Html.fromHtml(mNews.getStandfirst()));
            ((TextView) findViewById(R.id.newsContent)).setText(Html.fromHtml(mNews.getBody()));
        }
        ((TextView) findViewById(R.id.newsContent)).setMovementMethod(LinkMovementMethod.getInstance());
        Bundle bundle = new Bundle();
        bundle.putString("URL", "https://content.guardianapis.com/" + mNews.getSectionId() + "?api-key="+getString(R.string.API_KEY)+"&show-fields=thumbnail,body,trailText&show-tags=contributor&page-size=5");
        getSupportLoaderManager().initLoader(2, bundle, this);
    }
}
