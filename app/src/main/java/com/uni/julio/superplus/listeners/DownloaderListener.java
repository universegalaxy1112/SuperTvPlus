package com.uni.julio.superplus.listeners;

public interface DownloaderListener {
    void onDownloadComplete(String str);

    void onDownloadError(int i);
}
