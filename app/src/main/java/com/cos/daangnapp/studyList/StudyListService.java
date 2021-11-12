package com.cos.daangnapp.studyList;

import com.cos.daangnapp.CMRespDto;
import com.cos.daangnapp.studyList.model.StudyRespDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StudyListService {

    @GET("study/interest/lineup")
    Call<CMRespDto<List<StudyRespDto>>> getstudies(@Query("interest") String interest, String lineup);

//    @GET("post/gu/keyword")
//    Call<CMRespDto<List<StudyRespDto>>> searchposts(@Query("gu") String gu, @Query("keyword") String keyword);
}