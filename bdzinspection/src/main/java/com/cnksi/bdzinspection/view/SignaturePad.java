package com.cnksi.bdzinspection.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.cnksi.bdzinspection.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SignaturePad extends View {
    //View state
    private List<TimedPoint> mPoints;
    private boolean mIsEmpty;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastVelocity;
    private float mLastWidth;
    private RectF mDirtyRect;

    private final SvgBuilder mSvgBuilder = new SvgBuilder();

    // Cache
    private List<TimedPoint> mPointsCache = new ArrayList<>();
    private ControlTimedPoints mControlTimedPointsCached = new ControlTimedPoints();
    private Bezier mBezierCached = new Bezier();

    //Configurable parameters
    private int mMinWidth;
    private int mMaxWidth;
    private float mVelocityFilterWeight;
    private OnSignedListener mOnSignedListener;
    private boolean mClearOnDoubleClick;

    //Click values
    private long mFirstClick;
    private int mCountClick;
    private static final int DOUBLE_CLICK_DELAY_MS = 200;

    //Default attribute values
    private final int DEFAULT_ATTR_PEN_MIN_WIDTH_PX = 4;
    private final int DEFAULT_ATTR_PEN_MAX_WIDTH_PX = 6;
    private final int DEFAULT_ATTR_PEN_COLOR = Color.BLACK;
    private final float DEFAULT_ATTR_VELOCITY_FILTER_WEIGHT = 0.9f;
    private final boolean DEFAULT_ATTR_CLEAR_ON_DOUBLE_CLICK = false;

    private Paint mPaint = new Paint();
    private Bitmap mSignatureBitmap = null;
    private Canvas mSignatureBitmapCanvas = null;

    public SignaturePad(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.XS_SignaturePad,
                0, 0);

        //Configurable parameters
        try {
            mMinWidth = a.getDimensionPixelSize(R.styleable.XS_SignaturePad_penMinWidth, convertDpToPx(DEFAULT_ATTR_PEN_MIN_WIDTH_PX));
            mMaxWidth = a.getDimensionPixelSize(R.styleable.XS_SignaturePad_penMaxWidth, convertDpToPx(DEFAULT_ATTR_PEN_MAX_WIDTH_PX));
            mPaint.setColor(a.getColor(R.styleable.XS_SignaturePad_penColor, DEFAULT_ATTR_PEN_COLOR));
            mVelocityFilterWeight = a.getFloat(R.styleable.XS_SignaturePad_velocityFilterWeight, DEFAULT_ATTR_VELOCITY_FILTER_WEIGHT);
            mClearOnDoubleClick = a.getBoolean(R.styleable.XS_SignaturePad_clearOnDoubleClick, DEFAULT_ATTR_CLEAR_ON_DOUBLE_CLICK);
        } finally {
            a.recycle();
        }

        //Fixed parameters
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        //Dirty rectangle to update only the changed portion of the view
        mDirtyRect = new RectF();

        clear();
    }

    /**
     * Set the pen color from a given resource.
     * If the resource is not found, {@link Color#BLACK} is assumed.
     *
     * @param colorRes the color resource.
     */
    public void setPenColorRes(int colorRes) {
        try {
            setPenColor(getResources().getColor(colorRes));
        } catch (Resources.NotFoundException ex) {
            setPenColor(Color.parseColor("#000000"));
        }
    }

    /**
     * Set the pen color from a given color.
     *
     * @param color the color.
     */
    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * Set the minimum width of the stroke in pixel.
     *
     * @param minWidth the width in dp.
     */
    public void setMinWidth(float minWidth) {
        mMinWidth = convertDpToPx(minWidth);
    }

    /**
     * Set the maximum width of the stroke in pixel.
     *
     * @param maxWidth the width in dp.
     */
    public void setMaxWidth(float maxWidth) {
        mMaxWidth = convertDpToPx(maxWidth);
    }

    /**
     * Set the velocity filter weight.
     *
     * @param velocityFilterWeight the weight.
     */
    public void setVelocityFilterWeight(float velocityFilterWeight) {
        mVelocityFilterWeight = velocityFilterWeight;
    }

    public void clear() {
        mSvgBuilder.clear();
        mPoints = new ArrayList<>();
        mLastVelocity = 0;
        mLastWidth = (mMinWidth + mMaxWidth) / 2;

        if (mSignatureBitmap != null) {
            mSignatureBitmap = null;
            ensureSignatureBitmap();
        }

        setIsEmpty(true);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mPoints.clear();
                if (isDoubleClick()) {
                    break;
                }
                mLastTouchX = eventX;
                mLastTouchY = eventY;
                addPoint(getNewPoint(eventX, eventY));
                if(mOnSignedListener != null) {
                    mOnSignedListener.onStartSigning();
                }

            case MotionEvent.ACTION_MOVE:
                resetDirtyRect(eventX, eventY);
                addPoint(getNewPoint(eventX, eventY));
                break;

            case MotionEvent.ACTION_UP:
                resetDirtyRect(eventX, eventY);
                addPoint(getNewPoint(eventX, eventY));
                getParent().requestDisallowInterceptTouchEvent(true);
                setIsEmpty(false);
                break;

            default:
                return false;
        }

        //invalidate();
        invalidate(
                (int) (mDirtyRect.left - mMaxWidth),
                (int) (mDirtyRect.top - mMaxWidth),
                (int) (mDirtyRect.right + mMaxWidth),
                (int) (mDirtyRect.bottom + mMaxWidth));

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSignatureBitmap != null) {
            canvas.drawBitmap(mSignatureBitmap, 0, 0, mPaint);
        }
    }

    public void setOnSignedListener(OnSignedListener listener) {
        mOnSignedListener = listener;
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }

    public String getSignatureSvg() {
        int width = getTransparentSignatureBitmap().getWidth();
        int height = getTransparentSignatureBitmap().getHeight();
        return mSvgBuilder.build(width, height);
    }

    public Bitmap getSignatureBitmap() {
        Bitmap originalBitmap = getTransparentSignatureBitmap();
        Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        return whiteBgBitmap;
    }

    public void setSignatureBitmap(final Bitmap signature) {
        // View was laid out...
        if (isLaidOut(this)) {
            clear();
            ensureSignatureBitmap();

            RectF tempSrc = new RectF();
            RectF tempDst = new RectF();

            int dWidth = signature.getWidth();
            int dHeight = signature.getHeight();
            int vWidth = getWidth();
            int vHeight = getHeight();

            // Generate the required transform.
            tempSrc.set(0, 0, dWidth, dHeight);
            tempDst.set(0, 0, vWidth, vHeight);

            Matrix drawMatrix = new Matrix();
            drawMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);

            Canvas canvas = new Canvas(mSignatureBitmap);
            canvas.drawBitmap(signature, drawMatrix, null);
            setIsEmpty(false);
            invalidate();
        }
        // View not laid out yet e.g. called from onCreate(), onRestoreInstanceState()...
        else {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Remove layout listener...
                   removeOnGlobalLayoutListener(getViewTreeObserver(), this);

                    // Signature bitmap...
                    setSignatureBitmap(signature);
                }
            });
        }
    }

    public Bitmap getTransparentSignatureBitmap() {
        ensureSignatureBitmap();
        return mSignatureBitmap;
    }

    public Bitmap getTransparentSignatureBitmap(boolean trimBlankSpace) {

        if (!trimBlankSpace) {
            return getTransparentSignatureBitmap();
        }

        ensureSignatureBitmap();

        int imgHeight = mSignatureBitmap.getHeight();
        int imgWidth = mSignatureBitmap.getWidth();

        int backgroundColor = Color.TRANSPARENT;

        int xMin = Integer.MAX_VALUE,
            xMax = Integer.MIN_VALUE,
            yMin = Integer.MAX_VALUE,
            yMax = Integer.MIN_VALUE;

        boolean foundPixel = false;

        // Find xMin
        for (int x = 0; x < imgWidth; x++) {
            boolean stop = false;
            for (int y = 0; y < imgHeight; y++) {
                if (mSignatureBitmap.getPixel(x, y) != backgroundColor) {
                    xMin = x;
                    stop = true;
                    foundPixel = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }

        // Image is empty...
        if (!foundPixel) {
            return null;
        }

        // Find yMin
        for (int y = 0; y < imgHeight; y++) {
            boolean stop = false;
            for (int x = xMin; x < imgWidth; x++) {
                if (mSignatureBitmap.getPixel(x, y) != backgroundColor) {
                    yMin = y;
                    stop = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }

        // Find xMax
        for (int x = imgWidth - 1; x >= xMin; x--) {
            boolean stop = false;
            for (int y = yMin; y < imgHeight; y++) {
                if (mSignatureBitmap.getPixel(x, y) != backgroundColor) {
                    xMax = x;
                    stop = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }

        // Find yMax
        for (int y = imgHeight - 1; y >= yMin; y--) {
            boolean stop = false;
            for (int x = xMin; x <= xMax; x++) {
                if (mSignatureBitmap.getPixel(x, y) != backgroundColor) {
                    yMax = y;
                    stop = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }

      return Bitmap.createBitmap(mSignatureBitmap, xMin, yMin, xMax - xMin, yMax - yMin);
    }

    private boolean isDoubleClick() {
        if (mClearOnDoubleClick) {
            if (mFirstClick != 0 && System.currentTimeMillis() - mFirstClick > DOUBLE_CLICK_DELAY_MS) {
                mCountClick = 0;
            }
            mCountClick++;
            if (mCountClick == 1) {
                mFirstClick = System.currentTimeMillis();
            } else if (mCountClick == 2) {
                long lastClick = System.currentTimeMillis();
                if (lastClick - mFirstClick < DOUBLE_CLICK_DELAY_MS) {
                    this.clear();
                    return true;
                }
            }
        }
        return false;
    }

    private TimedPoint getNewPoint(float x, float y) {
        int mCacheSize = mPointsCache.size();
        TimedPoint timedPoint;
        if (mCacheSize == 0) {
            // Cache is empty, create a new point
            timedPoint = new TimedPoint();
        } else {
            // Get point from cache
            timedPoint = mPointsCache.remove(mCacheSize-1);
        }

        return timedPoint.set(x, y);
    }

    private void recyclePoint(TimedPoint point) {
        mPointsCache.add(point);
    }

    private void addPoint(TimedPoint newPoint) {
        mPoints.add(newPoint);

        int pointsCount = mPoints.size();
        if (pointsCount > 3) {

            ControlTimedPoints tmp = calculateCurveControlPoints(mPoints.get(0), mPoints.get(1), mPoints.get(2));
            TimedPoint c2 = tmp.c2;
            recyclePoint(tmp.c1);

            tmp = calculateCurveControlPoints(mPoints.get(1), mPoints.get(2), mPoints.get(3));
            TimedPoint c3 = tmp.c1;
            recyclePoint(tmp.c2);

            Bezier curve = mBezierCached.set(mPoints.get(1), c2, c3, mPoints.get(2));

            TimedPoint startPoint = curve.startPoint;
            TimedPoint endPoint = curve.endPoint;

            float velocity = endPoint.velocityFrom(startPoint);
            velocity = Float.isNaN(velocity) ? 0.0f : velocity;

            velocity = mVelocityFilterWeight * velocity
                    + (1 - mVelocityFilterWeight) * mLastVelocity;

            // The new width is a function of the velocity. Higher velocities
            // correspond to thinner strokes.
            float newWidth = strokeWidth(velocity);

            // The Bezier's width starts out as last curve's final width, and
            // gradually changes to the stroke width just calculated. The new
            // width calculation is based on the velocity between the Bezier's
            // start and end mPoints.
            addBezier(curve, mLastWidth, newWidth);

            mLastVelocity = velocity;
            mLastWidth = newWidth;

            // Remove the first element from the list,
            // so that we always have no more than 4 mPoints in mPoints array.
            recyclePoint(mPoints.remove(0));

            recyclePoint(c2);
            recyclePoint(c3);
            
        } else if (pointsCount == 1) {
            // To reduce the initial lag make it work with 3 mPoints
            // by duplicating the first point
            TimedPoint firstPoint = mPoints.get(0);
            mPoints.add(getNewPoint(firstPoint.x, firstPoint.y));
        }
    }

    private void addBezier(Bezier curve, float startWidth, float endWidth) {
        mSvgBuilder.append(curve, (startWidth + endWidth) / 2);
        ensureSignatureBitmap();
        float originalWidth = mPaint.getStrokeWidth();
        float widthDelta = endWidth - startWidth;
        float drawSteps = (float) Math.floor(curve.length());

        for (int i = 0; i < drawSteps; i++) {
            // Calculate the Bezier (x, y) coordinate for this step.
            float t = ((float) i) / drawSteps;
            float tt = t * t;
            float ttt = tt * t;
            float u = 1 - t;
            float uu = u * u;
            float uuu = uu * u;

            float x = uuu * curve.startPoint.x;
            x += 3 * uu * t * curve.control1.x;
            x += 3 * u * tt * curve.control2.x;
            x += ttt * curve.endPoint.x;

            float y = uuu * curve.startPoint.y;
            y += 3 * uu * t * curve.control1.y;
            y += 3 * u * tt * curve.control2.y;
            y += ttt * curve.endPoint.y;

            // Set the incremental stroke width and draw.
            mPaint.setStrokeWidth(startWidth + ttt * widthDelta);
            mSignatureBitmapCanvas.drawPoint(x, y, mPaint);
            expandDirtyRect(x, y);
        }

        mPaint.setStrokeWidth(originalWidth);
    }

    private ControlTimedPoints calculateCurveControlPoints(TimedPoint s1, TimedPoint s2, TimedPoint s3) {
        float dx1 = s1.x - s2.x;
        float dy1 = s1.y - s2.y;
        float dx2 = s2.x - s3.x;
        float dy2 = s2.y - s3.y;

        float m1X = (s1.x + s2.x) / 2.0f;
        float m1Y = (s1.y + s2.y) / 2.0f;
        float m2X = (s2.x + s3.x) / 2.0f;
        float m2Y = (s2.y + s3.y) / 2.0f;

        float l1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
        float l2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);

        float dxm = (m1X - m2X);
        float dym = (m1Y - m2Y);
        float k = l2 / (l1 + l2);
        if (Float.isNaN(k)) {
            k = 0.0f;
        }
        float cmX = m2X + dxm * k;
        float cmY = m2Y + dym * k;

        float tx = s2.x - cmX;
        float ty = s2.y - cmY;

        return mControlTimedPointsCached.set(getNewPoint(m1X + tx, m1Y + ty), getNewPoint(m2X + tx, m2Y + ty));
    }

    private float strokeWidth(float velocity) {
        return Math.max(mMaxWidth / (velocity + 1), mMinWidth);
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * mPoints.
     *
     * @param historicalX the previous x coordinate.
     * @param historicalY the previous y coordinate.
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < mDirtyRect.left) {
            mDirtyRect.left = historicalX;
        } else if (historicalX > mDirtyRect.right) {
            mDirtyRect.right = historicalX;
        }
        if (historicalY < mDirtyRect.top) {
            mDirtyRect.top = historicalY;
        } else if (historicalY > mDirtyRect.bottom) {
            mDirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     *
     * @param eventX the event x coordinate.
     * @param eventY the event y coordinate.
     */
    private void resetDirtyRect(float eventX, float eventY) {

        // The mLastTouchX and mLastTouchY were set when the ACTION_DOWN motion event occurred.
        mDirtyRect.left = Math.min(mLastTouchX, eventX);
        mDirtyRect.right = Math.max(mLastTouchX, eventX);
        mDirtyRect.top = Math.min(mLastTouchY, eventY);
        mDirtyRect.bottom = Math.max(mLastTouchY, eventY);
    }

    private void setIsEmpty(boolean newValue) {
        mIsEmpty = newValue;
        if (mOnSignedListener != null) {
            if (mIsEmpty) {
                mOnSignedListener.onClear();
            } else {
                mOnSignedListener.onSigned();
            }
        }
    }

    private void ensureSignatureBitmap() {
        if (mSignatureBitmap == null) {
            mSignatureBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            mSignatureBitmapCanvas = new Canvas(mSignatureBitmap);
        }
    }

    private int convertDpToPx(float dp){
      return Math.round(getContext().getResources().getDisplayMetrics().density * dp);
//        return Math.round(AutoUtils.getPercentHeight1px() * dp);
    }

    public interface OnSignedListener {
        void onStartSigning();
        void onSigned();
        void onClear();
    }

    public  void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener victim) {
        // Future (API16+)...
        if (Build.VERSION.SDK_INT >= 16) {
            observer.removeOnGlobalLayoutListener(victim);
        }
        // Legacy...
        else {
            observer.removeGlobalOnLayoutListener(victim);
        }
    }
    public  boolean isLaidOut(View view) {
        // Future (API19+)...
        if (Build.VERSION.SDK_INT >= 19) {
            return view.isLaidOut();
        }
        // Legacy...
        return view.getWidth() > 0 && view.getHeight() > 0;
    }


    public void saveBitmapToPNG( File photo) throws IOException {
        Bitmap bitmap=getTransparentSignatureBitmap(false);
        Log.i("Bitmap",bitmap.getWidth()+":"+bitmap.getHeight());
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Log.i("Bitmap",newBitmap.getWidth()+":"+newBitmap.getHeight());
        Log.i("Bitmap",newBitmap.getByteCount()+"");
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
//        newBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Log.i("Bitmap",newBitmap.getWidth()+":"+newBitmap.getHeight());
        stream.close();
    }

    /**
     * 三次贝塞尔曲线工具类
     */
    public class Bezier {

        public TimedPoint startPoint;
        public TimedPoint control1;
        public TimedPoint control2;
        public TimedPoint endPoint;

        public Bezier set(TimedPoint startPoint, TimedPoint control1,
                          TimedPoint control2, TimedPoint endPoint) {
            this.startPoint = startPoint;
            this.control1 = control1;
            this.control2 = control2;
            this.endPoint = endPoint;
            return this;
        }

        /**
         * 获得贝塞尔曲线的长度
         * @return
         */
        public float length() {
            int steps = 10;
            float length = 0;
            double cx, cy, px = 0, py = 0, xDiff, yDiff;

            for (int i = 0; i <= steps; i++) {
                float t = (float) i / steps;
                cx = point(t, this.startPoint.x, this.control1.x,
                        this.control2.x, this.endPoint.x);
                cy = point(t, this.startPoint.y, this.control1.y,
                        this.control2.y, this.endPoint.y);
                if (i > 0) {
                    xDiff = cx - px;
                    yDiff = cy - py;
                    length += Math.sqrt(xDiff * xDiff + yDiff * yDiff);
                }
                px = cx;
                py = cy;
            }
            return length;

        }

        /**
         * 求分段的贝塞尔曲线长度。
         * //P(t)=p1(1-t)^3+3p2(1-t)^2t+3p3(1-t)t^2+p4t^3；
         * @param t
         * @param start
         * @param c1
         * @param c2
         * @param end
         * @return
         */

        public double point(float t, float start, float c1, float c2, float end) {
            return start * (1.0 - t) * (1.0 - t) * (1.0 - t)
                    + 3.0 * c1 * (1.0 - t) * (1.0 - t) * t
                    + 3.0 * c2 * (1.0 - t) * t * t
                    + end * t * t * t;
        }

    }

    /**
     * Represent a point as it would be in the generated SVG document.
     */
    class SvgPoint {

        final Integer x, y;

        public SvgPoint(TimedPoint point) {
            // one optimisation is to get rid of decimals as they are mostly non-significant in the
            // produced SVG image
            x = Math.round(point.x);
            y = Math.round(point.y);
        }

        public SvgPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * SvgPoint转换成绝对坐标
         * @return
         */
        public String toAbsoluteCoordinates() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(x);
            if (y >= 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(y);
            return stringBuilder.toString();
        }
        /**
         * SvgPoint转换成相对坐标
         * @return
         */
        public String toRelativeCoordinates(final SvgPoint referencePoint) {
            return (new SvgPoint(x - referencePoint.x, y - referencePoint.y)).toString();
        }

        @Override
        public String toString() {
            return toAbsoluteCoordinates();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SvgPoint svgPoint = (SvgPoint) o;

            if (!x.equals(svgPoint.x)) {
                return false;
            }
            return y.equals(svgPoint.y);

        }

        @Override
        public int hashCode() {
            int result = x.hashCode();
            result = 31 * result + y.hashCode();
            return result;
        }
    }

    /**
     * 控制点类 三次贝塞尔曲线需要两个控制点
     */
    public class ControlTimedPoints {

        public TimedPoint c1;
        public TimedPoint c2;

        public ControlTimedPoints set(TimedPoint c1, TimedPoint c2) {
            this.c1 = c1;
            this.c2 = c2;
            return this;
        }

    }

    /**
     * Svg 路径构造类
     */
    public class SvgPathBuilder {

        public  final Character SVG_RELATIVE_CUBIC_BEZIER_CURVE = 'c';
        public  final Character SVG_MOVE = 'M';
        private final StringBuilder mStringBuilder;
        private final Integer mStrokeWidth;
        private final SvgPoint mStartPoint;
        private SvgPoint mLastPoint;
        private Character mLastSvgCommand;

        public SvgPathBuilder(final SvgPoint startPoint, final Integer strokeWidth) {
            mStrokeWidth = strokeWidth;
            mStartPoint = startPoint;
            mLastPoint = startPoint;
            mLastSvgCommand = null;
            mStringBuilder = new StringBuilder();
        }

        public final Integer getStrokeWidth() {
            return mStrokeWidth;
        }

        public final SvgPoint getLastPoint() {
            return mLastPoint;
        }

        public SvgPathBuilder append(final SvgPoint controlPoint1, final SvgPoint controlPoint2, final SvgPoint endPoint) {
            mStringBuilder.append(makeRelativeCubicBezierCurve(controlPoint1, controlPoint2, endPoint));
            mLastSvgCommand = SVG_RELATIVE_CUBIC_BEZIER_CURVE;
            mLastPoint = endPoint;
            return this;
        }

        @Override
        public String toString() {
            return (new StringBuilder())
                    .append("<path ")
                    .append("stroke-width=\"")
                    .append(mStrokeWidth)
                    .append("\" ")
                    .append("d=\"")
                    .append(SVG_MOVE)
                    .append(mStartPoint)
                    .append(mStringBuilder)
                    .append("\"/>")
                    .toString();
        }

        private String makeRelativeCubicBezierCurve(final SvgPoint controlPoint1, final SvgPoint controlPoint2, final SvgPoint endPoint) {
            final String sControlPoint1 = controlPoint1.toRelativeCoordinates(mLastPoint);
            final String sControlPoint2 = controlPoint2.toRelativeCoordinates(mLastPoint);
            final String sEndPoint = endPoint.toRelativeCoordinates(mLastPoint);

            final StringBuilder sb = new StringBuilder();
            if (!SVG_RELATIVE_CUBIC_BEZIER_CURVE.equals(mLastSvgCommand)) {
                sb.append(SVG_RELATIVE_CUBIC_BEZIER_CURVE);
                sb.append(sControlPoint1);
            } else {
                if (!sControlPoint1.startsWith("-")) {
                    sb.append(" ");
                }
                sb.append(sControlPoint1);
            }

            if (!sControlPoint2.startsWith("-")) {
                sb.append(" ");
            }
            sb.append(sControlPoint2);

            if (!sEndPoint.startsWith("-")) {
                sb.append(" ");
            }
            sb.append(sEndPoint);

            // discard zero curve
            final String svg = sb.toString();
            if ("c0 0 0 0 0 0".equals(svg)) {
                return "";
            } else {
                return svg;
            }
        }
    }

    /**
     * 输出Svg 图像
     */
    public class SvgBuilder {
        private final StringBuilder mSvgPathsBuilder = new StringBuilder();
        private SvgPathBuilder mCurrentPathBuilder = null;

        public SvgBuilder() {
        }

        public void clear() {
            mSvgPathsBuilder.setLength(0);
            mCurrentPathBuilder = null;
        }

        /**
         * 输出给定宽高的Svg
         * @param width
         * @param height
         * @return
         */
        public String build(final int width, final int height) {
            if (isPathStarted()) {
                appendCurrentPath();
            }
            return (new StringBuilder())
                    .append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n")
                    .append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.2\" baseProfile=\"tiny\" ")
                    .append("height=\"")
                    .append(height)
                    .append("\" ")
                    .append("width=\"")
                    .append(width)
                    .append("\">")
                    .append("<g ")
                    .append("stroke-linejoin=\"round\" ")
                    .append("stroke-linecap=\"round\" ")
                    .append("fill=\"none\" ")
                    .append("stroke=\"black\"")
                    .append(">")
                    .append(mSvgPathsBuilder)
                    .append("</g>")
                    .append("</svg>")
                    .toString();
        }

        /**
         * 将贝塞尔曲线记录到当前路径
         * @param curve
         * @param strokeWidth
         * @return
         */
        public SvgBuilder append(final Bezier curve, final float strokeWidth) {
            final Integer roundedStrokeWidth = Math.round(strokeWidth);
            final SvgPoint curveStartSvgPoint = new SvgPoint(curve.startPoint);
            final SvgPoint curveControlSvgPoint1 = new SvgPoint(curve.control1);
            final SvgPoint curveControlSvgPoint2 = new SvgPoint(curve.control2);
            final SvgPoint curveEndSvgPoint = new SvgPoint(curve.endPoint);

            if (!isPathStarted()) {
                startNewPath(roundedStrokeWidth, curveStartSvgPoint);
            }

            if (!curveStartSvgPoint.equals(mCurrentPathBuilder.getLastPoint())
                    || !roundedStrokeWidth.equals(mCurrentPathBuilder.getStrokeWidth())) {
                appendCurrentPath();
                startNewPath(roundedStrokeWidth, curveStartSvgPoint);
            }

            mCurrentPathBuilder.append(curveControlSvgPoint1, curveControlSvgPoint2, curveEndSvgPoint);
            return this;
        }

        /**
         * 从一个新的点开始记录
         * @param roundedStrokeWidth
         * @param curveStartSvgPoint
         */
        private void startNewPath(Integer roundedStrokeWidth, SvgPoint curveStartSvgPoint) {
            mCurrentPathBuilder = new SvgPathBuilder(curveStartSvgPoint, roundedStrokeWidth);
        }

        /**
         * 在当前路劲上追加一段
         */
        private void appendCurrentPath() {
            mSvgPathsBuilder.append(mCurrentPathBuilder);
        }

        /**
         * 是否开启新的路径
         * @return
         */
        private boolean isPathStarted() {
            return mCurrentPathBuilder != null;
        }

    }

    /**
     * 时间点
     */
    public class TimedPoint {
        public float x;
        public float y;
        public long timestamp;

        public TimedPoint set(float x, float y) {
            this.x = x;
            this.y = y;
            this.timestamp = System.currentTimeMillis();
            return this;
        }

        /**
         * 计算两个点之间的速度
         * @param start
         * @return
         */
        public float velocityFrom(TimedPoint start) {
            float velocity = distanceTo(start) / (this.timestamp - start.timestamp);
            return velocity;
        }

        /**
         * 计算两个点之间的距离
         * @param point
         * @return
         */
        public float distanceTo(TimedPoint point) {
            return (float) Math.sqrt(Math.pow(point.x - this.x, 2) + Math.pow(point.y - this.y, 2));
        }
    }
}
