package com.yidong.jon.retrofitdownload.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface DownLoadService {
    /**
     * Download file.
     *
     * @param fileUrl
     * @param range
     * @return
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl, @Header("Range") String range);

    @Streaming
    @GET
    Call<ResponseBody> getHttpHeader(@Url String fileUrl, @Header("Range") String range);

    @Streaming
    @GET
    Call<ResponseBody> getHttpHeaderWithIfRange(@Url String fileUrl, @Header("If-Range") String lastModify, @Header("Range") String range);
}
