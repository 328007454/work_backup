package com.cnksi.bdzinspection.fragment.inspectionready;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.RoadMapAdapter;
import com.cnksi.bdzinspection.databinding.XsFragmentRoadmapBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.SpacingService;
import com.cnksi.common.model.Spacing;
import com.cnksi.core.utils.PreferencesUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 巡视线路图Fragment
 *
 * @author Joe
 */
public class RoadMapFragment extends BaseFragment {
    //间隔集合
    private List<Spacing> spaceList = new ArrayList<>();
    //当前设备类型
    private String fucntionModel = "one";

    private RoadMapAdapter spaceRoadMapAdapter;

    XsFragmentRoadmapBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentRoadmapBinding.inflate(getActivity().getLayoutInflater());
        AutoUtils.autoSize(binding.getRoot());
        initialUI();
        return binding.getRoot();
    }

    private void initialUI() {

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
        currentBdzId = PreferencesUtils.get(Config.CURRENT_BDZ_ID, "");
        try {

            spaceList = SpacingService.getInstance().findByFunctionModel(currentReportId,currentBdzId,fucntionModel,sort,currentInspectionType);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mHandler.sendEmptyMessage(LOAD_DATA);
    }

    public void reLoadData() {
        if (null == currentActivity || TextUtils.isEmpty(fucntionModel) || TextUtils.isEmpty(currentBdzId)) {
            return;
        }
        searchData(0);
    }
}
