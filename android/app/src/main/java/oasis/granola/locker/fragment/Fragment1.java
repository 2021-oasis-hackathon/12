package oasis.granola.locker.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import oasis.granola.locker.MainActivity;
import oasis.granola.locker.R;

public class Fragment1 extends Fragment {

    private MapView mapView;
    private int hostId;

    private boolean firstGetLocationFlag;

    Dialog customDialog;

//    위치정보를 읽어들이지 못했을 때 기본좌표
    private double currLatitude = 36.7960209;
    private double currLongitude = 127.1314041;
    private MapView.POIItemEventListener markerClickListner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);

        firstGetLocationFlag = true;

        customDialog = new CustomDialog((MainActivity)getActivity());
        customDialog.setContentView(R.layout.locker_dialog);
        customDialog.getWindow().setGravity(Gravity.BOTTOM);

        mapView = new MapView(getActivity());
        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.map_view);
        ImageView btn = (ImageView) view.findViewById(R.id.moveBtn);
        mapViewContainer.addView(mapView);
//        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.7960209 , 127.1314041), true);


        markerClickListner = new MarkerClickListner();
        mapView.setPOIItemEventListener(markerClickListner);

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
                getLockers();
                firstGetLocationFlag = !firstGetLocationFlag;
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
                mapView.setZoomLevel(4, true);

                customDialog.show();
                customDialog.cancel();
            }

                //마커 찍기
                MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                MapPOIItem marker = new MapPOIItem();

                marker.setItemName("Default Marker");
                marker.setTag(-1);
                marker.setMapPoint(MARKER_POINT);
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                mapView.addPOIItem(marker);
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

    public void getLockers() {
        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }
        String url = "http://" + AppHelper.hostUrl + "/locker/get";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject o = (JSONObject) data.get(i);
                                double latitude = o.getDouble("latitude");
                                double longitude = o.getDouble("longitude");

                                MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                                MapPOIItem marker = new MapPOIItem();
                                marker.setItemName("Default Marker");
                                marker.setTag(i);
                                marker.setMapPoint(MARKER_POINT);
                                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                                marker.setUserObject(o);

                                mapView.addPOIItem(marker);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                            정보 못읽어옴 알림
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    class clickListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(currLatitude, currLongitude), true);
            mapView.setZoomLevel(2, true);
        }
    }

    class MarkerClickListner implements MapView.POIItemEventListener {

        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            if (mapPOIItem.getTag() == -1) {
                return;
            }
            TextView dialogName = (TextView)customDialog.findViewById(R.id.locker_name);
            TextView dialogAddress = (TextView)customDialog.findViewById(R.id.locker_address);
            TextView dialogAddressDetail = (TextView)customDialog.findViewById(R.id.locker_address_detail);
            Button chatBtn = (Button) customDialog.findViewById(R.id.btnChat);
            chatBtn.setOnClickListener(chatClickListener);

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
}
