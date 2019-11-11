package com.trioangle.gofer.interfaces;

/**
 * @package com.trioangle.gofer
 * @subpackage interfaces
 * @category ServiceListener
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.trioangle.gofer.datamodels.JsonResponse;

/*****************************************************************
 ServiceListener
 ****************************************************************/
public interface ServiceListener {

    void onSuccess(JsonResponse jsonResp, String data);

    void onFailure(JsonResponse jsonResp, String data);
}
