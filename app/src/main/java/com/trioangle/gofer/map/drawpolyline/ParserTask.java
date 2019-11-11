package com.trioangle.gofer.map.drawpolyline;
/**
 * @package com.trioangle.gofer
 * @subpackage map.drawpolyline
 * @category ParserTask
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* ************************************************************
 A class to parse the Google Places in JSON format
*************************************************************** */
public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    public PolylineOptionsInterface polylineOptionsInterface = null;
    public Context mContext;

    public ParserTask(PolylineOptionsInterface polylineOptionsInterface, Context mContext) {
        this.polylineOptionsInterface = polylineOptionsInterface;
        this.mContext = mContext;
    }

    /**
     * Parsing the data in non-ui thread
     */
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        if (isOnline(mContext)) {
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        try {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(6);
                    lineOptions.color(Color.BLACK);
                    lineOptions.geodesic(true);

                }
                // Drawing polyline in the Google Map for the i-th route
                polylineOptionsInterface.getPolylineOptions(lineOptions, points);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
