package com.uni.julio.superplus.utils;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import com.uni.julio.superplus.listeners.DownloaderListener;

public class DownloaderBroadcastReceiver extends BroadcastReceiver {
    private long downloadReference;
    private DownloaderListener listener;
    private DownloadManager manager;

    public DownloaderBroadcastReceiver(DownloadManager manager2, long reference, DownloaderListener listener2) {
        this.downloadReference = reference;
        this.listener = listener2;
        this.manager = manager2;
    }

    public void onReceive(Context context, Intent intent) {
        long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (reference == this.downloadReference) {
            Query query = new Query();
            query.setFilterById(new long[]{reference});
            Cursor cursor = this.manager.query(query);
            cursor.moveToFirst();
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String temp=cursor.getString(cursor.getColumnIndex("local_uri"));
            String temp1 = cursor.getString(cursor.getColumnIndex("local_uri"));
            String savedFilePath = temp1 == null ? "" : temp1.replace("file://", "");
            if(temp.equals("")) return;
            switch (status) {
                case 1:
                case 4:
                case 16:
                    this.listener.onDownloadError(-1);
                    return;
                case 8:
                    this.listener.onDownloadComplete(savedFilePath);
                    return;
                default:
            }
        }
    }
}
