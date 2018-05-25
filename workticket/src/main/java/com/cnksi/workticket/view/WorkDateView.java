package com.cnksi.workticket.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.workticket.R;
import com.cnksi.workticket.bean.WorkTicketOrder;
import com.cnksi.workticket.db.WorkTicketOrderService;
import com.cnksi.workticket.enum_ticket.TicketTimeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Mr.K  on 2018/5/9.
 * @decrption 该类主要是展示工作预约票一周的工作安排时间展示
 */

public class WorkDateView extends View {
    private Float[] hDistance = new Float[]{216f, 101f};

    private Float[] vDistance = new Float[]{108f, 121f};
    private Bitmap bitmap;
    private int bitMapWidth, bitMapHeight;
    private List<String> weekStartDates = new ArrayList<>();
    private List<String> dates = new ArrayList<>();
    private String[][] positions = new String[5][7];

    /**
     * 本周工作预约量
     */
    private List<WorkTicketOrder> orders = new ArrayList<>();
    /**
     * 本周工作预约对应工作表中的位置
     */
    private HashMap<String, WorkTicketOrder> workTicketOrderHashMap = new HashMap<>();


    /**
     * 画笔
     */
    private Paint mPaint = new Paint();

    public WorkDateView(Context context) {
        this(context, null);
    }

    public WorkDateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WorkDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initWorkDatePosition();
    }

    private void initWorkDatePosition() {
        int hRow = 5;
        int vRow = 7;
        for (int i = 0; i < hRow; i++) {
            for (int f = 0; f < vRow; f++) {
                positions[i][f] = String.valueOf(i + 1) + String.valueOf(f + 1);
            }
        }

    }

    private void initPaint() {
        mPaint.setColor(0xffe1e4e4);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2f);
        mPaint.setTextSize(33);

        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ticket_date_ok);
        bitMapHeight = bitmap.getHeight();
        bitMapWidth = bitmap.getWidth();
        weekStartDates = com.cnksi.workticket.util.DateUtils.getWeekDay();
        for (String str : weekStartDates) {
            str = DateUtils.getFormatterTime(str, "yyyy-MM-dd 00:00:00");
            str = str.substring(0, 10) + " 23:59:59";
            String date = str.substring(8, 10);
            if (date.startsWith("0")) {
                date = date.replace("0", "");
            }
            dates.add(date);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.reset();
        mPaint.setColor(0xffe1e4e4);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2f);

        //水平线
        canvas.drawLine(0f, vDistance[0], 939f, vDistance[0], mPaint);
        canvas.drawLine(0f, vDistance[0] + vDistance[1] + 2, 939f, vDistance[0] + vDistance[1] + 2, mPaint);
        canvas.drawLine(0f, vDistance[0] + vDistance[1] * 2 + 4, 939f, vDistance[0] + vDistance[1] * 2 + 4, mPaint);
        canvas.drawLine(0f, vDistance[0] + vDistance[1] * 3 + 6, 939f, vDistance[0] + vDistance[1] * 3 + 6, mPaint);
        canvas.drawLine(0f, vDistance[0] + vDistance[1] * 4 + 8, 939f, vDistance[0] + vDistance[1] * 4 + 8, mPaint);
        //垂直线
        canvas.drawLine(hDistance[0], 0f, hDistance[0], 723, mPaint);
        canvas.drawLine(hDistance[0] + hDistance[1] + 2, 0f, hDistance[0] + hDistance[1] + 2, 723, mPaint);
        canvas.drawLine(hDistance[0] + hDistance[1] * 2 + 4, 0f, hDistance[0] + hDistance[1] * 2 + 4, 723, mPaint);
        canvas.drawLine(hDistance[0] + hDistance[1] * 3 + 6, 0f, hDistance[0] + hDistance[1] * 3 + 6, 723, mPaint);
        canvas.drawLine(hDistance[0] + hDistance[1] * 4 + 8, 0f, hDistance[0] + hDistance[1] * 4 + 8, 723, mPaint);
        canvas.drawLine(hDistance[0] + hDistance[1] * 5 + 10, 0f, hDistance[0] + hDistance[1] * 5 + 10, 723, mPaint);
        canvas.drawLine((939 - hDistance[1]), 0, (939 - hDistance[1]), 723, mPaint);
        canvas.drawLine(7, 7, hDistance[0], vDistance[0], mPaint);

        mPaint.reset();
        mPaint.setColor(0xff05C8B8);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2f);
        mPaint.setTextSize(33);

        canvas.drawText("日期", hDistance[0] - 90f, 54f, mPaint);
        canvas.drawText("时间段", 21, (vDistance[0] - 13f), mPaint);

        //时间区域
        canvas.drawText("10:00-11:00", 21, (vDistance[0] + 2 + vDistance[1] / 2 + 11.5f), mPaint);
        canvas.drawText("11:00-12:00", 21, (vDistance[0] + 4 + vDistance[1] * 1.5f + 11.5f), mPaint);
        canvas.drawText("14:00-15:00", 21, (vDistance[0] + 6 + vDistance[1] * 2.5f + 11.5f), mPaint);
        canvas.drawText("15:00-16:00", 21, (vDistance[0] + 8 + vDistance[1] * 3.5f + 11.5f), mPaint);
        canvas.drawText("16:00-17:00", 21, (vDistance[0] + 10 + vDistance[1] * 4.5f + 11.5f), mPaint);

        //日期本周七天的日期

        canvas.drawText(dates.get(0), hDistance[0] + 2 + hDistance[1] * 0.5f - 11.5f, vDistance[0] * 0.5f + 11.5f, mPaint);
        canvas.drawText(dates.get(1), hDistance[0] + 2 + hDistance[1] * 1.5f - 11.5f, vDistance[0] * 0.5f + 11.5f, mPaint);
        canvas.drawText(dates.get(2), hDistance[0] + 2 + hDistance[1] * 2.5f - 11.5f, vDistance[0] * 0.5f + 11.5f, mPaint);
        canvas.drawText(dates.get(3), hDistance[0] + 2 + hDistance[1] * 3.5f - 11.5f, vDistance[0] * 0.5f + 11.5f, mPaint);
        canvas.drawText(dates.get(4), hDistance[0] + 2 + hDistance[1] * 4.5f - 11.5f, vDistance[0] * 0.5f + 11.5f, mPaint);
        canvas.drawText(dates.get(5), hDistance[0] + 2 + hDistance[1] * 5.5f - 11.5f, vDistance[0] * 0.5f + 11.5f, mPaint);
        canvas.drawText(dates.get(6), hDistance[0] + 2 + hDistance[1] * 6.5f - 11.5f, vDistance[0] * 0.5f + 11.5f, mPaint);

        //本周工作时间安排
        for (int i = 0; i < positions.length; i++) {
            for (int f = 0; f < positions[i].length; f++) {
                if (workTicketOrderHashMap.get(positions[i][f]) != null) {
                    canvas.drawBitmap(bitmap, hDistance[0] + hDistance[1] * f + 2 * f + (hDistance[1] - bitMapWidth) * 0.5f, vDistance[0] + vDistance[1] * i + 2 * i + (vDistance[1] - bitMapHeight) * 0.5f, mPaint);
                }
            }
        }
    }


    /**
     * 获取本周日期下的工作，并且转化到对应的工作表的坐标位置
     *
     * @param activity context引用
     */
    public void getCurrentWeekDateWork(Activity activity) {
        workTicketOrderHashMap.clear();
        orders.clear();
        ExecutorManager.executeTaskSerially(() -> {
            orders = WorkTicketOrderService.getInstance().getWeekDateWork(weekStartDates.get(0), weekStartDates.get(6));
            for (WorkTicketOrder order : orders) {
                if (TextUtils.equals(order.workVal, TicketTimeEnum.region_10to11.value)) {
                    WorkDateView.this.dispalayTimeZoneWork(order, 0);

                } else if (TextUtils.equals(order.workVal, TicketTimeEnum.region_11to12.value)) {
                    WorkDateView.this.dispalayTimeZoneWork(order, 1);

                } else if (TextUtils.equals(order.workVal, TicketTimeEnum.region_14to15.value)) {
                    WorkDateView.this.dispalayTimeZoneWork(order, 2);

                } else if (TextUtils.equals(order.workVal, TicketTimeEnum.region_15to16.value)) {
                    WorkDateView.this.dispalayTimeZoneWork(order, 3);

                } else if (TextUtils.equals(order.workVal, TicketTimeEnum.region_16to17.value)) {
                    WorkDateView.this.dispalayTimeZoneWork(order, 4);
                }
            }

            activity.runOnUiThread(WorkDateView.this::invalidate);
        });

    }

    public void dispalayTimeZoneWork(WorkTicketOrder order, int m) {
        for (String date : weekStartDates) {
            date = date.substring(0, 10) + " 23:59:59";
            if (com.cnksi.workticket.util.DateUtils.get2TimeDifferenceMinutes(order.workDate, date) <= 1440) {
                date = date.substring(0, 10) + " 00:00:00";
                int p = weekStartDates.indexOf(date);
                workTicketOrderHashMap.put(positions[m][p], order);
            }
        }

    }


}
