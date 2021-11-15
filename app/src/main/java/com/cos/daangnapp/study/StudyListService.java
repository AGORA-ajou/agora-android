package com.cos.daangnapp.study;

import com.cos.daangnapp.CMRespDto;
import com.cos.daangnapp.study.model.StudyRespDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StudyListService {
//사용자 분별위해 phoneNumber도 같이 보내주기!
    @GET("study/filter/{phoneNumber}")
    Call<CMRespDto<List<StudyRespDto>>> getstudies(@Path("phoneNumber") String phoneNumber, @Query("interest") String interest, @Query("lineup") String lineup);

}