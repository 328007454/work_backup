package com.cnksi.sjjc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.common.Config;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sjjc.bean.AppVersion;
import com.cnksi.sjjc.databinding.DialogCopyTipsBinding;
import com.cnksi.sjjc.util.AppUtils;
import com.cnksi.sjjc.util.FunctionUtils;
import com.cnksi.sjjc.util.UpdateUtils;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;

import java.io.File;
import java.util.ArrayList;


/**
 * @author luoxy
 * @version 1.0
 * @date 16/4/23
 */
public abstract class BaseSjjcActivity extends BaseTitleActivity {


    /**
     * 显示大图片
     *
     * @param position
     */
    public void showImageDetails(Activity context, int position, ArrayList<String> mImageUrlList,
                                 boolean isShowDelete, boolean isDeleteFile) {
        ImageDetailsActivity.with(context).setPosition(position).setImageUrlList(mImageUrlList).setDeleteFile(isDeleteFile).setShowDelete(isShowDelete).start();
    }


    /**
     * 可以标记图片
     */

    public void drawCircle(String pictureName, String pictureContent) {
        DrawCircleImageActivity.with(mActivity).setTxtContent(pictureContent).setPath(pictureName).start();
    }


}
