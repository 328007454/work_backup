package com.cnksi.xscore.xsutils.imageloader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 专辑帮助类
 */
public class AlbumHelper {
	private static final String TAG = "AlbumHelper";
	private Context context;
	private ContentResolver cr;
	/**
	 * 是否创建了图片集
	 */
	private boolean hasBuildImagesBucketList = false;
	/**
	 * 缩略图列表
	 */
	private HashMap<String, String> thumbnailList = new HashMap<String, String>();
	private HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

	private static AlbumHelper instance;

	private AlbumHelper() {
	}

	public static AlbumHelper getHelper() {
		if (instance == null) {
			instance = new AlbumHelper();
		}
		return instance;
	}

	public HashMap<String, String> getThumbAlbumList() {
		return thumbnailList;
	}

	public HashMap<String, ImageBucket> getBucketList() {
		return bucketList;
	}

	public void setHasBuildImagesBucketList(boolean hasBuild) {
		this.hasBuildImagesBucketList = hasBuild;
	}

	/**
	 * 重置相册
	 */
	public void resetBucketList() {
		this.bucketList.clear();
		this.thumbnailList.clear();
		this.hasBuildImagesBucketList = false;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			cr = context.getContentResolver();
		}
	}

	/**
	 * 得到缩略图
	 */
	private void getThumbnail() {
		String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA };
		Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
		getThumbnailColumnData(cursor);
	}

	/**
	 * 从数据库中得到缩略图
	 * 
	 * @param cursor
	 */
	private void getThumbnailColumnData(Cursor cursor) {
		if (cursor.moveToFirst()) {
			int imageId;
			String imagePath;
			// int _idColumn = cur.getColumnIndex(Thumbnails._ID);
			int image_idColumn = cursor.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cursor.getColumnIndex(Thumbnails.DATA);
			do {
				imageId = cursor.getInt(image_idColumn);
				imagePath = cursor.getString(dataColumn);
				thumbnailList.put(String.valueOf(imageId), imagePath);
			} while (cursor.moveToNext());
		}
	}

	/**
	 * 得到图片集
	 */
	private void buildImagesBucketList() {
		long startTime = System.currentTimeMillis();

		// 构造缩略图索引
		getThumbnail();

		// 构造相册索引
		String columns[] = new String[] { Media._ID, Media.BUCKET_ID, Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE, Media.SIZE, Media.BUCKET_DISPLAY_NAME };
		// 得到一个游标
		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
		if (cur.moveToFirst()) {
			// 获取指定列的索引
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
			int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
			int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
			int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
			int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
			// 获取图片总数
			// int totalNum = cur.getCount();

			do {
				String _id = cur.getString(photoIDIndex);
				String name = cur.getString(photoNameIndex);
				String path = cur.getString(photoPathIndex);
				String title = cur.getString(photoTitleIndex);
				String size = cur.getString(photoSizeIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);
				String picasaId = cur.getString(picasaIdIndex);

				Log.i(TAG, _id + ", bucketId: " + bucketId + ", picasaId: " + picasaId + " name:" + name + " path:" + path + " title: " + title + " size: " + size + " bucket: " + bucketName + "---");

				ImageBucket bucket = bucketList.get(bucketId);
				if (bucket == null) {
					bucket = new ImageBucket();
					bucketList.put(bucketId, bucket);
					bucket.imageList = new ArrayList<ImageItem>();
					bucket.bucketName = bucketName;
					bucket.bucketPath = path.substring(0, path.lastIndexOf("/") + 1);
				}
				bucket.count++;
				ImageItem imageItem = new ImageItem();
				imageItem.imageId = _id;
				imageItem.imagePath = path;
				imageItem.thumbnailPath = thumbnailList.get(_id);
				bucket.imageList.add(imageItem);

			} while (cur.moveToNext());
		}

		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, ImageBucket> entry = (Entry<String, ImageBucket>) itr.next();
			ImageBucket bucket = entry.getValue();
			Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", " + bucket.count + " ---------- ");
			for (int i = 0; i < bucket.imageList.size(); ++i) {
				ImageItem image = bucket.imageList.get(i);
				Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath + ", " + image.thumbnailPath);
			}
		}
		hasBuildImagesBucketList = true;
		long endTime = System.currentTimeMillis();
		Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
	}

	/**
	 * 得到图片集
	 *
	 * @param refresh
	 * @return
	 */
	public List<ImageBucket> getImagesBucketList(boolean refresh) {
		if (refresh || (!refresh && !hasBuildImagesBucketList)) {
			buildImagesBucketList();
		}
		List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, ImageBucket> entry = (Entry<String, ImageBucket>) itr.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}

	/**
	 * 得到指定文件夹下的图片集合
	 * 
	 * @param folderArray
	 * @return
	 */
	public List<ImageBucket> getImageBucketList(String[] folderArray) {
		return getImageBucketList(Arrays.asList(folderArray));
	}

	/**
	 * 得到指定文件夹下的图片集合
	 * 
	 * @param folderList
	 * @return
	 */
	public List<ImageBucket> getImageBucketList(List<String> folderList) {
		List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
		for (String folder : folderList) {
			ImageBucket bucket = new ImageBucket();
			bucket.imageList = new ArrayList<ImageItem>();
			bucket.bucketName = folder.substring(folder.lastIndexOf("/") + 1, folder.length());
			bucket.bucketPath = folder;
			// 遍历某个文件夹下的所有图片文件
			File root = new File(folder);
			if (root != null) {
				File[] files = root.listFiles();
				if (files != null && files.length > 0) {
					for (File file : files) {
						if (file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".png") || file.getAbsolutePath().endsWith(".JPG") || file.getAbsolutePath().endsWith(".PNG")) {
							ImageItem imageItem = new ImageItem();
							imageItem.imagePath = file.getAbsolutePath();
							bucket.imageList.add(imageItem);
						}
					}
					// 排序
					Collections.sort(bucket.imageList, new SortClass());
					bucket.count = bucket.imageList.size();
					if (bucket.count > 0) {
						tmpList.add(bucket);
					}
				}
			}
		}
		return tmpList;
	}

	/**
	 * 按照最后更改时间排序
	 * 
	 * @author Oliver
	 *
	 */
	public class SortClass implements Comparator<ImageItem> {
		@Override
		public int compare(ImageItem lhs, ImageItem rhs) {
			File file1 = new File(lhs.imagePath);
			File file2 = new File(rhs.imagePath);
			return String.valueOf(file2.lastModified()).compareTo(String.valueOf(file1.lastModified()));
		}
	}
}
