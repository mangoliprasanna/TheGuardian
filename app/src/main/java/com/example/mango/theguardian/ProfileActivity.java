package com.example.mango.theguardian;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mango.theguardian.adapters.SimilarNewsAdapter;
import com.example.mango.theguardian.utils.News;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<String> {

    private RecyclerView profileArticlesRecyclerView;
    private List<News> mAllNews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Using RecyclerView to manipulate the News on screen.
        // These news are only belonging to hte current Contributor.
        profileArticlesRecyclerView = findViewById(R.id.profileArticlesRecyclerView);
        profileArticlesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        profileArticlesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Creating bundle passing API of Contributor to load the more information about Contributor from server.
        Bundle bundle = new Bundle();
        bundle.putString("URL", getIntent().getStringExtra("PROFILEAPI") + "?api-key="+getString(R.string.API_KEY)+ "&show-fields=thumbnail&show-tags=contributor");

        // Initiating Loader with ID 1.
        getSupportLoaderManager().initLoader(1, bundle, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id,Bundle args) {
        // Initiating JSONLoade to featch information from server.
        return new JSONLoader(this, args.getString("URL"));
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        // Empty list of news belonging to the Contributor.
        mAllNews = new ArrayList<>();
        try {
            // Creating JSONObject to parse news information from json.
            JSONObject json = new JSONObject(data);

            // Array of news from json data.
            JSONArray jsonArray = json.getJSONObject("response").getJSONArray("results");

            // Counting number of articles written by contributor and formating it in ##,### numerical formate.
            String articles = new DecimalFormat("##,###").format(json.getJSONObject("response").getInt("total")).toString() + " Articles";

            // Display the number of articles on Screen.
            ((TextView) findViewById(R.id.profileArticles)).setText(articles);

            // Iterating through each item in array to extract individual news.
            for (int i = 0; i < jsonArray.length(); i++)
                mAllNews.add(new News(jsonArray.getJSONObject(i)));
            // Updating UI.
            updateUT();

            // Creating Adapter to display news in recyclerView.
            SimilarNewsAdapter similarNewsAdapter = new SimilarNewsAdapter(this, mAllNews);
            profileArticlesRecyclerView.setAdapter(similarNewsAdapter);

            // Notifying dataset changes to reflect on screen.
            similarNewsAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return  true;
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private void updateUT() {

        // Title or Name of contributor.
        ((TextView) findViewById(R.id.progileTitle)).setText(mAllNews.get(0).getConributorWebTitle());

        // Extracting only first name to generate string Articles by <Contributor_fname>
        String name = " " + mAllNews.get(0).getContributorFname();
        ((TextView) findViewById(R.id.articleBy)).setText( getString(R.string.profile_article) + Character.toUpperCase(name.charAt(0)) + name.substring(1));

        // Checking Build version of device to parse HTML to TextView.
        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.N)
            ((TextView) findViewById(R.id.profileBio)).setText(Html.fromHtml(mAllNews.get(0).getContributorBio(), Html.FROM_HTML_MODE_COMPACT));
        else
            ((TextView) findViewById(R.id.profileBio)).setText(Html.fromHtml(mAllNews.get(0).getContributorBio()));

        // Updating Bio information to TextView.
        ((TextView) findViewById(R.id.profileBio)).setMovementMethod(LinkMovementMethod.getInstance());

        // If Contributor has twitter handel displaying otherwise leaveaint the blank space.
        if(mAllNews.get(0).getContributorTwitter() != null)
            ((TextView) findViewById(R.id.profileTwitter)).setText("@" + mAllNews.get(0).getContributorTwitter());
        if(mAllNews.get(0).getContributorImage() != null)
            Picasso.get().load(mAllNews.get(0).getContributorImage()).into((ImageView) findViewById(R.id.profileImage));
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}