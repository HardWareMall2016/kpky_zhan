package com.zhan.kykp.network.bean;

import com.zhan.kykp.network.FileDownloadListener;

/**
 * Created by WuYue on 2015/10/22.
 */
public interface FileDownloadWithProgressListener extends FileDownloadListener{
    void onProgress(int bytesWritten, int totalSize);
}
