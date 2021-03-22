package com.uni.julio.superplus.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.uni.julio.superplus.R;
import com.uni.julio.superplus.binding.BindingAdapters;
import com.uni.julio.superplus.helper.TVRecyclerView;
import com.uni.julio.superplus.helper.TVRecyclerViewAdapter;
import com.uni.julio.superplus.helper.VideoStreamManager;
import com.uni.julio.superplus.listeners.LoadMoviesForCategoryResponseListener;
import com.uni.julio.superplus.listeners.MovieSelectedListener;
import com.uni.julio.superplus.model.MainCategory;
import com.uni.julio.superplus.model.Movie;
import com.uni.julio.superplus.model.MovieCategory;
import com.uni.julio.superplus.model.VideoStream;
import com.uni.julio.superplus.utils.Device;
import com.uni.julio.superplus.utils.networing.NetManager;

import java.util.List;

public class GridViewAdapter extends TVRecyclerViewAdapter<GridViewAdapter.MyViewHolder> implements LoadMoviesForCategoryResponseListener {
    private List<VideoStream> mMovies;
    private Context mContext;
    private MovieSelectedListener movieSelectedListener;
    private int mainCategoryId;
    private int movieCategoryId = 0;

    public GridViewAdapter(Context context, TVRecyclerView recyclerView, List<VideoStream> videoDataList, int mainCategoryId, int movieCategoryId, MovieSelectedListener movieSelectedListener) {
        this.mMovies = videoDataList;
        this.mContext = context;
        this.movieSelectedListener = movieSelectedListener;
        this.mainCategoryId = mainCategoryId;
        this.movieCategoryId = movieCategoryId;
        if (!Device.canTreatAsBox()) recyclerView.setIsAutoProcessFocus(false);
        else
            recyclerView.setOnItemStateListener(new TVRecyclerView.OnItemStateListener() {
                @Override
                public void onItemViewClick(View view, int position) {
                    GridViewAdapter.this.movieSelectedListener.onMovieSelected(mMovies.get((int) view.getTag()));
                }

                @Override
                public void onItemViewFocusChanged(boolean gainFocus, View view, int position) {
                }
            });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.gridview_row, viewGroup, false);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) (mContext.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int px = (int) mContext.getResources().getDisplayMetrics().density * (int) mContext.getResources().getDimension(R.dimen.dip_4);
        int width = (screenWidth - 16 * Integer.parseInt(mContext.getString(R.string.more_video)) - px) / Integer.parseInt(mContext.getString(R.string.more_video));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, (int) (1.5 * width));
        itemView.setLayoutParams(params);
        return new MyViewHolder(mContext, itemView);
    }

    @Override
    protected void focusOut(View v, int position) {

    }

    @Override
    protected void focusIn(View v, int position) {

    }

    @Override
    protected void onDataBinding(MyViewHolder holder, int position) {
        Movie movie = (Movie) mMovies.get(position);
        holder.getViewDataBinding().setVariable(com.uni.julio.superplus.BR.moviesMenuItem, movie);
        holder.getViewDataBinding().getRoot().setTag(position);
        holder.getViewDataBinding().setVariable(com.uni.julio.superplus.BR.moviesAdapter, this);
        BindingAdapters.loadImage((ImageView) holder.getViewDataBinding().getRoot().findViewById(R.id.fl_main_layout).findViewById(R.id.img), movie.getHDPosterUrl());
        holder.getViewDataBinding().executePendingBindings();

        int size = mMovies.size();
        int offset = ((int) (size / 50));
        if (movieCategoryId == -1) return;
        MainCategory mainCategory = VideoStreamManager.getInstance().getMainCategory(mainCategoryId);
        MovieCategory movieCategory = mainCategory.getMovieCategory(movieCategoryId);
        if (size < 1500 && size % 50 == 0 && (size - 20) == position && !movieCategory.isLoading()) {
            NetManager.getInstance().retrieveMoviesForSubCategory(mainCategory, movieCategory, offset, this, 30);
        }

    }

    public void updateMovies(List<VideoStream> objects) {
        mMovies = objects;
        postAndNotifyAdapter();
    }

    public void onClickItem(View view) {
        movieSelectedListener.onMovieSelected(mMovies.get((int) view.getTag()));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    @Override
    public void onMoviesForCategoryCompleted(MovieCategory movieCategory, List<VideoStream> movies, int offset) {
        movieCategory.setLoaded(true);
        if (!movieCategory.hasErrorLoading()) {
            movieCategory.setLoading(false);
            movieCategory.setErrorLoading(false);

        }
        this.postAndNotifyAdapter();
    }

    @Override
    public void onMoviesForCategoryCompletedError(MovieCategory movieCategory) {
        movieCategory.setLoaded(false);
        movieCategory.setLoading(false);
        movieCategory.setErrorLoading(true);
    }

    @Override
    public void onError() {

    }

    class MyViewHolder extends TVRecyclerViewAdapter.ViewHolder {
        private ViewDataBinding viewDataBinding;

        MyViewHolder(Context context, View itemView) {
            super(context, itemView);
            viewDataBinding = DataBindingUtil.bind(itemView);
        }

        ViewDataBinding getViewDataBinding() {
            return viewDataBinding;
        }
    }

    private void postAndNotifyAdapter() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

}
