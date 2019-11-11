package com.trioangle.gofer.map;
/**
 * @package com.trioangle.gofer
 * @subpackage map
 * @category RouteEvaluator
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.animation.TypeEvaluator;

import com.google.android.gms.maps.model.LatLng;

/* ************************************************************
RouteEvaluator to give the LatLng
*************************************************************** */
public class RouteEvaluator implements TypeEvaluator<LatLng> {
    @Override
    public LatLng evaluate(float t, LatLng startPoint, LatLng endPoint) {
        double lat = startPoint.latitude + t * (endPoint.latitude - startPoint.latitude);
        double lng = startPoint.longitude + t * (endPoint.longitude - startPoint.longitude);
        return new LatLng(lat, lng);
    }
}
