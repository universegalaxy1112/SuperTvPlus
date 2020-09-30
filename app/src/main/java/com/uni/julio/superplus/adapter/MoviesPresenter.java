package com.uni.julio.superplus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.leanback.widget.Presenter;

import com.uni.julio.superplus.R;
import com.uni.julio.superplus.helper.VideoStreamManager;
import com.uni.julio.superplus.listeners.LoadMoviesForCategoryResponseListener;
import com.uni.julio.superplus.model.Movie;
import com.uni.julio.superplus.model.MovieCategory;
import com.uni.julio.superplus.utils.networing.NetManager;

public class MoviesPresenter extends Presenter {
    private Context mContext;
    private int mainCategoryId;
    private MovieCategory movieCategory;
    private LoadMoviesForCategoryResponseListener loadMoviesForCategoryResponseListener;

    public MoviesPresenter(Context context) {
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new MoviesPresenterViewHolder(((LayoutInflater) this.mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.video_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Object item) {
        Movie movie = (Movie) item;
        ((MoviesPresenterViewHolder) holder).getViewBinding().setVariable(com.uni.julio.superplus.BR.moviesMenuItem, movie);
        ((MoviesPresenterViewHolder) holder).getViewBinding().executePendingBindings();
        if(this.movieCategory == null) return;
        int size = movieCategory.getMovieList().size();
        int offset = ((int) (size / 50));
        if (size < 1500 && size % 50 == 0 && (size - 20 == ((Movie) item).getPosition() && !movieCategory.isLoading())) {
            NetManager.getInstance().retrieveMoviesForSubCategory(VideoStreamManager.getInstance().getMainCategory(this.mainCategoryId), movieCategory, offset, this.loadMoviesForCategoryResponseListener, 30);
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }

    public void setParams(MovieCategory movieCategory, int mainCategoryId, LoadMoviesForCategoryResponseListener loadMoviesForCategoryResponseListener) {
        this.movieCategory = movieCategory;
        this.mainCategoryId = mainCategoryId;
        this.loadMoviesForCategoryResponseListener = loadMoviesForCategoryResponseListener;
    }


}
