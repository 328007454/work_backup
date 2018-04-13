package com.cnksi.bdzinspection.fragment.inspectionready;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ListContentDialogAdapter;
import com.cnksi.bdzinspection.adapter.inspectionready.ToolsAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.BaseService;
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.bdzinspection.model.ReportTool;
import com.cnksi.bdzinspection.model.Tool;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.xscore.xsutils.ScreenUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhy.core.utils.AutoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/7/25 17:35
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ToolFragment extends BaseFragment {
    LinearLayout linearLayout;
    ScrollView scrollView;
    List<Tool> mToolsList;
    HashMap<String, ReportTool> toolHashMap = new HashMap<>();
    ToolsAdapter toolsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.xs_fragment_tool, null);
        linearLayout = (LinearLayout) v.findViewById(R.id.ll_container);
        scrollView = (ScrollView) v.findViewById(R.id.scrollView);
        getBundleValue();
        initKeyBoard();
        return v;
    }

    private void initKeyBoard() {
        final View myLayout = currentActivity.getWindow().getDecorView();
        myLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                myLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = myLayout.getRootView().getHeight();
                int heightDiff = screenHeight - (r.bottom - r.top);
                //154为底部Button占用高度
                scrollView.setPadding(0, 0, 0, heightDiff - AutoUtils.getPercentHeightSize(154));
            }
        });
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared)
            mFixedThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Selector selector = BaseService.from(Tool.class).and(Tool.INSPECTION, "=", currentInspectionType);
                    try {
                        mToolsList = XunshiApplication.getDbUtils().findAll(selector);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(currentReportId)) {
                        selector = BaseService.from(ReportTool.class).and(ReportTool.REPORTID, "=", currentReportId);
                        try {
                            List<ReportTool> reportTools = XunshiApplication.getDbUtils().findAll(selector);
                            if (null == reportTools)
                                reportTools = new ArrayList<ReportTool>();
                            if (reportTools.size() > 0) {
                                for (ReportTool tool : reportTools) {
                                    toolHashMap.put(tool.toolId, tool);
                                }
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toolsAdapter = new ToolsAdapter(currentActivity, mToolsList, linearLayout, new ItemClickListener<Tool>() {
                                @Override
                                public void onItemClick(View v, Tool o, int position) {
                                    showToolStandardDialog(o);
                                }
                            });
                            toolsAdapter.setToolMap(toolHashMap);
                            toolsAdapter.notifyDataSetChanged();
                            isPrepared = true;
                        }
                    });
                }
            });
    }

    private Dialog mToolStandardDialog = null;
    ListContentDialogAdapter mListContentAdapter;

    public void save(String reportId) {
        final List<ReportTool> saveList = new ArrayList<>();
        if (toolsAdapter == null)
            return;
        for (int i = 0; i < toolsAdapter.getCount(); i++) {
            View v = linearLayout.getChildAt(i);
            EditText et = (EditText) v.findViewById(R.id.et_input_values);
            String num = et.getText().toString();
            Tool t = mToolsList.get(i);
            ReportTool reportTool = toolHashMap.get(t.toolid);
            if (reportTool == null) {
                if (!TextUtils.isEmpty(num))
                    reportTool = new ReportTool(reportId, t.toolid, num);
                else continue;
            } else {
                if (!num.equals(reportTool.num)) {
                    reportTool.num = num;
                } else continue;
            }
            saveList.add(reportTool);
        }
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    XunshiApplication.getDbUtils().saveAll(saveList);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示工具检查标准的dialog
     */
    private XsContentListDialogBinding dialogBinding;

    private void showToolStandardDialog(Tool tool) {
        List<String> toolStandardList = Arrays.asList(tool.check_standard.split("；|;"));
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
        if (mListContentAdapter == null) {
            mListContentAdapter = new ListContentDialogAdapter(currentActivity, toolStandardList);
        } else {
            mListContentAdapter.setList(toolStandardList);
        }
        dialogBinding = XsContentListDialogBinding.inflate(getActivity().getLayoutInflater());
        mToolStandardDialog = DialogUtils.createDialog(currentActivity, dialogBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogBinding.lvContainer.setAdapter(mListContentAdapter);
        dialogBinding.tvDialogTitle.setText(tool.name + "检查标准");

        mToolStandardDialog.show();
    }
}