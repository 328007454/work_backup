package com.cnksi.inspe.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.cnksi.inspe.BuildConfig;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.base.AppBaseFragment;
import com.cnksi.inspe.databinding.ActivityInspeMainBinding;
import com.cnksi.inspe.ui.fragment.AllIssueFragment;
import com.cnksi.inspe.ui.fragment.InspectionFragment;
import com.cnksi.inspe.ui.fragment.MyIssueFragment;


/**
 * 检查任务模块首页
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 13:41
 */
//@Route(path = "/inspe/main")
public class InspeMainActivity extends AppBaseActivity {

    ActivityInspeMainBinding dataBinding;
    //    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            int i = item.getItemId();
//            if (i == R.id.navigation_home) {
//                if (dataBinding.viewpager.getCurrentItem() != 0)
////                    dataBinding.viewpager.setCurrentItem(0);
////                    mTextMessage.setText(R.string.title_home);
//                    return true;
//            } else if (i == R.id.navigation_dashboard) {
//                if (dataBinding.viewpager.getCurrentItem() != 1)
////                    dataBinding.viewpager.setCurrentItem(1);
////                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//            } else if (i == R.id.navigation_notifications) {
//                if (dataBinding.viewpager.getCurrentItem() != 2)
////                    dataBinding.viewpager.setCurrentItem(2);
////                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
//            return false;
//        }
//    };
    private FragmentPagerAdapter mSectionsPagerAdapter;
    private AppBaseFragment[] fragments;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_main;
    }

    @Override
    public void initUI() {
        setTitle("精益化检查", R.drawable.inspe_left_black_24dp);

        dataBinding = (ActivityInspeMainBinding) rootDataBinding;
        fragments = new AppBaseFragment[3];

        //用户ID
        String[] userIds = getIntent().getStringArrayExtra("userid_array");
        //用户名(pms系统账号是唯一的)
        String[] userNames = getIntent().getStringArrayExtra("username_array");

        if ((userIds == null || userIds.length == 0) && (userNames == null || userNames.length == 0)) {
            showToast("参数错误！");
            finish();
            return;
        } else {
            if (userIds != null) {
                getUserService().initIds(userIds);
            } else {
                getUserService().initNames(userNames);
            }

        }

        //测试输出
        if (BuildConfig.DEBUG) {
            try {
                showToast("我是:" + getUserService().getUser1().getAccount() + ",权限:" + getUserService().getUser1().getRoleType().name());
            } catch (Exception e) {
                showToast("角色错误！");
                finish();
                return;
            }
        }


//        dataBinding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        dataBinding.viewpager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.inspe_line_size1));
        dataBinding.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(dataBinding.tabs));
        dataBinding.tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(dataBinding.viewpager));
        dataBinding.viewpager.setOffscreenPageLimit(3);//将缓存设置最大，否则在快速滑动时会导致卡顿(Skipped 98 frames!  The application may be doing too much work on its main thread.)

        mSectionsPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public Fragment getItem(int position) {

                return createMainFragment(position);
            }
        };


        dataBinding.viewpager.setAdapter(mSectionsPagerAdapter);
        dataBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                switch (position) {
//                    case 0:
//                        dataBinding.navigation.setSelectedItemId(R.id.navigation_home);
//                        break;
//                    case 1:
//                        dataBinding.navigation.setSelectedItemId(R.id.navigation_dashboard);
//                        break;
//                    case 2:
//                        dataBinding.navigation.setSelectedItemId(R.id.navigation_notifications);
//                        break;
//                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void initData() {

//        DateDialog dateDialog = new DateDialog(InspeMainActivity.this);
//        dateDialog.setDate(2010, 10, 10);
//        dateDialog.setOnDialogListener(new DateDialog.OnDialogListener() {
//            @Override
//            public void onDateChanged(long date, int year, int month, int day) {
//                Log.e(tag, year + "-" + month + "," + day);
//                Toast.makeText(InspeMainActivity.this, (year + "-" + month + "," + day), Toast.LENGTH_SHORT).show();
//            }
//        });
//        dateDialog.show();


    }


    /**
     * 创建Fragment
     *
     * @param postion
     * @return
     * @throws NullPointerException postion 超过2会抛异常
     */
    private AppBaseFragment createMainFragment(int postion) {
        if (null == fragments[postion]) {
            Log.e(tag, "createMainFragment(int " + postion + ")");
            switch (postion) {
                case 0:
                    fragments[postion] = new InspectionFragment();
                    break;
                case 1:
                    fragments[postion] = new MyIssueFragment();
                    break;
                case 2:
                    fragments[postion] = new AllIssueFragment();
                    break;
                default:
                    throw new NullPointerException("你创建的Fragment不在设计内,fragments=" + fragments.length + ",positon=" + postion);
            }
        }

        return fragments[postion];
    }

}
