package com.uni.julio.superplus.listeners;


public interface EpisodeLoadListener {
    void onLoaded();
    void onError();
    void showCustomProgress();
}