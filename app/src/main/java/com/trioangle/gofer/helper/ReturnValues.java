package com.trioangle.gofer.helper;
/**
 * @package com.trioangle.gofer
 * @subpackage helper
 * @category ReturnValues
 * @author Trioangle Product Team
 * @version 1.5
 */

import com.google.android.gms.maps.model.LatLng;

/* ************************************************************
 Return latitude and longitude Values for animate route in Main Activity
*************************************************************** */
public class ReturnValues {
    private final LatLng latlng;
    private final String country;

    public ReturnValues(LatLng latlng, String country) {
        this.latlng = latlng;
        this.country = country;
    }

    /**
     * return latitude and longitude
     */
    public LatLng getLatLng() {
        return latlng;
    }

    /**
     * return country
     */
    public String getCountry() {
        return country;
    }
}