package com.uni.julio.superplus.listeners;


import com.uni.julio.superplus.model.Serie;

public interface LoadSeasonsForSerieResponseListener extends BaseResponseListener {
    void onSeasonsLoaded(Serie serie, int seasons);
    void onSeasonsLoadedError();
}
