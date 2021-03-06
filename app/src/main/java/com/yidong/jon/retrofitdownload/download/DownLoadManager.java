package com.yidong.jon.retrofitdownload.download;

import com.yidong.jon.MyApplication;
import com.yidong.jon.retrofitdownload.download.db.DownLoadDatabase;
import com.yidong.jon.retrofitdownload.download.db.DownLoadEntity;
import com.yidong.jon.retrofitdownload.retrofit.NetWorkRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownLoadManager {

    private DownLoadDatabase mDownLoadDatabase;

    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

    //多线程下载文件最低大小10mb
    private final long MULTI_LINE = 10 * 1024 * 1024;

    //所有下载Task
    private Map<String, DownLoadRequest> mDownLoadRequestMap = new ConcurrentHashMap<>();

    private static volatile DownLoadManager instance;

    private DownLoadManager() {
        mDownLoadDatabase = DownLoadDatabase.getInstance(MyApplication.context);
    }

    public static final DownLoadManager getInstance() {
        if (instance == null) {
            synchronized (DownLoadManager.class) {
                if (instance == null) {
                    instance = new DownLoadManager();
                }
            }
        }
        return instance;
    }

    //默认支持多线程下载
    public void downLoad(final List<DownLoadEntity> list, final String tag, final DownLoadBackListener downLoadTaskListener) {
        downLoad(list, tag, downLoadTaskListener, MULTI_LINE);
    }


    public void downLoad(final List<DownLoadEntity> list, final String tag, final DownLoadBackListener downLoadTaskListener, final long multiLine) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                DownLoadRequest downLoadRequest = new DownLoadRequest(mDownLoadDatabase, downLoadTaskListener, list, multiLine);
                downLoadRequest.start();
                mDownLoadRequestMap.put(tag, downLoadRequest);
            }
        });
    }

    //默认支持多线程下载
    public void downLoad(final DownLoadEntity downLoadEntity, final String tag, final DownLoadBackListener downLoadTaskListener) {
        List<DownLoadEntity> list = new ArrayList<>();
        list.add(downLoadEntity);
        downLoad(list, tag, downLoadTaskListener, MULTI_LINE);
    }

    public void downLoad(final DownLoadEntity downLoadEntity, final String tag, final DownLoadBackListener downLoadTaskListener, final long multiLine) {
        List<DownLoadEntity> list = new ArrayList<>();
        list.add(downLoadEntity);
        downLoad(list, tag, downLoadTaskListener, multiLine);
    }


    //取消所有任务
    public void cancel() {
        if (!mDownLoadRequestMap.isEmpty()) {
            Iterator iterator = mDownLoadRequestMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                cancel(key);
            }
        }
    }

    //取消当前tag所有任务
    public void cancel(String tag) {
        if (!mDownLoadRequestMap.isEmpty()) {
            if (mDownLoadRequestMap.containsKey(tag)) {
                mDownLoadRequestMap.get(tag).stop();
                mDownLoadRequestMap.remove(tag);
            }
        }
    }
}
