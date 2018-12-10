package com.example.mango.theguardian.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.mango.theguardian.JSONLoader;
import com.example.mango.theguardian.OnLoadMoreListener;
import com.example.mango.theguardian.R;
import com.example.mango.theguardian.adapters.MainScreenAdapter;
import com.example.mango.theguardian.utils.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private RecyclerView mNewsRecyclerView;
    private MainScreenAdapter mainScreenAdapter;
    private List<News> allNews;
    private int PAGE_ID = 1;
    // MainScreen Fragment.
    public MainScreenFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflating layout for fragment.
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);

        // Creating recyclerView instance to access the recycler from view.
        mNewsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        mNewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        loadLoader(1);
        return view;
    }

    private void loadLoader(int id){
        // Bundle consists of key and value of url to featch the data through Loader.
        Bundle bundle = new Bundle();
        bundle.putString("URL", "https://content.guardianapis.com/search?api-key="+getString(R.string.API_KEY)+"&show-fields=thumbnail&show-tags=contributor&page-size=30&page=" + PAGE_ID);
        // Initialising Loader with ID i to load the URL data.
        getLoaderManager().initLoader(id, bundle, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        // New JSONLoader instance which return the string consists of JSON recompense from server.
        return new JSONLoader(getActivity(), args.getString("URL"));
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        // Empty News ArrayList.
        if(allNews == null)
            allNews = new ArrayList<>();
        else
            allNews.remove(allNews.size() - 1);
        try {
            // Creating JSONObject to parse news information from json.
            JSONObject json = new JSONObject(data);

            // Array of news from json data.
            JSONArray resultArray = json.getJSONObject("response").getJSONArray("results");

            // Iterating through each item in array to extract individual news.
            for(int i = 0; i < resultArray.length(); i++){
                // Adding new News element to array.
                allNews.add(new News(resultArray.getJSONObject(i)));
            }

            if(loader.getId() == 1) {
                // Creating Adapter for RecyclerView.
                mainScreenAdapter = new MainScreenAdapter(getContext(), allNews, mNewsRecyclerView);
                mNewsRecyclerView.setAdapter(mainScreenAdapter);
                mainScreenAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        allNews.add(null);
                        mainScreenAdapter.notifyItemInserted(allNews.size() - 1);
                        PAGE_ID++;
                        loadLoader(2);
                    }
                });
            } else {
                getLoaderManager().destroyLoader(2);
                mainScreenAdapter.notifyItemRemoved(allNews.size());
                mainScreenAdapter.notifyItemInserted(allNews.size());
                mainScreenAdapter.setLoaded();
            }
            // Notifying dataset changes to reflect on screen.
            mainScreenAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
