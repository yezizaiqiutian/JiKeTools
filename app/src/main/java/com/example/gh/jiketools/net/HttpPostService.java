package com.example.gh.jiketools.net;

import com.gh.retrofittools.api.BaseResultEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author: gh
 * @description:
 * @date: 2018/5/14.
 * @from:
 */
public interface HttpPostService {

    @FormUrlEncoded
    @POST("AppFiftyToneGraph/videoLink")
    Observable<BaseResultEntity<List<SubjectResulte>>> getAllVedioBys(@Field("once") boolean once_no);

}