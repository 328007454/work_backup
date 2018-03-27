package com.cnksi.inspe.ui;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeTeamissueBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.entity.InspeScoreEntity;
import com.cnksi.inspe.widget.DateDialog;

import java.util.List;

/**
 * 班组建设检查-根据标准检查相关事项，并填写相关检查结果或扣分等
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:30
 */
public class InspeTeamIssueActivity extends AppBaseActivity implements View.OnClickListener {

    private ActivityInspeTeamissueBinding dataBinding;

    private TeamRuleEntity teamRule;
    private float maxMinus;
    private List<InspeScoreEntity> list;
    private TeamRuleResultEntity teamRuleResult = new TeamRuleResultEntity();
    private TeamService teamService = new TeamService();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_teamissue;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspeTeamissueBinding) rootDataBinding;
        dataBinding.addBtn.setOnClickListener(this);
        dataBinding.minuxBtn.setOnClickListener(this);
        dataBinding.dateBtn.setOnClickListener(this);
        dataBinding.dateEdit.setOnClickListener(this);
        dataBinding.cameraBtn.setOnClickListener(this);
        dataBinding.issueInfoTxt.setOnClickListener(this);
        dataBinding.okBtn.setOnClickListener(this);
    }

    @Override
    public void initData() {
        teamRule = (TeamRuleEntity) getIntent().getSerializableExtra("data");
        maxMinus = getIntent().getFloatExtra("max_minus", 0);
        if (teamRule == null) {
            showToast("参数错误!");
            finish();
        }
        list = JSON.parseArray(teamRule.getStand_content(), InspeScoreEntity.class);

        //
        dataBinding.contextTxt.setText(teamRule.getName());
        dataBinding.minusScoreEdit.setText(Float.toString(0));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.dateBtn || i == R.id.dateEdit) {
            DateDialog dateDialog = new DateDialog(this);
            dateDialog.setOnDialogListener(new DateDialog.OnDialogListener() {
                @Override
                public void onDateChanged(long date, int year, int month, int day) {
                    dataBinding.dateEdit.setText(year + "年" + month + "月" + day + "日");
                }
            });
            dateDialog.show();


        } else if (i == R.id.issueInfoBtn || i == R.id.issueInfoTxt) {
        } else if (i == R.id.okBtn) {
            if (teamService.saveRuleResult(teamRuleResult)) {
                showToast("操作成功");
                finish();
            } else {
                showToast("保存数据库失败！");
            }


        } else if (i == R.id.cameraBtn) {
        } else if (i == R.id.addBtn) {
        } else if (i == R.id.minuxBtn) {
        }
    }
}
