package com.cnksi.inspe.ui;

import android.text.TextUtils;
import android.widget.Toast;

import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeDocBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TeamRuleEntity;

import java.util.List;

/**
 * 标准文档页面
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/4/3 12:23
 */
public class InspeDocActivity extends AppBaseActivity {

    private ActivityInspeDocBinding dataBinding;
    private InspecteTaskEntity task;
    private List<TeamRuleEntity> doclist;
    private List<TeamRuleEntity> itemList;
    private String role2Id;

    private TeamService teamService = new TeamService();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_doc;
    }

    @Override
    public void initUI() {
        setTitle("检查标准", R.drawable.inspe_left_black_24dp);
        dataBinding = (ActivityInspeDocBinding) rootDataBinding;

    }

    @Override
    public void initData() {
        role2Id = getIntent().getStringExtra("role_id");
        task = (InspecteTaskEntity) getIntent().getSerializableExtra("task");
        if (TextUtils.isEmpty(role2Id) || task == null) {
            Toast.makeText(this, "参数错误!", Toast.LENGTH_SHORT).show();
            finish();
        }
        //文档标准
        doclist = teamService.getRoleDoc(role2Id);
        if (doclist != null && doclist.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, size = doclist.size(); i < size; i++) {
                sb.append(doclist.get(i).getName()).append("\n\n");
            }
//            if (sb.length() > 4) {
            dataBinding.contextTxt.setText(sb.toString());//内容``
//            }


        }


        dataBinding.docTxt.setText(" －－《" + teamService.getRoleDocName(role2Id) + "》 ");//文档
    }
}
