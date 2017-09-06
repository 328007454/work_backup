package com.cnksi.core.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapUtil {

    /**
     * ALPHA_8就是Alpha由8位组成 ARGB_4444就是由4个4位组成即16位， ARGB_8888就是由4个8位组成即32位， RGB_565就是R为5位，G为6位，B为5位共16位
     * <p/>
     * 由此可见： ALPHA_8 代表8位Alpha位图 ARGB_4444 代表16位ARGB位图 ARGB_8888 代表32位ARGB位图 RGB_565 代表8位RGB位图
     * <p/>
     * 位图位数越高代表其可以存储的颜色信息越多，当然图像也就越逼真。
     */
    private static int DEFAULT_QUALITY = 80;
    private static final int START_QUALITY = 30;

    /**
     * <pre>
     * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处：
     *  1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度， 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *  2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
     * </pre>
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.RGB_565;
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        return getImageThumbnail(bitmap, width, height);
    }

    /**
     * 根据宽获得等比例压缩的缩略图
     *
     * @param imagePath
     * @param width
     * @return
     */
    public static Bitmap getImageThumbnailByWidth(String imagePath, int width) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int be = w / width;
        int height = (int) (h * ((float) width / (float) w));
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        return getImageThumbnail(bitmap, width, height);
    }

    /**
     * 根据高获取等比例压缩的缩略图
     *
     * @param imagePath
     * @param height
     * @return
     */
    public static Bitmap getImageThumbnailByHeight(String imagePath, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int be = h / height;
        int width = (int) (w * ((float) height / (float) h));
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        return getImageThumbnail(bitmap, width, height);
    }

    /**
     * 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getImageThumbnail(Bitmap bitmap, int width, int height) {
        return ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    /**
     * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, Thumbnails.MICRO_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 判断调用系统相机 照相返回的照片 是否需要旋转
     *
     * @param imagePath 图片绝对路径
     * @return 旋转后的图片
     */
    public static Bitmap postRotateBitmap(String imagePath) {
        return postRotateBitmap(imagePath, false);
    }

    /**
     * 判断调用系统相机 照相返回的照片 是否需要旋转
     *
     * @param imagePath        图片绝对路径
     * @param isHandPostRotate 是否需要手动旋转
     * @return 旋转后的图片
     */
    public static Bitmap postRotateBitmap(String imagePath, boolean isHandPostRotate) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.RGB_565;
        options.inJustDecodeBounds = true;
        Bitmap bitmapResult = null;
        int degree = readPictureDegree(imagePath);
        if (degree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(degree);
            options.inJustDecodeBounds = false;
            Bitmap bitmapTemp = BitmapFactory.decodeFile(imagePath, options);
            bitmapResult = Bitmap.createBitmap(bitmapTemp, 0, 0, options.outWidth, options.outHeight, m, true);
        } else if (options.outWidth > options.outHeight && isHandPostRotate) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(90);
            options.inJustDecodeBounds = false;
            Bitmap bitmapTemp = BitmapFactory.decodeFile(imagePath, options);
            bitmapResult = Bitmap.createBitmap(bitmapTemp, 0, 0, options.outWidth, options.outHeight, m, true);
        } else {
            options.inJustDecodeBounds = false;
            bitmapResult = BitmapFactory.decodeFile(imagePath, options);
        }
        return bitmapResult;
    }

    /**
     * 获取图片的高度
     *
     * @param context 上下文
     * @param resId   资源id
     * @return 图片的高度
     */
    public static int getBitmapHeight(Context context, int resId) {
        // 2.获取图片的宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        int height = options.outHeight;
        options.inJustDecodeBounds = false;
        return height;
    }

    /**
     * 获取图片的高度
     *
     * @param filePath 图片绝对路径
     * @return 图片的高度
     */
    public static int getBitmapHeight(String filePath) {
        // 获取图片的宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int height = options.outHeight;
        options.inJustDecodeBounds = false;
        return height;
    }

    /**
     * 获取图片的宽度
     *
     * @param context 上下文
     * @param resId   资源id
     * @return 图片的宽度
     */
    public static int getBitmapWidth(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        int width = options.outWidth;
        options.inJustDecodeBounds = false;
        return width;
    }

    /**
     * 获取图片的宽度
     *
     * @param filePath 图片绝对路径
     * @return 图片的宽度
     */
    public static int getBitmapWidth(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int width = options.outWidth;
        options.inJustDecodeBounds = false;
        return width;
    }

    /**
     * 获取图片的方向
     *
     * @param imgpath 图片路径
     * @return 图片的角度
     */
    public static int readPictureDegree(String imgpath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgpath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        }
        return degree;
    }

    /**
     * 根据新高度缩放图片
     *
     * @param srcBitmap 源图片
     * @param newHeight 新高度
     * @return 缩放后的图片
     */
    public static Bitmap createScaledBitmapByHeight(Bitmap srcBitmap, int newHeight) {
        int srcHeight = srcBitmap.getHeight();
        float scaleHeight = ((float) newHeight) / srcHeight;
        float scaleWidth = scaleHeight;
        return createScaledBitmap(srcBitmap, scaleWidth, scaleHeight);
    }

    /**
     * 根据新高度缩放图片
     *
     * @param imagePath 源图片 路径
     * @param newHeight 新高度
     * @return 缩放后的图片
     */
    public static Bitmap createScaledBitmapByHeight(String imagePath, int newHeight) {
        // 获取这个图片的宽和高，注意此处的bitmap为null
        Bitmap srcBitmap = BitmapFactory.decodeFile(imagePath, new BitmapFactory.Options());
        return srcBitmap == null ? srcBitmap : createScaledBitmapByHeight(srcBitmap, newHeight);
    }

    /**
     * 根据新宽度缩放图片
     *
     * @param srcBitmap 源图片
     * @param newWidth  新宽度
     * @return 缩放后的图片
     */
    public static Bitmap createScaledBitmapByWidth(Bitmap srcBitmap, int newWidth) {
        int srcWidth = srcBitmap.getWidth();
        float scaleWidth = ((float) newWidth) / srcWidth;
        float scaleHeight = scaleWidth;
        return createScaledBitmap(srcBitmap, scaleWidth, scaleHeight);
    }

    /**
     * 根据新宽度缩放图片
     *
     * @param imagePath 源图片 路径
     * @param newWidth  新宽度
     * @return 缩放后的图片
     */
    public static Bitmap createScaledBitmapByWidth(String imagePath, int newWidth) {
        Bitmap srcBitmap = BitmapFactory.decodeFile(imagePath, new BitmapFactory.Options());
        return srcBitmap == null ? srcBitmap : createScaledBitmapByWidth(srcBitmap, newWidth);
    }

    /**
     * 使用长宽缩放比缩放
     *
     * @param bitmap      源图片
     * @param scaleWidth  源图片的缩放率 如 0.8f 就是 源图片宽度的0.8倍
     * @param scaleHeight 源图片的缩放率 如 0.8f 就是 源图片高度的0.8倍
     * @return 缩放后的图片
     */
    private static Bitmap createScaledBitmap(Bitmap bitmap, float scaleWidth, float scaleHeight) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight, matrix, true);
        return resizedBitmap;
    }

    /**
     * 将图片缩放到指定大小
     *
     * @param srcBitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap createScaledBitmap(Bitmap srcBitmap, int newWidth, int newHeight) {
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / srcWidth;
        float scaleHeight = ((float) newHeight) / srcHeight;
        return createScaledBitmap(srcBitmap, scaleWidth, scaleHeight);
    }

    /**
     * 使用长宽缩放比缩放
     *
     * @param imagePath 源图片
     * @param newWidth
     * @param newHeight
     * @return 缩放后的图片
     */
    public static Bitmap createScaledBitmap(String imagePath, int newWidth, int newHeight) {
        Bitmap srcBitmap = BitmapFactory.decodeFile(imagePath, new BitmapFactory.Options());
        return createScaledBitmap(srcBitmap, newWidth, newHeight);
    }

    /**
     * 添加阴影
     *
     * @param originalBitmap
     */
    public static Bitmap drawImageDropShadow(Context context, Bitmap originalBitmap) {
        BlurMaskFilter blurFilter = new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL);
        Paint shadowPaint = new Paint();
        shadowPaint.setAlpha(50);
        shadowPaint.setColor(context.getResources().getColor(android.R.color.white));
        shadowPaint.setMaskFilter(blurFilter);
        int[] offsetXY = new int[2];
        Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint, offsetXY);
        Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage32);
        c.drawBitmap(originalBitmap, offsetXY[0], offsetXY[1], null);
        return shadowImage32;
    }

    /**
     * 得到圆角图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        return getRoundedCornerBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int width, int height) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /***
     * 创建图片的倒影图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectedImage(Bitmap bitmap) {
        // 图片与倒影间隔距离
        final int reflectionGap = 4;
        // 图片的宽度
        int width = bitmap.getWidth();
        // 图片的高度
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        // 图片缩放，x轴变为原来的1倍，y轴为-1倍,实现图片的反转
        matrix.preScale(1, -1);
        // 创建反转后的图片Bitmap对象，图片高是原图的一半。
        Bitmap reflectionBitmap = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
        // 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍。
        Bitmap withReflectionBitmap = Bitmap.createBitmap(width, (height + height / 2 + reflectionGap), Config.ARGB_8888);
        // 构造函数传入Bitmap对象，为了在图片上画图
        Canvas canvas = new Canvas(withReflectionBitmap);
        // 画原始图片
        canvas.drawBitmap(bitmap, 0, 0, null);
        // 画间隔矩形
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // 画倒影图片
        canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);
        // 实现倒影效果
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, withReflectionBitmap.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // 覆盖效果
        canvas.drawRect(0, height, width, withReflectionBitmap.getHeight(), paint);
        return withReflectionBitmap;
    }

    /**
     * 将控件保存为图片
     *
     * @param view     空间对象
     * @param filePath 保存文件的文件夹路径 如/sdCard/pictures/
     * @return 文件名称
     * @throws NullPointerException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String saveViewToBitmap(View view, String filePath) throws NullPointerException, FileNotFoundException, IOException {
        view.setDrawingCacheEnabled(true);
        String fileName = "";
        Bitmap bitmap = view.getDrawingCache();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_QUALITY, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
            fileName = System.currentTimeMillis() + CoreConfig.IMAGE_JPG_POSTFIX;
            if (!filePath.endsWith(File.separator)) {
                filePath += File.separator;
            }
            File file = new File(filePath + fileName);
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
            outputStream.close();
        }
        view.setDrawingCacheEnabled(false);
        return fileName;
    }

    /**
     * 从Assets中读取图片
     *
     * @param context  上下文
     * @param fileName 图片文件名称
     * @return Bitmap
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 将Bitmap保存为图片
     *
     * @param bitmap   bitmap对象
     * @param filePath 保存的文件路径，如：/sdcard/namecard/123.jpg
     */
    public static void saveBitmap(Bitmap bitmap, String filePath) {
        saveBitmap(bitmap, filePath, DEFAULT_QUALITY);
    }

    /**
     * 保存图片
     *
     * @param bitmap
     * @param filePath
     * @param quality
     */
    public static void saveBitmap(Bitmap bitmap, String filePath, int quality) {
        if (bitmap == null) return;
        if (!(0 < quality && quality <= 100)) quality = 100;

        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图片
     *
     * @param bm
     * @param folder
     * @param picName
     */
    public static void saveBitmap(Bitmap bm, String folder, String picName) {
        saveBitmap(bm, folder + File.separator + picName);
    }

    /**
     * 保存图片
     *
     * @param bm
     * @param folder
     * @param fileName
     * @param quality
     */
    public static void saveBitmap(Bitmap bm, String folder, String fileName, int quality) {
        saveBitmap(bm, folder + File.separator + fileName, quality);
    }

    /**
     * 图片压缩 - 质量压缩方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap compressImage(Bitmap bitmap, int quality) {
        if (bitmap == null) return null;
        if (!(0 < quality && quality <= 100)) quality = 100;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        int options = quality;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            // 每次都减少10
            options -= 10;
            if (options <= 0) break;

            // 重置baos即清空baos
            baos.reset();
            // 这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap resultBitmap = BitmapFactory.decodeStream(isBm, null, null);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return resultBitmap;
    }

    public static Bitmap compressImage(String pathName) {
        return compressImage(BitmapFactory.decodeFile(pathName), DEFAULT_QUALITY);
    }

    // ------------------------------ 优化后方法 ------------------------------

    public static Bitmap getOptimizedCompressImage(String pathName) {
        Bitmap srcBitmap = getOptimizedBitmap(pathName);
        return getOptimizedCompressImage(srcBitmap);
    }

    public static Bitmap getOptimizedCompressImage(String pathName, int startQuality) {
        Bitmap srcBitmap = getOptimizedBitmap(pathName);
        return compressImage(srcBitmap, startQuality);
    }

    /**
     * 图片压缩 - 质量压缩方法
     *
     * @param pathName
     * @param startQuality
     * @param refWidth
     * @param refHeight
     * @return
     */
    public static Bitmap getOptimizedCompressImage(String pathName, int startQuality, int refWidth, int refHeight) {
        Bitmap srcBitmap = getOptimizedBitmap(pathName, refWidth, refHeight);
        return compressImage(srcBitmap, startQuality);
    }

    public static Bitmap getOptimizedCompressImage(Bitmap bitmap) {
        return compressImage(bitmap, START_QUALITY);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * 获取优化后的图片
     *
     * @param pathName
     */
    public static Bitmap getOptimizedBitmap(String pathName) {
        // 以主流手机480x800的尺寸作为参考
        return getOptimizedBitmap(pathName, 480, 800);
    }

    /**
     * 获取优化后的图片
     *
     * @param pathName
     */
    public static Bitmap getOptimizedBitmap(String pathName, int refWidth, int refHeight) {
        if (TextUtils.isDigitsOnly(pathName)) return null;
        if (!(new File(pathName)).exists()) return null;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, opts);

        opts.inJustDecodeBounds = false;
        // RGB_565不支持透明度，ARGB_4444质量损失较明显（API level
        // 13开始已不推荐使用），默认的ARGB_8888会多用一倍内存
        // opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inSampleSize = calcOptimizedInSampleSize(opts, refWidth, refHeight);

        return BitmapFactory.decodeFile(pathName, opts);
    }

    /**
     * 参考给定宽高计算优化的inSampleSize值
     *
     * @param opts
     * @param refWidth
     * @param refHeight
     * @return
     */
    public static int calcOptimizedInSampleSize(BitmapFactory.Options opts, int refWidth, int refHeight) {
        if (opts == null) return 1;
        if (!(refWidth > 0 && refHeight > 0)) return 1;

        // ---------- 以竖屏情境计算（宽 < 高） ----------
        int width = opts.outWidth;
        int height = opts.outHeight;
        if (width > height) {
            height = opts.outWidth;
            width = opts.outHeight;
        }

        int rWidth = refWidth;
        int rHeight = refHeight;
        if (rWidth > rHeight) {
            rHeight = refWidth;
            rWidth = refHeight;
        }

        int widthRatio = Math.round((float) width / rWidth);
        int heightRatio = Math.round((float) height / rHeight);
        int optimizedInSampleSize = Math.max(widthRatio, heightRatio);
        if (optimizedInSampleSize < 1) return 1;

        return optimizedInSampleSize;
    }

    /**
     * 旋转图片
     *
     * @param imagePath 原图片
     * @param degree    旋转角度
     * @return
     */
    public static Bitmap rotateBitmap(String imagePath, int degree) {
        if (FileUtils.isFileExists(imagePath)) {
            return rotateBitmap(BitmapFactory.decodeFile(imagePath), degree);
        }
        return null;
    }

    /**
     * 旋转图片
     *
     * @param bitmap 原图片
     * @param degree 旋转角度
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(degree);
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        return resultBitmap;
    }

    /**
     * 保存手写笔记
     *
     * @return
     */
    public static boolean saveEditPicture(View view, String picturePath, int quality) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
            try {
                File file = new File(picturePath);
                if (file.exists()) {
                    file.delete();
                }
                OutputStream outputStream = new FileOutputStream(file);
                outputStream.write(buffer);
                outputStream.close();
                bos.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                view.setDrawingCacheEnabled(false);
            }
        }
        return false;
    }

    /**
     * 横向拼接两张图片
     *
     * @param first
     * @param second
     * @return
     */
    public static Bitmap addHorizontal2Bitmap(Bitmap first, Bitmap second) {
        int width = first.getWidth() + second.getWidth();
        int height = first.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, first.getWidth(), 0, null);
        return result;
    }

    /**
     * 纵向拼接两张图片
     *
     * @param first
     * @param second
     * @return
     */
    public static Bitmap addVertical2Bitmap(Bitmap first, Bitmap second) {
        int height = first.getHeight() + second.getHeight();
        int width = first.getWidth();
        Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, 0, first.getHeight(), null);
        return result;
    }

    /**
     * 在图片上添加水印
     *
     * @param bitmap  源图片
     * @param content 内容
     * @return
     */
    public static Bitmap addText2Bitmap(Bitmap bitmap, String content) {
        return addText2Bitmap(bitmap, content, 30);
    }

    /**
     * 在图片上添加水印
     *
     * @param bitmap   源图片
     * @param content  内容
     * @param textSize 字体大小
     * @return
     */
    public static Bitmap addText2Bitmap(Bitmap bitmap, String content, int textSize) {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255, 0, 0));
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        int x = bitmap.getWidth() / 20;
        int y = bitmap.getHeight() - bitmap.getHeight() / 20;
        canvas.drawText(content, x, y, paint);
        return bitmap;
    }

    /**
     * get the orientation of the bitmap {@link android.media.ExifInterface}
     *
     * @param path
     * @return
     */
    public final static int getDegress(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 压缩一张存在SDCard上的图片 到指定的高宽
     *
     * @param srcPath   图片绝对路径
     * @param reqWidth  压缩后的宽度
     * @param reqHeight 压缩后的高度
     * @return
     */
    public static void compressImage(String srcPath, int reqWidth, int reqHeight) {
        if (!TextUtils.isEmpty(srcPath) && FileUtils.isFileExists(srcPath)) {
            int scale = 1;
            // 首先不加载图片,仅获取图片尺寸
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
            options.inJustDecodeBounds = true;
            // 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
            BitmapFactory.decodeFile(srcPath, options);
            options.inPreferredConfig = Config.RGB_565;
            // 计算缩放率
            scale = calculateInSampleSize(options, reqWidth, reqHeight);
            // 计算压缩比例,如inSampleSize=4时,图片会压缩成原图的1/4
            options.inSampleSize = scale;
            // 当inJustDecodeBounds设为false时,BitmapFactory.decode...就会返回图片对象了
            options.inJustDecodeBounds = false;

            Bitmap bitmap = null;
            try {
                // 利用计算的比例值获取压缩后的图片对象
                bitmap = BitmapFactory.decodeFile(srcPath, options);
            } catch (Exception e) {
                options.inSampleSize = calculateInSampleSize(options, 500, 500);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(srcPath, options);
            }
            // 保存图片
            saveBitmap(bitmap, srcPath);
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    /**
     * 压缩一张存在SDCard上的图片 到指定的高宽
     *
     * @param srcPath 图片绝对路径
     * @param scale   压缩率
     * @param length  大小大于多少才压缩
     */
    public static void compressImage(String srcPath, int scale, long length) {
        if (!TextUtils.isEmpty(srcPath) && FileUtils.isFileExists(srcPath) && (new File(srcPath)).length() > length) {
            // 首先不加载图片,仅获取图片尺寸
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Config.RGB_565;
            // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
            options.inJustDecodeBounds = true;
            // 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
            BitmapFactory.decodeFile(srcPath, options);
            // 计算压缩比例,如inSampleSize=4时,图片会压缩成原图的1/4
            options.inSampleSize = scale;
            // 当inJustDecodeBounds设为false时,BitmapFactory.decode...就会返回图片对象了
            options.inJustDecodeBounds = false;
            // 利用计算的比例值获取压缩后的图片对象
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
            // 保存图片
            saveBitmap(bitmap, srcPath);
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    /**
     * 压缩一张存在SDCard上的图片 到指定的高宽
     *
     * @param srcPath 图片绝对路径
     * @param scale   压缩率
     */
    public static void compressImage(String srcPath, int scale) {
        compressImage(srcPath, scale, 1024l * 1024l);
    }

    /**
     * 质量压缩方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap resultBitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return resultBitmap;
    }
}
