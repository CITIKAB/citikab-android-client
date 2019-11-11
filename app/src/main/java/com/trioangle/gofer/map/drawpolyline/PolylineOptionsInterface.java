package com.trioangle.gofer.map.drawpolyline;

/**
 * @package com.trioangle.gofer
 * @subpackage map.drawpolyline
 * @category PolylineOptions Interface
 * @author Trioangle Product Team
 * @version 1.5
 */

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/* ************************************************************
Interface for draw route
*************************************************************** */
public interface PolylineOptionsInterface {
    void getPolylineOptions(PolylineOptions output, ArrayList points);
}
