package oasis.granola.locker.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import oasis.granola.locker.AppHelper;
import oasis.granola.locker.MainActivity;
import oasis.granola.locker.R;

public class Fragment1 extends Fragment {

    private MapView mapView;
//    위치정보를 읽어들이지 못했을 때 기본좌표
    private double currLatitude = 36.7960209;
    private double currLongitude = 127.1314041;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        mapView = new MapView(getActivity());
        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.map_view);
        ImageView btn = (ImageView) view.findViewById(R.id.moveBtn);
        mapViewContainer.addView(mapView);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(36.7960209 , 127.1314041), true);

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
        MapPoint MARKER_POINT;
        MapPOIItem marker = new MapPOIItem();
        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            currLatitude = latitude;
            currLongitude = longitude;
            if (((MainActivity)getActivity()).firstGetLocationFlag) {
//                여기서 주변 보관소 정보 가져옴
                getLockers();
                ((MainActivity)getActivity()).firstGetLocationFlag = !((MainActivity)getActivity()).firstGetLocationFlag;
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
                mapView.setZoomLevel(4, true);
            }

                //마커 찍기
                MARKER_POINT = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                marker.setItemName("Default Marker");
                marker.setTag(0);
                marker.setMapPoint(MARKER_POINT);
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                mapView.addPOIItem(marker);
            Toast.makeText(getActivity(), latitude + "   " + longitude , Toast.LENGTH_SHORT).show();
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
                            MapPoint MARKER_POINT;
                            MapPOIItem marker = new MapPOIItem();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject o = (JSONObject) data.get(i);
                                double latitude = o.getDouble("latitude");
                                double longitude = o.getDouble("longitude");
                                MARKER_POINT = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                                marker.setItemName("Default Marker");
                                marker.setTag(0);
                                marker.setMapPoint(MARKER_POINT);
                                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

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
            mapView.setZoomLevel(4, true);
        }
    }
}
