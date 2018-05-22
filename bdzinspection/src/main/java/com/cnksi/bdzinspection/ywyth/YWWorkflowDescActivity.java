package com.cnksi.bdzinspection.ywyth;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.ImageDetailsActivity;
import com.cnksi.bdzinspection.activity.TitleActivity;
import com.cnksi.bdzinspection.adapter.AudioAdapter;
import com.cnksi.bdzinspection.adapter.ImageAdapter;
import com.cnksi.bdzinspection.databinding.XsActivityYwythDescBinding;
import com.cnksi.bdzinspection.model.PlanProcessStatus;
import com.cnksi.bdzinspection.model.Process;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.ywyth.adapter.VideoAdapter;
import com.cnksi.xscore.xsutils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年3月29日 下午5:55:54 TODO
 */
public class YWWorkflowDescActivity extends TitleActivity {

	private PlanProcessStatus bean;
	private Process pro;
	private List<String> imgs, videos, audios;
	private XsActivityYwythDescBinding binding;
	@Override
	protected int setLayout() {
		return R.layout.xs_activity_ywyth_desc;
	}

	@Override
	protected String initialUI() {
		binding = (XsActivityYwythDescBinding) getDataBinding();
		bean = (PlanProcessStatus) getIntent().getSerializableExtra(YWWorkflowActivity.PLANDATA);
		pro = (Process) getIntent().getSerializableExtra(YWWorkflowActivity.PROCESSDATA);
		binding.announcements.setText(StringUtils.isEmpty(pro.mayoccur) ? "无" : pro.mayoccur);
		return pro.content;
	}

	@Override
	protected void initialData() {
		if (bean.picture != null) {
			imgs = Arrays.asList(bean.picture.split(Config.SPLIT));
		} else {
			imgs = new ArrayList<String>();
		}
		binding.horizonListview.setAdapter(new ImageAdapter(currentActivity, imgs));
		binding.horizonListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(currentActivity, ImageDetailsActivity.class);
				ArrayList<String> _t = new ArrayList<String>();
				for (String string : imgs) {
					_t.add(Config.CUSTOMER_PICTURES_FOLDER + string);
				}
				intent.putStringArrayListExtra(Config.IMAGEURL_LIST, _t);
				intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, false);
				intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
				startActivityForResult(intent, Config.ACTION_SELECT_IMAGE);
			}
		});
		if (bean.audio != null) {
			audios = Arrays.asList(bean.audio.split(Config.SPLIT));
		} else {
			audios = new ArrayList<String>();
		}
		binding.audioList.setAdapter(new AudioAdapter(currentActivity, audios));
		if (bean.video != null) {
			videos = Arrays.asList(bean.video.split(Config.SPLIT));
		} else {
			videos = new ArrayList<String>();
		}
		binding.videolist.setAdapter(new VideoAdapter(currentActivity, videos));
	}

	@Override
	protected void releaseResAndSaveData() {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case Config.ACTION_SELECT_IMAGE:

				List<String> mList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
				break;

			default:
				break;
			}
		}
	}

}
