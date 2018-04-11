package com.cnksi.bdzinspection.fragment.inspectionready;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.RoadMapAdapter;
import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.daoservice.BaseService;
import com.cnksi.bdzinspection.databinding.XsFragmentRoadmapBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.bdzinspection.model.Spacing;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhy.core.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡视线路图Fragment
 *
 * @author Joe
 */
public class RoadMapFragment extends BaseFragment {
    //间隔集合
    private List<Spacing> spaceList = new ArrayList<Spacing>();
    //当前设备类型
    private String fucntionModel = "one";

    private RoadMapAdapter spaceRoadMapAdapter;
    
    XsFragmentRoadmapBinding binding;
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentRoadmapBinding.inflate(getActivity().getLayoutInflater());
        AutoUtils.autoSize(binding.getRoot());
        initUI();
        return binding.getRoot();
    }

    private void initUI() {

        getBundleValue();
        binding.zoomImageView.setVisibility(View.GONE);
        binding.ltvSpaces.setVisibility(View.VISIBLE);
        isPrepared = true;

    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            // 填充各控件的数据
            searchData(LOAD_DATA);
            isFirstLoad = false;
        }
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA: // 加载数据时判断路线图是否存在
//			if (bdz != null && !TextUtils.isEmpty(bdz.roadmap)) {
//				// 判断文件是否存在，存在则显示该路线图
//				File file = new File(Config.PICTURES_FOLDER, bdz.roadmap);
//				if (file.exists()) {
//					BitmapFactory.Options options = new BitmapFactory.Options();
//					options.inSampleSize = 2;
//					Bitmap bm = BitmapFactory.decodeFile(Config.PICTURES_FOLDER + File.separator + bdz.roadmap, options);
//					binding.zoomImageView.setImageBitmap(bm);
//				}
//			}
                if (spaceRoadMapAdapter == null) {
                    spaceRoadMapAdapter = new RoadMapAdapter(currentActivity, spaceList);
                } else {
                    spaceRoadMapAdapter.setList(spaceList);
                }
                spaceRoadMapAdapter.setIsReport(false);
                binding.ltvSpaces.setAdapter(spaceRoadMapAdapter);
                break;
            default:
                break;
        }
    }

    /**
     * 查询数据
     *
     * @param
     * @param
     */
    public void searchData(int requestCode) {
        String sort = "one".equals(fucntionModel) ? Spacing.SORT_ONE
                : "second".equals(fucntionModel) ? Spacing.SORT_SECOND : Spacing.SORT;
        currentBdzId = PreferencesUtils.getString(currentActivity, Config.CURRENT_BDZ_ID, "");
        Selector selector = BaseService.from(Spacing.class).and(Spacing.BDZID, "=", currentBdzId)
                .expr("and spid in (select distinct(spid) spid from device where device_type = '" + fucntionModel
                        + "' and bdzid = '" + currentBdzId + "' and dlt<>1)")
                .orderBy(sort, false);
        try {
            spaceList = CustomApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//		try {
//			bdz = CustomApplication.getDbUtils().findById(Bdz.class, currentBdzId);
//		} catch (DbException e) {
//			e.printStackTrace();
//		}
        mHandler.sendEmptyMessage(LOAD_DATA);
    }

    public void reLoadData() {
        if (null == currentActivity || TextUtils.isEmpty(fucntionModel) || TextUtils.isEmpty(currentBdzId))
            return;
        searchData(0);
    }
}
