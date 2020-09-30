package com.uni.julio.superplus.listeners;


import com.uni.julio.superplus.model.Season;

public interface LoadEpisodesForSerieResponseListener extends BaseResponseListener {
    void onEpisodesForSerieCompleted(Season season);
}
