
package android.databinding;
import com.cnksi.sjjc.BR;
class DataBinderMapper {
    final static int TARGET_MIN_SDK = 18;
    public DataBinderMapper() {
    }
    public android.databinding.ViewDataBinding getDataBinder(android.databinding.DataBindingComponent bindingComponent, android.view.View view, int layoutId) {
        switch(layoutId) {
                case com.cnksi.sjjc.R.layout.permission_dialog_tips:
                    return com.cnksi.sjjc.databinding.PermissionBinding.bind(view, bindingComponent);
                case com.cnksi.sjjc.R.layout.dialog_copy_tips:
                    return com.cnksi.sjjc.TipLayout.bind(view, bindingComponent);
                case com.cnksi.sjjc.R.layout.text_view_layout:
                    return com.cnksi.sjjc.databinding.PeopeNameBinding.bind(view, bindingComponent);
                case com.cnksi.sjjc.R.layout.dialog_add_person:
                    return com.cnksi.sjjc.databinding.DialogPeople.bind(view, bindingComponent);
                case com.cnksi.sjjc.R.layout.name_show_layout:
                    return com.cnksi.sjjc.databinding.ShowNameBinding.bind(view, bindingComponent);
                case com.cnksi.sjjc.R.layout.activity_hwcw:
                    return com.cnksi.sjjc.databinding.HwcwBinding.bind(view, bindingComponent);
        }
        return null;
    }
    android.databinding.ViewDataBinding getDataBinder(android.databinding.DataBindingComponent bindingComponent, android.view.View[] views, int layoutId) {
        switch(layoutId) {
        }
        return null;
    }
    int getLayoutId(String tag) {
        if (tag == null) {
            return 0;
        }
        final int code = tag.hashCode();
        switch(code) {
            case 1889492117: {
                if(tag.equals("layout/permission_dialog_tips_0")) {
                    return com.cnksi.sjjc.R.layout.permission_dialog_tips;
                }
                break;
            }
            case 87905153: {
                if(tag.equals("layout/dialog_copy_tips_0")) {
                    return com.cnksi.sjjc.R.layout.dialog_copy_tips;
                }
                break;
            }
            case 1731284008: {
                if(tag.equals("layout/text_view_layout_0")) {
                    return com.cnksi.sjjc.R.layout.text_view_layout;
                }
                break;
            }
            case -2033425706: {
                if(tag.equals("layout/dialog_add_person_0")) {
                    return com.cnksi.sjjc.R.layout.dialog_add_person;
                }
                break;
            }
            case -1850538066: {
                if(tag.equals("layout/name_show_layout_0")) {
                    return com.cnksi.sjjc.R.layout.name_show_layout;
                }
                break;
            }
            case 300754687: {
                if(tag.equals("layout/activity_hwcw_0")) {
                    return com.cnksi.sjjc.R.layout.activity_hwcw;
                }
                break;
            }
        }
        return 0;
    }
    String convertBrIdToString(int id) {
        if (id < 0 || id >= InnerBrLookup.sKeys.length) {
            return null;
        }
        return InnerBrLookup.sKeys[id];
    }
    private static class InnerBrLookup {
        static String[] sKeys = new String[]{
            "_all"
            ,"hwcw"
            ,"isBhpcw"
            ,"report"};
    }
}