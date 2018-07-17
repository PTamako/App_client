package com.example.lenovo.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.model.LatLng;
import com.example.lenovo.test.model.BaseResponse;
import com.example.lenovo.test.model.LoginFormRequest;
import com.example.lenovo.test.model.SearchFormRequest;
import com.example.lenovo.test.model.Store;
import com.example.lenovo.test.model.StoreList;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends Activity {
    private Context context;
    private double mLatitude;
    private double mLongtitude;
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private LocationClient myClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private boolean isFirstLocate = true;
    private MyOrientationListener mOrientationListener;
    private BitmapDescriptor mIconLocation;
    private float mCurrentX;
    //覆盖物相关
    boolean isShow = false;
    private BitmapDescriptor mMarker;
    private RelativeLayout mMarkerly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除bar的title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initialView();
        initLocation();
        //覆盖物相关
        initMarkers();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle exaInfo = marker.getExtraInfo();
                final Store info = (Store) exaInfo.getSerializable("info");
                Button sEva = (Button) mMarkerly.findViewById(R.id.comment_submit);
                TextView sLoc = (TextView) mMarkerly.findViewById(R.id.store_loc);
                TextView sTel = (TextView) mMarkerly.findViewById(R.id.store_tel);
                TextView sName = (TextView) mMarkerly.findViewById(R.id.store_name);
                ImageView sImg = (ImageView) mMarkerly.findViewById(R.id.store_img);
                sLoc.setText(info.getLoc());
                sTel.setText(info.getTelephone() + "");
                sName.setText(info.getStoreName());
                if(null!=info.getImage()){
                    String imgUrl = Constants.BASE_URL+"/"+info.getImage();
                    //图片开源框架Picasso
                    Picasso.with(MainActivity.this).load(imgUrl).fit().into(sImg);
                }

                sEva.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = getIntent();
                        String user = intent1.getStringExtra("data");
                        String storeName = info.getStoreName();
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,CommentActivity.class);
                        intent.putExtra("extra_data",storeName);
                        intent.putExtra("data1",user);
                        startActivity(intent);
                    }
                });



                mMarkerly.setVisibility(View.VISIBLE);
                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerly.setVisibility(View.GONE);

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

//    public Bitmap stringtoBitmap(String string) {
//        //将字符串转换成Bitmap类型
//        Bitmap bitmap = null;
//        try {
//            byte[] bitmapArray;
//            bitmapArray = Base64.decode(string, Base64.DEFAULT);
//            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
//            return bitmap;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private void initMarkers() {
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        mMarkerly = (RelativeLayout) findViewById(R.id.marker_info);
        Button option = (Button) findViewById(R.id.id_add_overlay);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShow) {
                    //发送检索商家请求到服务器
                    SearchFormRequest request = new SearchFormRequest();
                    request.setLatitude(mLatitude);
                    request.setLongitude(mLongtitude);
//                    request.setLatitude(32.179033);
//                    request.setLongitude(118.716370);
                    doSearch(request);
                    isShow = true;
                } else {
                    mBaiduMap.clear();
                    isShow = false;
                }
            }
        });
    }

    private void addOverlay(List<Store> infos) {
        mBaiduMap.clear();
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options;
        for (Store info : infos) {
            // 经纬度
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            // 图标
            options = new MarkerOptions().position(latLng).icon(mMarker)
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info);
            marker.setExtraInfo(arg0);
        }

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);

    }

    private void initialView() {
        mapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
        mBaiduMap.setMapStatus(msu);
    }

    private void initLocation() {
        myClient = new LocationClient(getApplicationContext());
        myClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 高精度
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setIsNeedAddress(true);// 位置，一定要设置，否则后面得不到地址
        option.setOpenGps(true);// 打开GPS
        option.setScanSpan(3000);// 多长时间进行一次请求
        option.setNeedDeviceDirect(true);        // 返回的定位结果包含手机机头的方向
        myClient.setLocOption(option);
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.arrow);
        mOrientationListener = new MyOrientationListener(this);
        mOrientationListener
                .setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        mCurrentX = x;
                    }
                });
    }

    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if (!myClient.isStarted()) {
            myClient.start();
            mOrientationListener.start();
        }
    }

    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        myClient.stop();
        mOrientationListener.stop();
    }

    protected void onDestory() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData data = new MyLocationData.Builder()
                    .direction(mCurrentX)
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(data);
            MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mIconLocation);
            mBaiduMap.setMyLocationConfiguration(config);
            mLatitude = location.getLatitude();
            mLongtitude = location.getLongitude();
            if (isFirstLocate) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(update);
                isFirstLocate = false;
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    Toast.makeText(MainActivity.this, "获取数据成功", Toast.LENGTH_SHORT).show();
                    //获取数据成功，将数据添加到地图
                    StoreList response = (StoreList) msg.obj;
                    addOverlay(response.getList());
                    break;
                case 201:
                    Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 发送商家搜索请求到服务器
     *
     * @param request
     */
    public void doSearch(final SearchFormRequest request) {
        final String url = Constants.BASE_URL + "/merchant/search";
        try {
            HttpUtils.post(url, JSONHelper.toJSON(request), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String content = response.body().string();
                    final StoreList gatewayResponse = JSONHelper.fromJSON(content, StoreList.class);
                    Log.i("TEST", "返回的状态码:" + gatewayResponse.getStatus());
                    //使用Handler异步处理UI
                    Message msg = Message.obtain();
                    if (gatewayResponse.getStatus() == 200) {
                        msg.what = gatewayResponse.getStatus();
                        msg.obj = gatewayResponse;
                    } else {
                        msg.what = gatewayResponse.getStatus();
                    }
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


