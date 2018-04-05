package com.zhy.core;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.core.attr.AutoAttr;
import com.zhy.core.attr.MinHeightAttr;

import java.util.ArrayList;
import java.util.List;

public class AutoLayoutInfo
{
    private List<AutoAttr> autoAttrs = new ArrayList<>();

    public void addAttr(AutoAttr autoAttr)
    {
        autoAttrs.add(autoAttr);
    }


    public void fillAttrs(View view)
    {
        for (AutoAttr autoAttr : autoAttrs)
        {
            autoAttr.apply(view);
        }
    }


    public static AutoLayoutInfo getAttrFromView(View view, int attrs, int base)
    {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) return null;
        AutoLayoutInfo autoLayoutInfo = new AutoLayoutInfo();

        // width & height
        if ((attrs & com.zhy.core.attr.Attrs.WIDTH) != 0 && params.width > 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.WidthAttr.generate(params.width, base));
        }

        if ((attrs & com.zhy.core.attr.Attrs.HEIGHT) != 0 && params.height > 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.HeightAttr.generate(params.height, base));
        }

        //margin
        if (params instanceof ViewGroup.MarginLayoutParams)
        {
            if ((attrs & com.zhy.core.attr.Attrs.MARGIN) != 0)
            {
                autoLayoutInfo.addAttr(com.zhy.core.attr.MarginLeftAttr.generate(((ViewGroup.MarginLayoutParams) params).leftMargin, base));
                autoLayoutInfo.addAttr(com.zhy.core.attr.MarginTopAttr.generate(((ViewGroup.MarginLayoutParams) params).topMargin, base));
                autoLayoutInfo.addAttr(com.zhy.core.attr.MarginRightAttr.generate(((ViewGroup.MarginLayoutParams) params).rightMargin, base));
                autoLayoutInfo.addAttr(com.zhy.core.attr.MarginBottomAttr.generate(((ViewGroup.MarginLayoutParams) params).bottomMargin, base));
            }
            if ((attrs & com.zhy.core.attr.Attrs.MARGIN_LEFT) != 0)
            {
                autoLayoutInfo.addAttr(com.zhy.core.attr.MarginLeftAttr.generate(((ViewGroup.MarginLayoutParams) params).leftMargin, base));
            }
            if ((attrs & com.zhy.core.attr.Attrs.MARGIN_TOP) != 0)
            {
                autoLayoutInfo.addAttr(com.zhy.core.attr.MarginTopAttr.generate(((ViewGroup.MarginLayoutParams) params).topMargin, base));
            }
            if ((attrs & com.zhy.core.attr.Attrs.MARGIN_RIGHT) != 0)
            {
                autoLayoutInfo.addAttr(com.zhy.core.attr.MarginRightAttr.generate(((ViewGroup.MarginLayoutParams) params).rightMargin, base));
            }
            if ((attrs & com.zhy.core.attr.Attrs.MARGIN_BOTTOM) != 0)
            {
                autoLayoutInfo.addAttr(com.zhy.core.attr.MarginBottomAttr.generate(((ViewGroup.MarginLayoutParams) params).bottomMargin, base));
            }
        }

        //padding
        if ((attrs & com.zhy.core.attr.Attrs.PADDING) != 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.PaddingLeftAttr.generate(view.getPaddingLeft(), base));
            autoLayoutInfo.addAttr(com.zhy.core.attr.PaddingTopAttr.generate(view.getPaddingTop(), base));
            autoLayoutInfo.addAttr(com.zhy.core.attr.PaddingRightAttr.generate(view.getPaddingRight(), base));
            autoLayoutInfo.addAttr(com.zhy.core.attr.PaddingBottomAttr.generate(view.getPaddingBottom(), base));
        }
        if ((attrs & com.zhy.core.attr.Attrs.PADDING_LEFT) != 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.MarginLeftAttr.generate(view.getPaddingLeft(), base));
        }
        if ((attrs & com.zhy.core.attr.Attrs.PADDING_TOP) != 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.MarginTopAttr.generate(view.getPaddingTop(), base));
        }
        if ((attrs & com.zhy.core.attr.Attrs.PADDING_RIGHT) != 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.MarginRightAttr.generate(view.getPaddingRight(), base));
        }
        if ((attrs & com.zhy.core.attr.Attrs.PADDING_BOTTOM) != 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.MarginBottomAttr.generate(view.getPaddingBottom(), base));
        }

        //minWidth ,maxWidth , minHeight , maxHeight
        if ((attrs & com.zhy.core.attr.Attrs.MIN_WIDTH) != 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.MinWidthAttr.generate(com.zhy.core.attr.MinWidthAttr.getMinWidth(view), base));
        }
        if ((attrs & com.zhy.core.attr.Attrs.MAX_WIDTH) != 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.MaxWidthAttr.generate(com.zhy.core.attr.MaxWidthAttr.getMaxWidth(view), base));
        }
        if ((attrs & com.zhy.core.attr.Attrs.MIN_HEIGHT) != 0)
        {
            autoLayoutInfo.addAttr(MinHeightAttr.generate(MinHeightAttr.getMinHeight(view), base));
        }
        if ((attrs & com.zhy.core.attr.Attrs.MAX_HEIGHT) != 0)
        {
            autoLayoutInfo.addAttr(com.zhy.core.attr.MaxHeightAttr.generate(com.zhy.core.attr.MaxHeightAttr.getMaxHeight(view), base));
        }

        //textsize

        if (view instanceof TextView)
        {
            if ((attrs & com.zhy.core.attr.Attrs.TEXTSIZE) != 0)
            {
                autoLayoutInfo.addAttr(com.zhy.core.attr.TextSizeAttr.generate((int) ((TextView) view).getTextSize(), base));
            }
        }
        return autoLayoutInfo;
    }


    @Override
    public String toString()
    {
        return "AutoLayoutInfo{" +
                "autoAttrs=" + autoAttrs +
                '}';
    }
}