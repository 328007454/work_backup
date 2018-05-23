/**
 *
 */
package com.cnksi.bdzinspection.ywyth;

import android.os.Bundle;
import android.widget.CheckBox;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.TitleActivity;
import com.cnksi.bdzinspection.daoservice.YthService;
import com.cnksi.bdzinspection.databinding.XsActivityYwythGqjBinding;
import com.cnksi.bdzinspection.model.GQJ;
import com.cnksi.bdzinspection.ywyth.adapter.YWGQJAdapter;
import com.cnksi.common.daoservice.TaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运维工器具界面
 * @author wastrel
 * @Date 2016-3-24
 *
 */
public class YWGQJActivity extends TitleActivity {
	private List<GQJ> dlist;
	public Map<Integer, Boolean> flag;
	private boolean taskStatus = false;
	private XsActivityYwythGqjBinding binding;
	/*
	 * (non-Javadoc)
	 * @see com.cnksi.bdzinspection.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.xs_activity_ywyth_gqj;
	}

	@Override
	protected String initialUI() {
		binding = (XsActivityYwythGqjBinding) getDataBinding();
		return getResources().getString(R.string.xs_yw_gqj);

	}

	/*
	 * (non-Javadoc)
	 * @see com.cnksi.bdzinspection.activity.TitleActivity#initialData()
	 */
	@Override
	protected void initialData() {
		getIntentValue();
		taskStatus = TaskService.getInstance().getTaskStatusForBoolean(currentTaskId);
		// TODO Auto-generated method stub
		try {
			dlist = YthService.getInstance().findGqjById(currentInspectionType);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (dlist == null) {
			dlist = new ArrayList<GQJ>();
		}
		flag = getGqjcheck().get(currentTaskId);
		if (flag == null) {
			flag = new HashMap<Integer, Boolean>();
		}
		binding.list.setAdapter(new YWGQJAdapter(currentActivity, dlist, flag));
		if (!taskStatus) {
			binding.list.setOnItemClickListener((parent, view, position, id) -> {
                // TODO Auto-generated method stub
                CheckBox ck = (CheckBox) view.findViewById(R.id.check);
                ck.toggle();
                flag.put(position, ck.isChecked());

            });
		}

	}

	@Override
	protected void releaseResAndSaveData() {
		// TODO Auto-generated method stub
		getGqjcheck().put(currentTaskId, flag);
	}

}
