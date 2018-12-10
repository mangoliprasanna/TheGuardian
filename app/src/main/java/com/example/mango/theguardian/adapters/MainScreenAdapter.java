package com.example.mango.theguardian.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mango.theguardian.NewsDetailActivity;
import com.example.mango.theguardian.R;
import com.example.mango.theguardian.OnLoadMoreListener;
import com.example.mango.theguardian.utils.News;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainScreenAdapter extends RecyclerView.Adapter {

    private List<News> mAllNews;
    private Context mContext;
    private int visibleThreshold = 2;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;

    public MainScreenAdapter(Context context, List<News> allNews, RecyclerView recyclerView){
        this.mContext = context;
        this.mAllNews = allNews;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            // Handeling onScroll for RecyclerView.
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    // Counting number of elements in RecyclerView.
                    int totalItemCount = linearLayoutManager.getItemCount();
                    // Looking for the lastVisible item.
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    // visibileThreashold is useful so that we can start loading new news when third last element is visible.
                    // This will avoid waiting time for user.
                    // until user reaches last item the more newses can be pushed on screen.
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
                        if (onLoadMoreListener != null) {
                            // Performing onLoadMoew method.
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolderer = null;
        // Checking what type of viewType is requested by holder.
        switch (viewType){
            // returning the holder with R.layout.layout_progress_bar.
            case 0:
                viewHolderer = new ProgressHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_progress_bar, viewGroup, false));
                break;
            case 1:
                // returning the holder with R.layout.layout_single_news.
                viewHolderer = new SingleNewsHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_single_news, viewGroup, false));
                break;
            case 2:
                // returning the holder wih R.id.layout_list_news
                viewHolderer = new SingleNewsHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_list_news, viewGroup, false));
                break;
        }
        return viewHolderer;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder newsHolder, int i) {

        // onBindViewHolder is function common for holder.
        // Views in both of layout files are the same so no need of separate holder for views.
        // also the id for the views in both of layout files is same.

        // Getting single News object to fill current layout information.
        final News currentNews = mAllNews.get(i);

        // Casting RecyclerView.ViewHolder to SingelNewsHolder to access views.

        if(newsHolder instanceof  SingleNewsHolder) {
            newsHolder = (SingleNewsHolder) newsHolder;
            ((SingleNewsHolder) newsHolder).newsWebTitle.setText(currentNews.getWebTitle());
            ((SingleNewsHolder) newsHolder).newsDate.setText(currentNews.getPublicationDate());
            if (currentNews.getConributorWebTitle() != null)
                ((SingleNewsHolder) newsHolder).newsContributor.setText("By " + currentNews.getConributorWebTitle());
            else
                ((SingleNewsHolder) newsHolder).newsContributor.setText("");

            // onClickListener fot the containers in both layouts.
            ((SingleNewsHolder) newsHolder).container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, NewsDetailActivity.class);
                    i.putExtra("APIURL", currentNews.getApiUrl());
                    i.putExtra("TITLE", currentNews.getSection());
                    mContext.startActivity(i);
                }
            });

            // Using third party lib for loading images over internet.
            Picasso.get().load(currentNews.getThumbnail()).into(((SingleNewsHolder) newsHolder).newsImage);
        }
    }

    // Function for the interface.
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position){
        // Return ProgressBar when end of list.
        if(mAllNews.get(position) == null)
            return 0;
        // Every 5th element in list should be card view and other should be list view.
        if(position % 5 == 0)
            return 1;
        else
            return 2;

    }

    // Function to set loading state to false whem loading is completed.
    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return mAllNews.size();
    }

    // ProgressBar to infalte ProgressBar View.
    public class ProgressHolder extends  RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public ProgressHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public static class SingleNewsHolder extends RecyclerView.ViewHolder {
        // Basic SingleNewsHolder which is common layout for all news.
        TextView newsWebTitle;
        TextView newsContributor;
        TextView newsDate;
        ImageView newsImage;
        View container;
        public SingleNewsHolder(View itemView) {
            super(itemView);
            newsWebTitle = itemView.findViewById(R.id.newsWebTitle);
            newsContributor = itemView.findViewById(R.id.newsContributor);
            newsDate = itemView.findViewById(R.id.newsDate);
            newsImage = itemView.findViewById(R.id.newsImage);
            container = itemView.findViewById(R.id.container);
        }
    }
}
