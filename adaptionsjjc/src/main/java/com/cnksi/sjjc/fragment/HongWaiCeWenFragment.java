package com.cnksi.sjjc.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.activity.ImageDetailsActivity;
import com.cnksi.sjjc.bean.ReportHwcw;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by han on 2016/5/6.
 */
public class HongWaiCeWenFragment extends BaseCoreFragment {

    public ReportHwcw currentReport;


    /**
     * 发热设备名称
     */
    @ViewInject(R.id.frsbmc)
    private TextView frsbmc;
    /**
     * 具体发热部位
     */
    @ViewInject(R.id.jtfrbw)
    private TextView jtfrbw;
    /**
     * 保护屏背面温度
     */
    @ViewInject(R.id.bhpbmwd)
    private TextView bhpbmwd;
    /**
     * 保护屏正面温度
     */

    @ViewInject(R.id.bhpzmwd)
    private TextView bhpzmwd;
    /**
     * 环境参照温度
     */
    @ViewInject(R.id.hjcztwd)
    private TextView hjcztwd;
    /**
     * 温差
     */
    @ViewInject(R.id.wc)
    private TextView wc;
    /**
     * 相对温差
     */
    @ViewInject(R.id.xdwc)
    private TextView xdwc;
    /**
     * 额定电流
     */
    @ViewInject(R.id.fhdl)
    private TextView fhdl;
    /**
     * 负荷电流
     */
    @ViewInject(R.id.eddl)
    private TextView eddl;
    /**
     * 测试时间
     */
    @ViewInject(R.id.cssj)
    private TextView cssj;

    /**
     * image 容器
     */
    @ViewInject(R.id.ll_image_container)
    private LinearLayout imageContainer;

    private boolean isBhpcw=false;
    /**
     * 图像listview
     */
    private ArrayList<String> imageList=new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hwcw_report_layout, container, false);
        x.view().inject(view);
        getBundleValue();
        initData();
        return view;
    }

    @Override
    protected void lazyLoad() {

    }


    public  String convertString(String value,String hz)
    {
        if (value==null)
        {
            return "";
        }else if (TextUtils.isEmpty(value.trim()))
        {
            return "";
        }else{
            return value+hz;
        }
    }

    @Override
    protected void initUI() {

        if (currentReport!=null){
            frsbmc.append(convertString(currentReport.device_name,""));
            jtfrbw.append(convertString(currentReport.frbw,""));
            xdwc.append(convertString(currentReport.xdwc,"%"));
            wc.append(convertString(currentReport.wc,"K"));
            fhdl.append(convertString(currentReport.fhdl,"A"));
            eddl.append(convertString(currentReport.eddl,"A"));
            hjcztwd.append(convertString(currentReport.hjcztwd,"℃"));
            cssj.append(currentReport.insert_time);
            if (!isBhpcw)
            {
                bhpbmwd.setVisibility(View.GONE);
                bhpzmwd.setText("发热点温度：");
                bhpzmwd.append(convertString(currentReport.frdwd,"℃"));

            }else{
                bhpzmwd.append(convertString(currentReport.bhpzmwd,"℃"));
                bhpbmwd.append(convertString(currentReport.bhpbmwd,"℃"));
            }
            if (!TextUtils.isEmpty(currentReport.hwtx))
            {
                String img[]=currentReport.hwtx.split(CoreConfig.COMMA_SEPARATOR);
                for (String str :img)
                {
                    imageList.add(Config.RESULT_PICTURES_FOLDER+str);
                }
            }
            if (!TextUtils.isEmpty(currentReport.kjgtx))
            {
                String img[]=currentReport.kjgtx.split(CoreConfig.COMMA_SEPARATOR);
                for (String str:img)
                {
                    imageList.add( Config.RESULT_PICTURES_FOLDER+ str);
                }
            }
            for (String path:imageList)
            {
                if (TextUtils.isEmpty(path)||path.equalsIgnoreCase("null"))
                {
                    continue;
                }
                View view=LayoutInflater.from(getActivity()).inflate(R.layout.imageview,null);
                ImageView imageView= (ImageView) view.findViewById(R.id.imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImageDetails(getActivity(),0,imageList,false);
                    }
                });
                imageContainer.addView(view);
                x.image().bind(imageView,path, CustomApplication.getImageOPtions());
            }

        }
    }

    @Override
    protected void initData() {

    }

    protected void getBundleValue() {
        currentReport = (ReportHwcw) bundle.getSerializable(Config.CURRENT_REPORT);
        isBhpcw=bundle.getBoolean(Config.STATUS);
    }

    /**
     * 显示大图片
     *
     * @param position
     */
    public void showImageDetails(Activity context, int position, ArrayList<String> mImageUrlList,
                                 boolean isShowDelete) {
        Intent intent = new Intent(context, ImageDetailsActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
        if (mImageUrlList != null) {
            intent.putStringArrayListExtra(Config.IMAGEURL_LIST, mImageUrlList);
        }
        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, isShowDelete);
        context.startActivityForResult(intent, BaseActivity.CANCEL_RESULT_LOAD_IMAGE);
    }
}
