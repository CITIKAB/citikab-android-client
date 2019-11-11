package com.trioangle.gofer.interfaces;
/**
 * @package com.trioangle.gofereatsdriver
 * @subpackage interfaces
 * @category YourTripsListener
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.res.Resources;

import com.trioangle.gofer.sidebar.trips.YourTrips;


/*****************************************************************
 YourTripsListener
 ****************************************************************/

public interface YourTripsListener {

    Resources getRes();

    YourTrips getInstance();
}
