package com.cnksi.bdzinspection.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseRecyclerDataBindingAdapter;
import com.cnksi.bdzinspection.adapter.PersonAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.DepartmentService;
import com.cnksi.bdzinspection.databinding.XsRecyclerviewBinding;
import com.cnksi.bdzinspection.model.Users;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkk on 2017/9/25.
 */

public class PersonListUtils implements BaseRecyclerDataBindingAdapter.OnItemClickListener {
    private PopupWindow popupWindow;
    private SelectPersonListener itemClickListener;
    private Activity context;
    private List<Users> selectUsers = new ArrayList<>();
    private String currentUsers;

    public void setPersonRatioListener(SelectPersonListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void disMissPopWindow() {
        if (null != popupWindow)
            popupWindow.dismiss();
    }

    private int height;

    public void setHeight(int height) {
        this.height = height;
        if (popupWindow != null) {
            popupWindow.setHeight(height - 270);
        }
    }

    public void setCurrentUser(String currentUsers) {
        this.currentUsers = currentUsers;
        selectUsers.clear();
        if (!TextUtils.isEmpty(currentUsers)) {
            String[] users = currentUsers.split(",");
            for (int i = 0; i < users.length; i++) {
                for (Users user : mUserses) {
                    if (user.username.equalsIgnoreCase(users[i])) {
                        selectUsers.add(user);
                    }
                }
            }
        }
        personAdapter.setClickObject(selectUsers);
    }

    public interface SelectPersonListener {
        void getSelectPersons(List<Users> userses);
    }


    @Override
    public void onAdapterItemClick(View view, Object data, int position) {
        Users users = (Users) data;
        if (selectUsers.contains(users)) {
            selectUsers.remove(users);
        } else {
            selectUsers.add(users);
        }
        personAdapter.setClickObject(selectUsers);
    }


    private PersonListUtils() {

    }


    private static class PersonInner {

        static PersonListUtils personListUtils = new PersonListUtils();
    }

    public static PersonListUtils getInsance() {
        return PersonInner.personListUtils;
    }

    /**
     * @param context 上下文
     * 初始化popwindow
     */
    private PersonAdapter personAdapter;
    private XsRecyclerviewBinding XsRecyclerviewBinding;

    public PersonListUtils initPopWindow(Activity context) {
        this.context = context;
        if (popupWindow == null) {
            popupWindow = new PopupWindow(context);
            XsRecyclerviewBinding = XsRecyclerviewBinding.inflate(context.getLayoutInflater());
            personAdapter = new PersonAdapter(XsRecyclerviewBinding.rvPersonList, currentInputPersons, R.layout.xs_item_device_param_type);
            personAdapter.setOnItemClickListener(this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            XsRecyclerviewBinding.rvPersonList.setLayoutManager(layoutManager);
            XsRecyclerviewBinding.rvPersonList.setAdapter(personAdapter);
            popupWindow.setWidth(400);
            Drawable drawable = context.getApplicationContext().getDrawable(R.drawable.xs_item);
            popupWindow.setBackgroundDrawable(drawable);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setContentView(XsRecyclerviewBinding.getRoot());
            XsRecyclerviewBinding.imgSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.getSelectPersons(selectUsers);
                    }
                }
            });
        }
        return this;
    }

    private List<Users> mUserses;

    /**
     * @param currentDept 所属部门ID
     *                    查询当前部门所有的成员
     */
    public void initPersonData(final String currentDept) {
        XunshiApplication.getFixedThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mUserses = DepartmentService.getInstance().getAllUsers(currentDept);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            personAdapter.setList(mUserses);
                        }
                    });
                } catch (DbException e) {
                    mUserses = new ArrayList<Users>();
                    e.printStackTrace();
                }
                if (mUserses == null) {
                    mUserses = new ArrayList<Users>();
                }
            }
        });
    }

    /**
     * @param person 输入名字
     * 筛选所有成员的姓名包含输入文字的人员
     */
    private List<Users> currentInputPersons = new ArrayList<>();

    public void checkInputPerson(String person) {
        List<Users> mUsers = new ArrayList<>();
        if (mUserses.isEmpty())
            return;
        for (Users user : mUserses) {
            if (user.username.contains(person)) {
                mUsers.add(user);
            }
        }
        if (!mUsers.isEmpty()) {
            currentInputPersons.clear();
            currentInputPersons.addAll(mUsers);
            personAdapter.setList(currentInputPersons);
        }
    }

    private int[] positions;

    public void showPopWindow(View widget) {
        if (positions == null) {
            positions = DisplayUtil.getInstance().calculatePopWindowPos(widget, XsRecyclerviewBinding.getRoot());
        }
        popupWindow.showAtLocation(widget, Gravity.TOP | Gravity.RIGHT, 0, 100);
    }
}
