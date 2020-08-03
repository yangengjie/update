package com.example.abu.update_library;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ygj on 2020/7/30.
 */

public class UpdateDownloader extends AsyncTask<Void, Integer, Long> {
    private Context mContext;
    private IDownloadAgent downloadAgent;
    private String downloadUrl;
    private File mTemp;
    private HttpURLConnection mConnection;
    private long mBytesLoaded = 0;
    private long mBytesTotal = 0;
    private long mBytesTemp = 0;
    private final static int EVENT_START = 1;
    private final static int EVENT_PROGRESS = 2;
    private long mTimeLast;

    public UpdateDownloader(Context mContext, IDownloadAgent downloadAgent, String downloadUrl, File mTemp) {
        this.mContext = mContext;
        this.downloadAgent = downloadAgent;
        this.downloadUrl = downloadUrl;
        this.mTemp = mTemp;
        if (mTemp.exists())
            mBytesTemp = mTemp.length();
    }

    @Override
    protected Long doInBackground(Void... voids) {
        try {
            long result = download();
            if (isCancelled()) {
                downloadAgent.onError(new UpdateError(UpdateError.DOWNLOAD_CANCELLED));
            } else if (result == -1) {
                downloadAgent.onError(new UpdateError(UpdateError.DOWNLOAD_UNKNOWN));
            }
        } catch (FileNotFoundException e) {
            downloadAgent.onError(new UpdateError(UpdateError.DOWNLOAD_DISK_IO));
        } catch (IOException e) {
            e.printStackTrace();
            downloadAgent.onError(new UpdateError(UpdateError.DOWNLOAD_NETWORK_IO));
        } catch (UpdateError updateError) {
            updateError.printStackTrace();
            downloadAgent.onError(updateError);
        } finally {
            if (mConnection != null)
                mConnection.disconnect();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        switch (values[0]) {
            case EVENT_START:
                downloadAgent.onStart();
                break;
            case EVENT_PROGRESS:
                long now = System.currentTimeMillis();
                if (now - mTimeLast < 900) {
                    break;
                }
                mTimeLast = now;
                downloadAgent.onProgress((int) ((mBytesTemp + mBytesLoaded) * 100 / mBytesTotal));
                break;
        }
    }

    @Override
    protected void onPostExecute(Long aVoid) {
        super.onPostExecute(aVoid);
        downloadAgent.onFinish();
    }

    private long download() throws IOException, UpdateError {
        checkNetWork();
        mConnection = create(new URL(downloadUrl));
        mConnection.connect();
        checkStatus();
        mBytesTotal = mConnection.getContentLength();
        checkSpace();

        if (mBytesTotal == mBytesTemp) {
            publishProgress(EVENT_START);
            return 0;
        }

        if (mBytesTemp > 0) {
            mConnection.disconnect();
            mConnection = create(mConnection.getURL());
            mConnection.addRequestProperty("Range", "bytes=" + mBytesTemp + "-");
            mConnection.connect();
            checkStatus();
        }
        publishProgress(EVENT_START);
        long bytesCopied = copy(mConnection.getInputStream(), new RandomAccessFile(mTemp, "rw"));

        if (!isCancelled() && (mBytesTemp + bytesCopied) != mBytesTotal && mBytesTotal != -1) {
            throw new UpdateError(UpdateError.DOWNLOAD_INCOMPLETE);
        }
        return bytesCopied;
    }

    private long copy(InputStream inputStream, RandomAccessFile rw) throws IOException, UpdateError {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] bytes = new byte[1024*8];
        try {
            rw.seek(rw.length());
            while (!isCancelled()) {
                int len = bufferedInputStream.read(bytes);
                if (len == -1)
                    break;
                rw.write(bytes, 0, len);
                mBytesLoaded += len;
                publishProgress(EVENT_PROGRESS);
                checkNetWork();
            }
            return mBytesLoaded;
        } finally {
            inputStream.close();
            rw.close();
            bufferedInputStream.close();
        }
    }

    private void checkSpace() throws UpdateError {
        long storage = getAvailableStorage();
        if (mBytesTotal - mBytesTemp > storage)
            throw new UpdateError(UpdateError.DOWNLOAD_DISK_NO_SPACE);
    }

    private long getAvailableStorage() {
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            } else {
                return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    private void checkStatus() throws IOException, UpdateError {
        if (mConnection.getResponseCode() != 200 && mConnection.getResponseCode() != 206)
            throw new UpdateError(UpdateError.DOWNLOAD_HTTP_STATUS);
    }

    private void checkNetWork() throws UpdateError {
        if (!UpdateUtil.hasNetWork(mContext))
            throw new UpdateError(UpdateError.DOWNLOAD_NETWORK_BLOCKED);
    }

    private HttpURLConnection create(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(15000);
        urlConnection.setReadTimeout(15000);
        return urlConnection;
    }
}
