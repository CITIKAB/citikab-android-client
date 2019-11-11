package com.trioangle.gofer.views.customize;
/**
 * @package com.trioangle.gofer
 * @subpackage view.customize
 * @category BaseDialogFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trioangle.gofer.R;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogD;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;

/*****************************************************************
 Custom dialog base fragment
 ****************************************************************/
public class BaseDialogFragment extends DialogFragment {
    protected Activity mActivity;
    private int layoutId;
    private boolean isNeedAnimation = true;


    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public void setAnimation(boolean isNeedAnimation) {
        this.isNeedAnimation = isNeedAnimation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNeedAnimation) {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.share_dialog);
        } else {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.progress_dialog);
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            DebuggableLogE("ABSDIALOGFRAG", "Exception", e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, container, false);
        initViews(v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = context instanceof Activity ? (Activity) context : null;
    }

    public void initViews(View v) {
        getDialog().setCanceledOnTouchOutside(false);
    }
}
