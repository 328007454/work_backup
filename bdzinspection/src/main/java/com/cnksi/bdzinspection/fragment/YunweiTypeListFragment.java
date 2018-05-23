package com.cnksi.bdzinspection.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.activity.AddTaskActivity;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.databinding.XsFragmentExpadableListBinding;
import com.cnksi.bdzinspection.model.Project;
import com.cnksi.bdzinspection.ywyth.adapter.YunweiTypeAdapter;
import com.cnksi.common.Config;
import com.cnksi.core.common.ExecutorManager;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 运维一体化Fragment
 * 
 */
public class YunweiTypeListFragment extends BaseFragment {


	private LinkedList<Project> groupList = new LinkedList<Project>();
	private HashMap<Project, ArrayList<Project>> groupHashMap = new HashMap<Project, ArrayList<Project>>();
	private YunweiTypeAdapter mYunAdapter = null;
	
	XsFragmentExpadableListBinding binding;
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = XsFragmentExpadableListBinding.inflate(inflater);
		initialUI();
		lazyLoad();
		initOnClick();
		return binding.getRoot();
	}



	private void initialUI() {
		getBundleValue();
		isPrepared = true;
	}

	@Override
	protected void onRefresh(Message msg) {
		switch (msg.what) {
		case LOAD_DATA:

			if (mYunAdapter == null) {
				mYunAdapter = new YunweiTypeAdapter(currentActivity);
				binding.elvContainer.setAdapter(mYunAdapter);
			}
			mYunAdapter.setGroupList(groupList);
			mYunAdapter.setGroupMap(groupHashMap);

			break;
		default:
			break;
		}
	}

	@Override
	protected void lazyLoad() {
		if (isPrepared && isVisible && isFirstLoad) {
			// 填充各控件的数据
			searchData();
			isFirstLoad = false;
		}
	}

	@Override
    public void setFirstLoad(boolean isFirstLoad) {
		this.isFirstLoad = isFirstLoad;
	}

	/**
	 * 查询数据
	 */
	public void searchData() {

		ExecutorManager.executeTask(() -> {
            try {
                Selector selector = XunshiApplication.getDbUtils().selector(Project.class).where(Project.LOOKUP_TYPE, "=", currentFunctionModel);
                List<Project> projectList = selector.findAll();
                groupList = new LinkedList<Project>(projectList);
                if (groupList != null && !groupList.isEmpty()) {
                    for (Project mProject : groupList) {
                        selector = XunshiApplication.getDbUtils().selector(Project.class).where(Project.PARENT_ID, "=", mProject.id);
                        List<Project> childProjectList = selector.findAll();
                        ArrayList<Project> childList = null;
                        if (childProjectList == null || childProjectList.isEmpty()) {
                            childList = new ArrayList<Project>();
                        } else {
                            childList = new ArrayList<Project>(childProjectList);
                        }
                        groupHashMap.put(mProject, new ArrayList<Project>(childList));
                    }
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
	}

	private void initOnClick() {
		binding.elvContainer.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, l) -> {
			Project mProject = mYunAdapter.getChild(groupPosition, childPosition);
			backResult(mProject);
			return false;
		});

		binding.elvContainer.setOnGroupClickListener((expandableListView, view, groupPosition, id) -> {
			int childCount = mYunAdapter.getChildrenCount(groupPosition);
			Project mProject = mYunAdapter.getGroup(groupPosition);
			if (childCount <= 0) {
				backResult(mProject);
			}
			return false;
		});

	}

	/**
	 * 将选择的运维一体化带回去
	 * 
	 * @param mProject
	 */
	private void backResult(Project mProject) {
		Intent intent = new Intent(currentActivity, AddTaskActivity.class);
		intent.putExtra(Config.CURRENT_INSPECTION_TYPE, mProject.id);
		intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, mProject.pro_name);
		currentActivity.setResult(Activity.RESULT_OK, intent);
		currentActivity.finish();
	}
}
