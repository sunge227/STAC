package dgsw.hs.kr.nugu;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by minseong on 2017-07-27.
 */

public class GPStrans extends Activity {

    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    double lat;
    double lon;
    TextView p;
    TextView aress;
    ToggleButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        p = (TextView)findViewById(R.id.pos);
        p.setText("위치정보 미수신중");
        aress = (TextView)findViewById(R.id.address);
        aress.setText("현재 위치를 확인 할 수 없습니다.");

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btn = (ToggleButton) findViewById(R.id.trans);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(btn.isChecked()){
                         p.setText("수신중...");
                         lm.requestLocationUpdates(LocationManager.GPS_PROVIDER ,
                                 100 , // 최소 시간 간격
                                 1 ,   // 최소 변경 거리
                                 mLocationListener);
                         lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                 100,
                                 1,
                                 mLocationListener);
                    }else{
                        p.setText("위치정보 미수신중");
                        aress.setText("위치 알수없음");
                        lm.removeUpdates(mLocationListener);
                    }
                }catch (SecurityException e){
                    e.printStackTrace();
                }
            }
        });
    } // end of onCreate

    private final LocationListener mLocationListener = new LocationListener() {


        @Override
        public void onLocationChanged(Location location) {
            double lon = location.getLongitude();
            double lat = location.getLatitude();

            p.setText("lat : "+lat+" "+"lon : "+lon);
            aress.setText(getAddress(GPStrans.this,lat,lon));

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
    };// end of LocationListener

    public static String getAddress(Context mContext , double lat , double lon){
        String nowAddress = "";
        Geocoder geocoder = new Geocoder(mContext , Locale.KOREA);
        List<Address> address;

        try{
            if(geocoder != null){
                address = geocoder.getFromLocation(lat , lon , 1); // 3번째 파라미터는 주소의 이름이 여러개 일때 받을 최대 갯수

                if(address != null && address.size() > 0){
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress = currentLocationAddress;
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return nowAddress;

    }


}//end of file
