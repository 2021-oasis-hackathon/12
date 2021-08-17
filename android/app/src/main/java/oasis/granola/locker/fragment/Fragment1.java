package oasis.granola.locker.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import oasis.granola.locker.AppHelper;
import oasis.granola.locker.CustomDialog;
import oasis.granola.locker.LoginActivity;
import oasis.granola.locker.MainActivity;
import oasis.granola.locker.R;
import oasis.granola.locker.ScanActivity;

import static android.content.Context.MODE_PRIVATE;

public class Fragment1 extends Fragment {

    private MapView mapView;
    private int hostId;

    private SharedPreferences tokenStore;
    private String token;
    private Boolean isEntrust = false;
    private boolean firstGetLocationFlag;

    Dialog customDialog;

//    위치정보를 읽어들이지 못했을 때 기본좌표
    private double currLatitude = 36.7960209;
    private double currLongitude = 127.1314041;
    private MapView.POIItemEventListener markerClickListner;
    private MapView.MapViewEventListener mapMoveListner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);

        firstGetLocationFlag = true;
        tokenStore = getActivity().getSharedPreferences("tokenStore", MODE_PRIVATE);
        token = tokenStore.getString("token", null);

        customDialog = new CustomDialog((MainActivity)getActivity());
        customDialog.setContentView(R.layout.locker_dialog);
        customDialog.getWindow().setGravity(Gravity.BOTTOM);

        mapView = new MapView(getActivity());
        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.map_view);
        ImageView btn = (ImageView) view.findViewById(R.id.moveBtn);
        mapViewContainer.addView(mapView);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        markerClickListner = new MarkerClickListner();
        mapMoveListner = new MapMoveListner();
        mapView.setPOIItemEventListener(markerClickListner);
        mapView.setMapViewEventListener(mapMoveListner);

        btn.setOnClickListener(new clickListner());
        GPSListener gpsListener = new GPSListener();
        long minTime = 1000;
        float minDistance = 1;
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
        return view;
    }

    private class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            currLatitude = latitude;
            currLongitude = longitude;
            if (firstGetLocationFlag) {
//                여기서 주변 보관소 정보 가져옴
                getLockers(token);
                firstGetLocationFlag = !firstGetLocationFlag;
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
                mapView.setZoomLevel(4, true);

                customDialog.show();
                customDialog.cancel();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public void getLockers(String token) {
        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }
        String url = "http://" + AppHelper.hostUrl + "/locker/get?token=" + token;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray list = data.getJSONArray("lockers");
                            isEntrust = data.getBoolean("entrust");

                            if (isEntrust) {
                                JSONObject o = (JSONObject) list.get(0);
                                setMarker(o, 0, true);
                            } else {
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject o = (JSONObject) list.get(i);
                                    setMarker(o, i, false);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getActivity().finish();
//                            정보 못읽어옴 알림
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public void setMarker(JSONObject o, int index, boolean entrust) {
        double latitude = 0;
        double longitude = 0;
        String name = "";

        try {
            latitude = o.getDouble("latitude");
            longitude = o.getDouble("longitude");
            name = o.getString("lockerName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(name);
        marker.setTag(index);
        marker.setMapPoint(MARKER_POINT);

        marker.setMarkerType((entrust) ? MapPOIItem.MarkerType.RedPin : MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setUserObject(o);

        mapView.addPOIItem(marker);
    }

    class clickListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(currLatitude, currLongitude), true);
            mapView.setZoomLevel(4, true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        }
    }

    class MapMoveListner implements MapView.MapViewEventListener {

        @Override
        public void onMapViewInitialized(MapView mapView) {

        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        }
    }


    class MarkerClickListner implements MapView.POIItemEventListener {

        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            TextView dialogName = (TextView)customDialog.findViewById(R.id.locker_name);
            TextView dialogAddress = (TextView)customDialog.findViewById(R.id.locker_address);
            TextView dialogAddressDetail = (TextView)customDialog.findViewById(R.id.locker_address_detail);
            Button chatBtn = (Button) customDialog.findViewById(R.id.btnChat);
            chatBtn.setOnClickListener(chatClickListener);
            Button entrustBtn = (Button) customDialog.findViewById(R.id.btnEntrust);
            entrustBtn.setOnClickListener(entrustClickListner);
            if (isEntrust) {
                entrustBtn.setText("되찾기");
            }

            JSONObject lockerInfo = (JSONObject) mapPOIItem.getUserObject();
            try {
                String lockerName = lockerInfo.getString("lockerName");
                String address = lockerInfo.getString("address");
                String addressDetail = lockerInfo.getString("addressDetail");
                int userId = lockerInfo.getInt("userId");

                hostId = userId;
                dialogName.setText(lockerName);
                dialogAddress.setText(address);
                dialogAddressDetail.setText(addressDetail);
                customDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    }

    public View.OnClickListener chatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt("hostId", hostId);
            Fragment2 fragment2 = new Fragment2();
            fragment2.setArguments(bundle);
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bnv_main);
            bottomNavigationView.setSelectedItemId(R.id.second);
            ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment2 ).commit();
        }
    };

    public View.OnClickListener entrustClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ScanActivity.class);
            intent.putExtra("isEntrust", isEntrust);
            startActivity(intent);
            getActivity().finish();
        }
    };
}
