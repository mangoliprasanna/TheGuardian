package com.example.mango.theguardian.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mango.theguardian.NewsDetailActivity;
import com.example.mango.theguardian.R;
import com.example.mango.theguardian.utils.News;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SimilarNewsAdapter extends RecyclerView.Adapter<MainScreenAdapter.SingleNewsHolder> {

    private List<News> allNews;
    private Context mContext;

    public SimilarNewsAdapter(Context context, List<News> allNews){
        this.allNews = allNews;
        this.mContext = context;
    }

    @Override
    public MainScreenAdapter.SingleNewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_news, parent, false);
        return new MainScreenAdapter.SingleNewsHolder(view);
    }

    @Override
    public void onBindViewHolder(MainScreenAdapter.SingleNewsHolder newsHolder, int position) {

        // Getting single News object to fill current layout information.
        final News currentNews = allNews.get(position);
        newsHolder.newsWebTitle.setText(currentNews.getWebTitle());
        newsHolder.newsDate.setText(currentNews.getPublicationDate());
        if(currentNews.getConributorWebTitle() != null)
            newsHolder.newsContributor.setText( "By " + currentNews.getConributorWebTitle());
        else
            newsHolder.newsContributor.setText("");

        // onClickListner for container.
        newsHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, NewsDetailActivity.class);
                i.putExtra("APIURL", currentNews.getApiUrl());
                i.putExtra("TITLE", currentNews.getSection());
                mContext.startActivity(i);
            }
        });

        // Using third party lib for loading images over internet.
        Picasso.get().load(currentNews.getThumbnail()).into(newsHolder.newsImage);
    }

    @Override
    public int getItemCount() {
        return allNews.size();
    }
}
