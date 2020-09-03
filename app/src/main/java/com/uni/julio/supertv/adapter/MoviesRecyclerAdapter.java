package com.uni.julio.supertv.adapter;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.uni.julio.supertv.R;
import com.uni.julio.supertv.helper.TVRecyclerView;
import com.uni.julio.supertv.helper.TVRecyclerViewAdapter;
import com.uni.julio.supertv.helper.VideoStreamManager;
import com.uni.julio.supertv.listeners.LoadMoviesForCategoryResponseListener;
import com.uni.julio.supertv.listeners.MovieSelectedListener;
import com.uni.julio.supertv.model.ImageResponse;
import com.uni.julio.supertv.model.MainCategory;
import com.uni.julio.supertv.model.Movie;
import com.uni.julio.supertv.model.MovieCategory;
import com.uni.julio.supertv.model.VideoStream;
import com.uni.julio.supertv.utils.Device;
import com.uni.julio.supertv.utils.networing.NetManager;

import java.util.List;

public class MoviesRecyclerAdapter extends TVRecyclerViewAdapter<MoviesRecyclerAdapter.MyViewHolder> implements LoadMoviesForCategoryResponseListener {
    private List<? extends VideoStream> mMovies;
    private Context mContext;
    private int movieCategoryId;
    private int mainCategoryId;
    private MovieSelectedListener movieSelectedListener;
    private TVRecyclerView recyclerView;

    MoviesRecyclerAdapter(Context context, TVRecyclerView recyclerView, List<VideoStream> videoDataList, int mainCategoryId, int movieCategoryId, MovieSelectedListener movieSelectedListener) {
        this.mMovies = videoDataList;
        this.mContext = context;
        this.mainCategoryId = mainCategoryId;
        this.movieCategoryId = movieCategoryId;
        this.movieSelectedListener = movieSelectedListener;
        this.recyclerView = recyclerView;
        if (!Device.canTreatAsBox()) recyclerView.setIsAutoProcessFocus(false);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.video_list_row, viewGroup, false);
        return new MyViewHolder(mContext, itemView);
    }

    @Override
    protected void focusOut(View v, int position) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.1f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY);

        set.start();
    }

    @Override
    protected void focusIn(View v, final int position) {
        recyclerView.scrollToPosition(position);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 1.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 1.1f);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY);
        set.start();


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDataBinding(MyViewHolder holder, int position) {
        Movie movie = (Movie) mMovies.get(position);
        holder.getViewDataBinding().setVariable(com.uni.julio.supertv.BR.moviesMenuItem, movie);
        holder.getViewDataBinding().getRoot().setTag(position);
        holder.getViewDataBinding().setVariable(com.uni.julio.supertv.BR.moviesAdapter, this);
        int size = mMovies.size();
        int offset = ((int) (size / 50.00));
        if (movieCategoryId == -1) return;
        MainCategory mainCategory = VideoStreamManager.getInstance().getMainCategory(mainCategoryId);
        MovieCategory movieCategory = mainCategory.getMovieCategory(movieCategoryId);
        if (size < 1500 && size % 50 == 0 && (size - 20) == position && !movieCategory.isLoading()) {
            NetManager.getInstance().retrieveMoviesForSubCategory(mainCategory, movieCategory, offset, this, 30);
        }
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
            itemView.setBackground(mContext.getResources().getDrawable(R.drawable.movies_bg));

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
