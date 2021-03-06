package com.cos.Agora.study.studycreate;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.cos.Agora.CMRespDto;
import com.cos.Agora.R;
import com.cos.Agora.global.User;
import com.cos.Agora.study.StudyListActivity;
import com.cos.Agora.study.model.StudyCreateReqDto;
import com.cos.Agora.study.model.StudyCreateRespDto;
import com.cos.Agora.study.service.StudyService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceSetActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "PlaceSetActivity";
    private GoogleMap googleMap;
    private Button CompleteButton;

    private String mstudyName;
    private String mstudyInterest;
    private int mstudyFrequency;
    private int mstudyMemNum;
    private double mstudyLongitude;
    private double mstudyLatitude;
    private String mstudydescription;
    
    private com.cos.Agora.retrofitURL retrofitURL;
    private StudyService studyCreateService = retrofitURL.retrofit.create(StudyService .class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeset);

        init();
        
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        CompleteButton = findViewById(R.id.btn_create_complete);

        CompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                completeCreate();//????????? ???????????? method
            }
        });
    }

    private void init() {
        Intent intent = getIntent();
        mstudyName = intent.getStringExtra("studyName");
        mstudyInterest = intent.getStringExtra("studyInterest");
        mstudyFrequency = intent.getIntExtra("studyFrequency",0);
        mstudyMemNum =  intent.getIntExtra("studyMemNum",0);
        mstudydescription = intent.getStringExtra("studyDescription");
    }

    private void completeCreate() {
        //Dto ?????? ???????????? startStudyCreate ??????
        startStudyCreate(new StudyCreateReqDto(mstudyName,mstudyInterest,mstudyFrequency,mstudyMemNum,mstudyLongitude,mstudyLatitude,mstudydescription));// Req?????? ??????

        Intent intent = new Intent(PlaceSetActivity.this, StudyListActivity.class);
        startActivity(intent);
        PlaceSetActivity.this.finish();
    }

    private void startStudyCreate(StudyCreateReqDto studyCreateReqDto) {
        Call<CMRespDto<StudyCreateRespDto>> call = studyCreateService.createStudy(studyCreateReqDto);
        call.enqueue(new Callback<CMRespDto<StudyCreateRespDto>>() {
            @Override
            public void onResponse(Call<CMRespDto<StudyCreateRespDto>> call, Response<CMRespDto<StudyCreateRespDto>> response) {
                CMRespDto<StudyCreateRespDto> cmRespDto = response.body();
                StudyCreateRespDto studyCreateRespDto = cmRespDto.getData();
                Log.d(TAG, "onResponse: ????????? ?????? ??????!!!");

                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("userId", studyCreateRespDto.getId());
                editor.putString("studyName",studyCreateRespDto.getTitle());
                editor.putString("studyInterest",studyCreateRespDto.getInterest());
                editor.putInt("studyFrequency",studyCreateRespDto.getCount());
                editor.putInt("studyMemNum",studyCreateRespDto.getLimit());
                editor.putString("studyDescription",studyCreateRespDto.getDescription());
                editor.putFloat("studyLongitude",studyCreateRespDto.getLongitude().floatValue());//float??? ??????. (editor?????? double??????)
                editor.putFloat("studyLatitude",studyCreateRespDto.getLatitude().floatValue());
                editor.commit();
            }
            @Override
            public void onFailure(Call<CMRespDto<StudyCreateRespDto>> call, Throwable t) {
//                Toast.makeText(StudyCreateActivity.this, "????????? ?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "????????? ?????? ?????? ??????");
//                Log.e("????????? ?????? ?????? ??????", t.getMessage());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // map ????????? ????????? ?????? ?????? ???, ?????? ?????? ????????????
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions mOptions = new MarkerOptions();
                // ?????? ?????????
                mOptions.title("????????? ??????");
                Double latitude = point.latitude; // ??????
                Double longitude = point.longitude; // ??????
                // ????????? ?????????(????????? ?????????) ??????
                mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                // LatLng: ?????? ?????? ?????? ?????????
                mOptions.position(new LatLng(latitude, longitude));
                // ?????? ??????
                googleMap.addMarker(mOptions);
                
                //????????? ?????? ?????? ??????
                mstudyLongitude = longitude;
                mstudyLatitude = latitude;
            }
        });

        // ????????? ????????? ????????? ?????? ?????????
        LatLng latlng = new LatLng(((User)getApplication()).getLatitude(), ((User)getApplication()).getLongitude());
        //LatLng latlng = new LatLng(37.557667, 126.926546);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        MarkerOptions markerOptions = new MarkerOptions().position(latlng).title("?????? ??? ??????");
        googleMap.addMarker(markerOptions);

        //GPS ???????????? ?????????, ??????????????? ?????? ????????? ??? ?????????!
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        else {
            checkLocationPermissionWithRationale();
        }
    }

    //?????? ?????? ?????? ????????? ??? ??????
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermissionWithRationale(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("????????????")
                        .setMessage("??? ?????? ???????????? ???????????? ??????????????? ????????? ???????????????. ???????????? ????????? ???????????? ?????????.")
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(PlaceSetActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}