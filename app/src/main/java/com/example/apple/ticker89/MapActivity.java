package com.example.apple.ticker89;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity{

    public LocationClient mLocationClient;
    private TextView positonText;
    private BaiduMap baiduMap;
    private boolean isFirstLocate=true;

    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_map);

        positonText=(TextView) findViewById(R.id.position_text_view);
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MapActivity.this,permissions,1);
        }else {
            requestLocation();
        }


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, MapBaiduActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(1000);
        //下面这行代码可以让手机只用GPS定位
          option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //下面这行代码可以明确指定在具体的位置
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mLocationClient.stop();
    }

    private void requestLocation(){
        //下面是结合上面调用控制实时移动的代码,但不知为何，添入这个反而会没用
        initLocation();
        mLocationClient.start();

    }




    //   @Override
    public void onRquestPermissionResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0){
                    for (int result:grantResults){
                        if (result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"拒绝权限者无程序可用",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }


    public class MyLocationListener implements BDLocationListener{


        @Override
        public void onReceiveLocation(final BDLocation location){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder currentPosition=new StringBuilder();
                    currentPosition.append("纬度: ").append(location.getLatitude()).append("\n");
                    currentPosition.append("经度: ").append(location.getLongitude()).append("\n");
                    currentPosition.append("国家: ").append(location.getCountry()).append("\n");
                    currentPosition.append("省: ").append(location.getProvince()).append("\n");
                    currentPosition.append("市: ").append(location.getCity()).append("\n");
                    currentPosition.append("区: ").append(location.getDistrict()).append("\n");
                    currentPosition.append("街道: ").append(location.getStreet()).append("\n");
                    currentPosition.append("定位方式：");
                    if (location.getLocType()==BDLocation.TypeGpsLocation){
                        currentPosition.append("GPS");
                    }else if (location.getLocType()==
                            BDLocation.TypeNetWorkLocation){
                        currentPosition.append("网络");
                    }
                    positonText.setText(currentPosition);
                }
            });
        }
        //       @Override
        public void onConnectHotSpotMessage(String s,int i){

        }
    }


}
