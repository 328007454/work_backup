/*
 * DragSortListView.
 *
 * A subclass of the Android ListView component that enables drag
 * and drop re-ordering of list items.
 *
 * Copyright 2012 Carl Bauer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cnksi.core.view.swipemenulist;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.WrapperListAdapter;

import com.cnksi.core.R;
import com.cnksi.core.view.swipemenulist.SwipeMenuView.OnSwipeItemClickListener;

/**
 * ListView subclass that mediates drag and drop resorting of items.
 * 
 * 
 * @author heycosmo
 * 
 */
@SuppressLint("ClickableViewAccessibility")
public class SwipeMenuDragSortListView extends ListView {

	/*************************************** SwipeMenuList Start ******************************************/
	private static final int TOUCH_STATE_NONE = 0;
	private static final int TOUCH_STATE_X = 1;
	private static final int TOUCH_STATE_Y = 2;

	private int MAX_Y = 5;
	private int MAX_X = 3;
	private float mDownX;
	private float mDownY;
	private int mTouchState;
	private int mTouchPosition;
	private SwipeMenuLayout mTouchView;
	private OnSwipeListener mOnSwipeListener;

	private SwipeMenuCreator mMenuCreator;
	private OnMenuItemClickListener mOnMenuItemClickListener;
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;

	public boolean isMenuOpened() {
		return mTouchView == null ? false : mTouchView.isOpen();
	}

	public void openSwipeMenu() {
		if (mTouchView != null)
			mTouchView.openMenu();
	}

	public void setCloseInterpolator(Interpolator interpolator) {
		mCloseInterpolator = interpolator;
	}

	public void setOpenInterpolator(Interpolator interpolator) {
		mOpenInterpolator = interpolator;
	}

	public Interpolator getOpenInterpolator() {
		return mOpenInterpolator;
	}

	public Interpolator getCloseInterpolator() {
		return mCloseInterpolator;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		mAdapterWrapper = new AdapterWrapper(adapter) {
			@Override
			public void createMenu(SwipeMenu menu) {
				if (mMenuCreator != null) {
					mMenuCreator.create(menu);
				}
			}

			@Override
			public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
				if (mOnMenuItemClickListener != null) {
					mOnMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
				}
				if (mTouchView != null) {
					mTouchView.smoothCloseMenu();
				}
			}
		};
		super.setAdapter(mAdapterWrapper);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mDragSortController.isDrag()) {
			if (mIgnoreTouchEvent) {
				mIgnoreTouchEvent = false;
				return false;
			}
			if (!mDragEnabled) {
				return super.onTouchEvent(ev);
			}

			boolean more = false;
			boolean lastCallWasIntercept = mLastCallWasIntercept;
			mLastCallWasIntercept = false;

			if (!lastCallWasIntercept) {
				saveTouchCoords(ev);
			}
			if (mDragState == DRAGGING) {
				onDragTouchEvent(ev);
				more = true; // give us more!
			} else {
				if (mDragState == IDLE) {
					if (super.onTouchEvent(ev)) {
						more = true;
					}
				}
				int action = ev.getAction() & MotionEvent.ACTION_MASK;
				switch (action) {
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					doActionUpOrCancel();
					break;
				default:
					if (more) {
						mCancelMethod = ON_TOUCH_EVENT;
					}
				}
			}
			return more;
		} else {
			if ((ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null) || !mDragEnabled)
				return super.onTouchEvent(ev);
			if (mIgnoreTouchEvent) {
				mIgnoreTouchEvent = false;
				return false;
			}
			int action = ev.getAction() & MotionEvent.ACTION_MASK;
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				int oldPos = mTouchPosition;
				mDownX = ev.getX();
				mDownY = ev.getY();
				mTouchState = TOUCH_STATE_NONE;
				mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
				if (mTouchView != null && mTouchView.isOpen()) {
					if (mTouchPosition == oldPos) {
						mTouchState = TOUCH_STATE_X;
						mTouchView.onSwipe(ev);
						return true;
					} else {
						mTouchView.smoothCloseMenu();
						mTouchView = null;
						return false;
					}
				}
				View view = getChildAt(mTouchPosition - getFirstVisiblePosition());
				if (view instanceof SwipeMenuLayout) {
					mTouchView = (SwipeMenuLayout) view;
				}
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float dy = Math.abs((ev.getY() - mDownY));
				float dx = Math.abs((ev.getX() - mDownX));
				if (mTouchState == TOUCH_STATE_X) {
					if (mTouchView != null) {
						mTouchView.onSwipe(ev);
					}
					getSelector().setState(new int[] { 0 });
					ev.setAction(MotionEvent.ACTION_CANCEL);
					super.onTouchEvent(ev);
					return true;
				} else if (mTouchState == TOUCH_STATE_NONE) {
					if (Math.abs(dy) > MAX_Y) {
						mTouchState = TOUCH_STATE_Y;
					} else if (dx > MAX_X) {
						mTouchState = TOUCH_STATE_X;
						if (mOnSwipeListener != null) {
							mOnSwipeListener.onSwipeStart(mTouchPosition);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				doActionUpOrCancel();
				if (mTouchState == TOUCH_STATE_X) {
					if (mTouchView != null) {
						mTouchView.onSwipe(ev);
						if (!mTouchView.isOpen()) {
							mTouchPosition = -1;
							mTouchView = null;
						}
					}
					if (mOnSwipeListener != null) {
						mOnSwipeListener.onSwipeEnd(mTouchPosition);
					}
					ev.setAction(MotionEvent.ACTION_CANCEL);
					super.onTouchEvent(ev);
					return true;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				doActionUpOrCancel();
				break;
			}
			return super.onTouchEvent(ev);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mDragEnabled) {
			return super.onInterceptTouchEvent(ev);
		}
		saveTouchCoords(ev);
		mLastCallWasIntercept = true;

		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		if (action == MotionEvent.ACTION_DOWN) {
			if (mDragState != IDLE) {
				mIgnoreTouchEvent = true;
				return true;
			}
			mInTouchEvent = true;
		}
		boolean intercept = false;

		if (mFloatView != null) {
			intercept = true;
		} else {
			if (super.onInterceptTouchEvent(ev)) {
				mListViewIntercepted = true;
				intercept = true;
			}
			switch (action) {
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				doActionUpOrCancel();
				break;
			default:
				if (intercept) {
					mCancelMethod = ON_TOUCH_EVENT;
				} else {
					mCancelMethod = ON_INTERCEPT_TOUCH_EVENT;
				}
			}
		}
		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
			mInTouchEvent = false;
		}
		return intercept;
	}

	public void smoothOpenMenu(int position) {
		if (position >= getFirstVisiblePosition() && position <= getLastVisiblePosition()) {
			View view = getChildAt(position - getFirstVisiblePosition());
			if (view instanceof SwipeMenuLayout) {
				mTouchPosition = position;
				if (mTouchView != null && mTouchView.isOpen()) {
					mTouchView.smoothCloseMenu();
				}
				mTouchView = (SwipeMenuLayout) view;
				mTouchView.smoothOpenMenu();
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
	}

	public void setMenuCreator(SwipeMenuCreator menuCreator) {
		this.mMenuCreator = menuCreator;
	}

	public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
		this.mOnMenuItemClickListener = onMenuItemClickListener;
	}

	public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.mOnSwipeListener = onSwipeListener;
	}

	public static interface OnMenuItemClickListener {
		void onMenuItemClick(int position, SwipeMenu menu, int index);
	}

	public static interface OnSwipeListener {
		void onSwipeStart(int position);

		void onSwipeEnd(int position);
	}

	/*************************************** SwipeMenuList End ******************************************/

	/**
	 * The View that floats above the ListView and represents the dragged item.
	 */
	private View mFloatView;

	/**
	 * The float View location. First based on touch location and given deltaX and deltaY. Then restricted by callback to FloatViewManager.onDragFloatView(). Finally restricted by bounds of DSLV.
	 */
	private Point mFloatLoc = new Point();

	private Point mTouchLoc = new Point();

	/**
	 * The middle (in the y-direction) of the floating View.
	 */
	private int mFloatViewMid;

	/**
	 * Flag to make sure float View isn't measured twice
	 */
	private boolean mFloatViewOnMeasured = false;

	/**
	 * Transparency for the floating View (XML attribute).
	 */
	private float mFloatAlpha = 1.0f;
	private float mCurrFloatAlpha = 1.0f;

	/**
	 * While drag-sorting, the current position of the floating View. If dropped, the dragged item will land in this position.
	 */
	private int mFloatPos;

	/**
	 * The first expanded ListView position that helps represent the drop slot tracking the floating View.
	 */
	private int mFirstExpPos;

	/**
	 * The second expanded ListView position that helps represent the drop slot tracking the floating View. This can equal mFirstExpPos if there is no slide shuffle occurring; otherwise it is equal to mFirstExpPos + 1.
	 */
	private int mSecondExpPos;

	/**
	 * Flag set if slide shuffling is enabled.
	 */
	private boolean mAnimate = false;

	/**
	 * The user dragged from this position.
	 */
	private int mSrcPos;

	/**
	 * Offset (in x) within the dragged item at which the user picked it up (or first touched down with the digitalis).
	 */
	private int mDragDeltaX;

	/**
	 * Offset (in y) within the dragged item at which the user picked it up (or first touched down with the digitalis).
	 */
	private int mDragDeltaY;

	/**
	 * A listener that receives a callback when the floating View is dropped.
	 */
	private DragListener mDragListener;

	/**
	 * Enable/Disable item dragging
	 * 
	 * @attr name dslv:drag_enabled
	 */
	private boolean mDragEnabled = true;

	/**
	 * Drag state enum.
	 */
	private final static int IDLE = 0;
	private final static int DROPPING = 2;
	private final static int STOPPED = 3;
	private final static int DRAGGING = 4;

	private int mDragState = IDLE;

	/**
	 * Height in pixels to which the originally dragged item is collapsed during a drag-sort. Currently, this value must be greater than zero.
	 */
	private int mItemHeightCollapsed = 1;

	/**
	 * Height of the floating View. Stored for the purpose of providing the tracking drop slot.
	 */
	private int mFloatViewHeight;

	/**
	 * Convenience member. See above.
	 */
	private int mFloatViewHeightHalf;

	/**
	 * Save the given width spec for use in measuring children
	 */
	private int mWidthMeasureSpec = 0;

	/**
	 * Sample Views ultimately used for calculating the height of ListView items that are off-screen.
	 */
	private View[] mSampleViewTypes = new View[1];

	/**
	 * Drag-scroll encapsulator!
	 */
	private DragScroller mDragScroller;

	/**
	 * Determines the start of the upward drag-scroll region at the top of the ListView. Specified by a fraction of the ListView height, thus screen resolution agnostic.
	 */
	private float mDragUpScrollStartFrac = 1.0f / 3.0f;

	/**
	 * Determines the start of the downward drag-scroll region at the bottom of the ListView. Specified by a fraction of the ListView height, thus screen resolution agnostic.
	 */
	private float mDragDownScrollStartFrac = 1.0f / 3.0f;

	/**
	 * The following are calculated from the above fracs.
	 */
	private int mUpScrollStartY;
	private int mDownScrollStartY;
	private float mDownScrollStartYF;
	private float mUpScrollStartYF;

	/**
	 * Calculated from above above and current ListView height.
	 */
	private float mDragUpScrollHeight;

	/**
	 * Calculated from above above and current ListView height.
	 */
	private float mDragDownScrollHeight;

	/**
	 * Maximum drag-scroll speed in pixels per ms. Only used with default linear drag-scroll profile.
	 */
	private float mMaxScrollSpeed = 0.5f;

	/**
	 * Defines the scroll speed during a drag-scroll. User can provide their own; this default is a simple linear profile where scroll speed increases linearly as the floating View nears the top/bottom of the ListView.
	 */
	private DragScrollProfile mScrollProfile = new DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			return mMaxScrollSpeed * w;
		}
	};

	/**
	 * Current touch x.
	 */
	private int mX;

	/**
	 * Current touch y.
	 */
	private int mY;

	/**
	 * Last touch x.
	 */
	// private int mLastX;

	/**
	 * Last touch y.
	 */
	private int mLastY;

	/**
	 * Drag flag bit. Floating View can move in the positive x direction.
	 */
	public final static int DRAG_POS_X = 0x1;

	/**
	 * Drag flag bit. Floating View can move in the negative x direction.
	 */
	public final static int DRAG_NEG_X = 0x2;

	/**
	 * Drag flag bit. Floating View can move in the positive y direction. This is subtle. What this actually means is that, if enabled, the floating View can be dragged below its starting position. Remove in favor of upper-bounding item position?
	 */
	public final static int DRAG_POS_Y = 0x4;

	/**
	 * Drag flag bit. Floating View can move in the negative y direction. This is subtle. What this actually means is that the floating View can be dragged above its starting position. Remove in favor of lower-bounding item position?
	 */
	public final static int DRAG_NEG_Y = 0x8;

	/**
	 * Flags that determine limits on the motion of the floating View. See flags above.
	 */
	private int mDragFlags = 0;

	/**
	 * Last call to an on*TouchEvent was a call to onInterceptTouchEvent.
	 */
	private boolean mLastCallWasIntercept = false;

	/**
	 * A touch event is in progress.
	 */
	private boolean mInTouchEvent = false;

	/**
	 * Let the user customize the floating View.
	 */
	private FloatViewManager mFloatViewManager = null;

	/**
	 * Given to ListView to cancel its action when a drag-sort begins.
	 */
	private MotionEvent mCancelEvent;

	/**
	 * Enum telling where to cancel the ListView action when a drag-sort begins
	 */
	private static final int NO_CANCEL = 0;
	private static final int ON_TOUCH_EVENT = 1;
	private static final int ON_INTERCEPT_TOUCH_EVENT = 2;

	/**
	 * Where to cancel the ListView action when a drag-sort begins
	 */
	private int mCancelMethod = NO_CANCEL;

	/**
	 * Determines when a slide shuffle animation starts. That is, defines how close to the edge of the drop slot the floating View must be to initiate the slide.
	 */
	private float mSlideRegionFrac = 0.25f;

	/**
	 * Number between 0 and 1 indicating the relative location of a sliding item (only used if drag-sort animations are turned on). Nearly 1 means the item is at the top of the slide region (nearly full blank item is directly below).
	 */
	private float mSlideFrac = 0.0f;

	/**
	 * Wraps the user-provided ListAdapter. This is used to wrap each item View given by the user inside another View (currenly a RelativeLayout) which expands and collapses to simulate the item shuffling.
	 */
	private AdapterWrapper mAdapterWrapper;

	/**
	 * Needed for adjusting item heights from within layoutChildren
	 */
	private boolean mBlockLayoutRequests = false;

	/**
	 * Set to true when a down event happens during drag sort; for example, when drag finish animations are playing.
	 */
	private boolean mIgnoreTouchEvent = false;

	/**
	 * Caches DragSortItemView child heights. Sometimes DSLV has to know the height of an offscreen item. Since ListView virtualizes these, DSLV must get the item from the ListAdapter to obtain its height. That process can be expensive, but often the same offscreen item will be requested many times in a row. Once an offscreen item height is calculated, we cache it in this guy. Actually, we cache the height of the child of the DragSortItemView since the item height changes often during a drag-sort.
	 */
	private static final int sCacheSize = 3;
	private HeightCache mChildHeightCache = new HeightCache(sCacheSize);

	private DropAnimator mDropAnimator;
	private DragSortController mDragSortController = null;

	public SwipeMenuDragSortListView(Context context) {
		this(context, null);
	}

	public SwipeMenuDragSortListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeMenuDragSortListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		MAX_X = dp2px(MAX_X);
		MAX_Y = dp2px(MAX_Y);
		mTouchState = TOUCH_STATE_NONE;

		int dropAnimDuration = 150;
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeMenuDragSortListView, 0, 0);
			mItemHeightCollapsed = Math.max(1, a.getDimensionPixelSize(R.styleable.SwipeMenuDragSortListView_collapsed_height, 1));
			mFloatAlpha = a.getFloat(R.styleable.SwipeMenuDragSortListView_float_alpha, mFloatAlpha);
			mCurrFloatAlpha = mFloatAlpha;
			mDragEnabled = a.getBoolean(R.styleable.SwipeMenuDragSortListView_drag_enabled, mDragEnabled);
			mSlideRegionFrac = Math.max(0.0f, Math.min(1.0f, 1.0f - a.getFloat(R.styleable.SwipeMenuDragSortListView_slide_shuffle_speed, 0.75f)));

			mAnimate = mSlideRegionFrac > 0.0f;
			float frac = a.getFloat(R.styleable.SwipeMenuDragSortListView_drag_scroll_start, mDragUpScrollStartFrac);
			setDragScrollStart(frac);
			mMaxScrollSpeed = a.getFloat(R.styleable.SwipeMenuDragSortListView_max_drag_scroll_speed, mMaxScrollSpeed);
			dropAnimDuration = a.getInt(R.styleable.SwipeMenuDragSortListView_drop_animation_duration, dropAnimDuration);

			boolean sortEnabled = a.getBoolean(R.styleable.SwipeMenuDragSortListView_sort_enabled, true);
			int dragInitMode = a.getInt(R.styleable.SwipeMenuDragSortListView_drag_start_mode, DragSortController.ON_DOWN);
			int dragHandleId = a.getResourceId(R.styleable.SwipeMenuDragSortListView_drag_handle_id, 0);
			int bgColor = a.getColor(R.styleable.SwipeMenuDragSortListView_float_background_color, Color.BLACK);

			mDragSortController = new DragSortController(this, dragHandleId, dragInitMode);
			mDragSortController.setSortEnabled(sortEnabled);
			mDragSortController.setBackgroundColor(bgColor);

			mFloatViewManager = mDragSortController;
			setOnTouchListener(mDragSortController);
			a.recycle();
		}
		mDragScroller = new DragScroller();
		mDropAnimator = new DropAnimator(0.5f, dropAnimDuration);

		mCancelEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0f, 0f, 0f, 0f, 0, 0f, 0f, 0, 0);
		mCancelEvent.recycle();
	}

	/**
	 * Usually called from a FloatViewManager. The float alpha will be reset to the xml-defined value every time a drag is stopped.
	 */
	public void setFloatAlpha(float alpha) {
		mCurrFloatAlpha = alpha;
	}

	public float getFloatAlpha() {
		return mCurrFloatAlpha;
	}

	/**
	 * Set maximum drag scroll speed in positions/second. Only applies if using default ScrollSpeedProfile.
	 * 
	 * @param max
	 *            Maximum scroll speed.
	 */
	public void setMaxScrollSpeed(float max) {
		mMaxScrollSpeed = max;
	}

	/**
	 * As opposed to {@link ListView#getAdapter()}, which returns a heavily wrapped ListAdapter (DragSortListView wraps the input ListAdapter {\emph and} ListView wraps the wrapped one).
	 * 
	 * @return The ListAdapter set as the argument of {@link setAdapter()}
	 */
	public ListAdapter getInputAdapter() {
		if (mAdapterWrapper == null) {
			return null;
		} else {
			return mAdapterWrapper.getListAdapter();
		}
	}

	private class AdapterWrapper extends BaseAdapter implements WrapperListAdapter, OnSwipeItemClickListener {

		private ListAdapter mAdapter;
		private OnMenuItemClickListener onMenuItemClickListener;

		public AdapterWrapper(ListAdapter adapter) {
			super();
			mAdapter = adapter;
			mAdapter.registerDataSetObserver(new DataSetObserver() {
				public void onChanged() {
					cancelDrag();
					notifyDataSetChanged();
				}

				public void onInvalidated() {
					cancelDrag();
					notifyDataSetInvalidated();
				}
			});
		}

		@Override
		public ListAdapter getWrappedAdapter() {
			return mAdapter;
		}

		public ListAdapter getListAdapter() {
			return mAdapter;
		}

		@Override
		public long getItemId(int position) {
			return mAdapter.getItemId(position);
		}

		@Override
		public Object getItem(int position) {
			return mAdapter.getItem(position);
		}

		@Override
		public int getCount() {
			return mAdapter.getCount();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return mAdapter.areAllItemsEnabled();
		}

		@Override
		public boolean isEnabled(int position) {
			return mAdapter.isEnabled(position);
		}

		@Override
		public int getItemViewType(int position) {
			return mAdapter.getItemViewType(position);
		}

		@Override
		public int getViewTypeCount() {
			return mAdapter.getViewTypeCount();
		}

		@Override
		public boolean hasStableIds() {
			return mAdapter.hasStableIds();
		}

		@Override
		public boolean isEmpty() {
			return mAdapter.isEmpty();
		}

		@Override
		public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
			if (onMenuItemClickListener != null) {
				onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
			}
		}

		public void createMenu(SwipeMenu menu) {

		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			mAdapter.registerDataSetObserver(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			mAdapter.unregisterDataSetObserver(observer);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SwipeMenuLayout layout = null;
			View contentView = null;
			if (convertView == null) {
				contentView = mAdapter.getView(position, null, SwipeMenuDragSortListView.this);
				SwipeMenu menu = new SwipeMenu(getContext());
				menu.setViewType(mAdapter.getItemViewType(position));
				createMenu(menu);
				SwipeMenuView menuView = new SwipeMenuView(menu, (SwipeMenuDragSortListView) parent);
				menuView.setOnSwipeItemClickListener(AdapterWrapper.this);
				SwipeMenuDragSortListView listView = (SwipeMenuDragSortListView) parent;
				layout = new SwipeMenuLayout(contentView, menuView, listView.getCloseInterpolator(), listView.getOpenInterpolator());
				// layout.setSwipe(false);
				layout.setPosition(position);
				layout.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				// layout.addView(contentView);
			} else {
				layout = (SwipeMenuLayout) convertView;
				layout.closeMenu();
				layout.setPosition(position);
				// layout.setSwipe(false);
				// mAdapter.getView(position, layout.getContentView(), parent);
				View oldContentView = layout.getChildAt(0);
				contentView = mAdapter.getView(position, oldContentView, SwipeMenuDragSortListView.this);
				if (contentView != oldContentView) {
					if (oldContentView != null) {
						layout.removeViewAt(0);
					}
					// layout.addView(contentView);
				}
			}
			adjustItem(position + getHeaderViewsCount(), layout, true);
			return layout;
		}
	}

	private void drawDivider(int expPosition, Canvas canvas) {
		final Drawable divider = getDivider();
		final int dividerHeight = getDividerHeight();
		if (divider != null && dividerHeight != 0) {
			final ViewGroup expItem = (ViewGroup) getChildAt(expPosition - getFirstVisiblePosition());
			if (expItem != null) {
				final int l = getPaddingLeft();
				final int r = getWidth() - getPaddingRight();
				final int t;
				final int b;
				final int childHeight = expItem.getChildAt(0).getHeight();
				if (expPosition > mSrcPos) {
					t = expItem.getTop() + childHeight;
					b = t + dividerHeight;
				} else {
					b = expItem.getBottom() - childHeight;
					t = b - dividerHeight;
				}

				canvas.save();
				canvas.clipRect(l, t, r, b);
				divider.setBounds(l, t, r, b);
				divider.draw(canvas);
				canvas.restore();
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (mDragState != IDLE) {
			if (mFirstExpPos != mSrcPos) {
				drawDivider(mFirstExpPos, canvas);
			}
			if (mSecondExpPos != mFirstExpPos && mSecondExpPos != mSrcPos) {
				drawDivider(mSecondExpPos, canvas);
			}
		}

		if (mFloatView != null) {
			final int w = mFloatView.getWidth();
			final int h = mFloatView.getHeight();

			int x = mFloatLoc.x;

			int width = getWidth();
			if (x < 0)
				x = -x;
			float alphaMod;
			if (x < width) {
				alphaMod = ((float) (width - x)) / ((float) width);
				alphaMod *= alphaMod;
			} else {
				alphaMod = 0;
			}
			final int alpha = (int) (255f * mCurrFloatAlpha * alphaMod);
			canvas.save();
			canvas.translate(mFloatLoc.x, mFloatLoc.y);
			canvas.clipRect(0, 0, w, h);

			canvas.saveLayerAlpha(0, 0, w, h, alpha, Canvas.ALL_SAVE_FLAG);
			mFloatView.draw(canvas);
			canvas.restore();
			canvas.restore();
		}
	}

	private int getItemHeight(int position) {
		View v = getChildAt(position - getFirstVisiblePosition());
		if (v != null) {
			return v.getHeight();
		} else {
			return calcItemHeight(position, getChildHeight(position));
		}
	}

	private class HeightCache {
		private SparseIntArray mMap;
		private ArrayList<Integer> mOrder;
		private int mMaxSize;

		public HeightCache(int size) {
			mMap = new SparseIntArray(size);
			mOrder = new ArrayList<Integer>(size);
			mMaxSize = size;
		}

		public void add(int position, int height) {
			int currHeight = mMap.get(position, -1);
			if (currHeight != height) {
				if (currHeight == -1) {
					if (mMap.size() == mMaxSize) {
						mMap.delete(mOrder.remove(0));
					}
				} else {
					mOrder.remove((Integer) position);
				}
				mMap.put(position, height);
				mOrder.add(position);
			}
		}

		public int get(int position) {
			return mMap.get(position, -1);
		}

		public void clear() {
			mMap.clear();
			mOrder.clear();
		}

	}

	private int getShuffleEdge(int position, int top) {

		final int numHeaders = getHeaderViewsCount();
		final int numFooters = getFooterViewsCount();

		if (position <= numHeaders || (position >= getCount() - numFooters)) {
			return top;
		}

		int divHeight = getDividerHeight();

		int edge;

		int maxBlankHeight = mFloatViewHeight - mItemHeightCollapsed;
		int childHeight = getChildHeight(position);
		int itemHeight = getItemHeight(position);

		int otop = top;
		if (mSecondExpPos <= mSrcPos) {
			if (position == mSecondExpPos && mFirstExpPos != mSecondExpPos) {
				if (position == mSrcPos) {
					otop = top + itemHeight - mFloatViewHeight;
				} else {
					int blankHeight = itemHeight - childHeight;
					otop = top + blankHeight - maxBlankHeight;
				}
			} else if (position > mSecondExpPos && position <= mSrcPos) {
				otop = top - maxBlankHeight;
			}

		} else {
			if (position > mSrcPos && position <= mFirstExpPos) {
				otop = top + maxBlankHeight;
			} else if (position == mSecondExpPos && mFirstExpPos != mSecondExpPos) {
				int blankHeight = itemHeight - childHeight;
				otop = top + blankHeight;
			}
		}

		if (position <= mSrcPos) {
			edge = otop + (mFloatViewHeight - divHeight - getChildHeight(position - 1)) / 2;
		} else {
			edge = otop + (childHeight - divHeight - mFloatViewHeight) / 2;
		}

		return edge;
	}

	private boolean updatePositions() {

		final int first = getFirstVisiblePosition();
		int startPos = mFirstExpPos;
		View startView = getChildAt(startPos - first);

		if (startView == null) {
			startPos = first + getChildCount() / 2;
			startView = getChildAt(startPos - first);
		}
		int startTop = startView.getTop();

		int itemHeight = startView.getHeight();

		int edge = getShuffleEdge(startPos, startTop);
		int lastEdge = edge;

		int divHeight = getDividerHeight();

		int itemPos = startPos;
		int itemTop = startTop;
		if (mFloatViewMid < edge) {
			while (itemPos >= 0) {
				itemPos--;
				itemHeight = getItemHeight(itemPos);

				if (itemPos == 0) {
					edge = itemTop - divHeight - itemHeight;
					break;
				}

				itemTop -= itemHeight + divHeight;
				edge = getShuffleEdge(itemPos, itemTop);
				if (mFloatViewMid >= edge) {
					break;
				}
				lastEdge = edge;
			}
		} else {
			final int count = getCount();
			while (itemPos < count) {
				if (itemPos == count - 1) {
					edge = itemTop + divHeight + itemHeight;
					break;
				}
				itemTop += divHeight + itemHeight;
				itemHeight = getItemHeight(itemPos + 1);
				edge = getShuffleEdge(itemPos + 1, itemTop);
				if (mFloatViewMid < edge) {
					break;
				}
				lastEdge = edge;
				itemPos++;
			}
		}

		final int numHeaders = getHeaderViewsCount();
		final int numFooters = getFooterViewsCount();

		boolean updated = false;

		int oldFirstExpPos = mFirstExpPos;
		int oldSecondExpPos = mSecondExpPos;
		float oldSlideFrac = mSlideFrac;

		if (mAnimate) {
			int edgeToEdge = Math.abs(edge - lastEdge);

			int edgeTop, edgeBottom;
			if (mFloatViewMid < edge) {
				edgeBottom = edge;
				edgeTop = lastEdge;
			} else {
				edgeTop = edge;
				edgeBottom = lastEdge;
			}

			int slideRgnHeight = (int) (0.5f * mSlideRegionFrac * edgeToEdge);
			float slideRgnHeightF = (float) slideRgnHeight;
			int slideEdgeTop = edgeTop + slideRgnHeight;
			int slideEdgeBottom = edgeBottom - slideRgnHeight;

			if (mFloatViewMid < slideEdgeTop) {
				mFirstExpPos = itemPos - 1;
				mSecondExpPos = itemPos;
				mSlideFrac = 0.5f * ((float) (slideEdgeTop - mFloatViewMid)) / slideRgnHeightF;
			} else if (mFloatViewMid < slideEdgeBottom) {
				mFirstExpPos = itemPos;
				mSecondExpPos = itemPos;
			} else {
				mFirstExpPos = itemPos;
				mSecondExpPos = itemPos + 1;
				mSlideFrac = 0.5f * (1.0f + ((float) (edgeBottom - mFloatViewMid)) / slideRgnHeightF);
			}

		} else {
			mFirstExpPos = itemPos;
			mSecondExpPos = itemPos;
		}

		if (mFirstExpPos < numHeaders) {
			itemPos = numHeaders;
			mFirstExpPos = itemPos;
			mSecondExpPos = itemPos;
		} else if (mSecondExpPos >= getCount() - numFooters) {
			itemPos = getCount() - numFooters - 1;
			mFirstExpPos = itemPos;
			mSecondExpPos = itemPos;
		}

		if (mFirstExpPos != oldFirstExpPos || mSecondExpPos != oldSecondExpPos || mSlideFrac != oldSlideFrac) {
			updated = true;
		}

		if (itemPos != mFloatPos) {
			mFloatPos = itemPos;
			updated = true;
		}

		return updated;
	}

	private class SmoothAnimator implements Runnable {
		protected long mStartTime;

		private float mDurationF;

		private float mAlpha;
		private float mA, mB, mC, mD;

		private boolean mCanceled;

		public SmoothAnimator(float smoothness, int duration) {
			mAlpha = smoothness;
			mDurationF = (float) duration;
			mA = mD = 1f / (2f * mAlpha * (1f - mAlpha));
			mB = mAlpha / (2f * (mAlpha - 1f));
			mC = 1f / (1f - mAlpha);
		}

		public float transform(float frac) {
			if (frac < mAlpha) {
				return mA * frac * frac;
			} else if (frac < 1f - mAlpha) {
				return mB + mC * frac;
			} else {
				return 1f - mD * (frac - 1f) * (frac - 1f);
			}
		}

		public void start() {
			mStartTime = SystemClock.uptimeMillis();
			mCanceled = false;
			onStart();
			post(this);
		}

		public void cancel() {
			mCanceled = true;
		}

		public void onStart() {
		}

		public void onUpdate(float frac, float smoothFrac) {
			// stub
		}

		public void onStop() {
			// stub
		}

		@Override
		public void run() {
			if (mCanceled) {
				return;
			}

			float fraction = ((float) (SystemClock.uptimeMillis() - mStartTime)) / mDurationF;

			if (fraction >= 1f) {
				onUpdate(1f, 1f);
				onStop();
			} else {
				onUpdate(fraction, transform(fraction));
				post(this);
			}
		}
	}

	/**
	 * Centers floating View over drop slot before destroying.
	 */
	private class DropAnimator extends SmoothAnimator {

		private int mDropPos;
		private int srcPos;
		private float mInitDeltaY;
		private float mInitDeltaX;

		public DropAnimator(float smoothness, int duration) {
			super(smoothness, duration);
		}

		@Override
		public void onStart() {
			mDropPos = mFloatPos;
			srcPos = mSrcPos;
			mDragState = DROPPING;
			mInitDeltaY = mFloatLoc.y - getTargetY();
			mInitDeltaX = mFloatLoc.x - getPaddingLeft();
		}

		private int getTargetY() {
			final int first = getFirstVisiblePosition();
			final int otherAdjust = (mItemHeightCollapsed + getDividerHeight()) / 2;
			View v = getChildAt(mDropPos - first);
			int targetY = -1;
			if (v != null) {
				if (mDropPos == srcPos) {
					targetY = v.getTop();
				} else if (mDropPos < srcPos) {
					targetY = v.getTop() - otherAdjust;
				} else {
					targetY = v.getBottom() + otherAdjust - mFloatViewHeight;
				}
			} else {
				cancel();
			}

			return targetY;
		}

		@Override
		public void onUpdate(float frac, float smoothFrac) {
			final int targetY = getTargetY();
			final int targetX = getPaddingLeft();
			final float deltaY = mFloatLoc.y - targetY;
			final float deltaX = mFloatLoc.x - targetX;
			final float f = 1f - smoothFrac;
			if (f < Math.abs(deltaY / mInitDeltaY) || f < Math.abs(deltaX / mInitDeltaX)) {
				mFloatLoc.y = targetY + (int) (mInitDeltaY * f);
				mFloatLoc.x = getPaddingLeft() + (int) (mInitDeltaX * f);
				doDragFloatView(true);
			}
		}

		@Override
		public void onStop() {
			dropFloatView();
		}
	}

	public void moveItem(int from, int to) {
		if (mDragListener != null) {
			final int count = getInputAdapter().getCount();
			if (from >= 0 && from < count && to >= 0 && to < count) {
				mDragListener.drag(from, to);
			}
		}
	}

	/**
	 * Cancel a drag. Calls {@link #stopDrag(boolean, boolean)} with <code>true</code> as the first argument.
	 */
	public void cancelDrag() {
		if (mDragState == DRAGGING) {
			mDragScroller.stopScrolling(true);
			destroyFloatView();
			clearPositions();
			adjustAllItems();

			if (mInTouchEvent) {
				mDragState = STOPPED;
			} else {
				mDragState = IDLE;
			}
		}
	}

	private void clearPositions() {
		mSrcPos = -1;
		mFirstExpPos = -1;
		mSecondExpPos = -1;
		mFloatPos = -1;
	}

	private void dropFloatView() {
		mDragState = DROPPING;
		if (mDragListener != null && mFloatPos >= 0 && mFloatPos < getCount()) {
			final int numHeaders = getHeaderViewsCount();
			mDragListener.drag(mSrcPos - numHeaders, mFloatPos - numHeaders);
		}
		destroyFloatView();
		adjustOnReorder();
		clearPositions();
		adjustAllItems();
		if (mInTouchEvent) {
			mDragState = STOPPED;
		} else {
			mDragState = IDLE;
		}
	}

	private void adjustOnReorder() {
		final int firstPos = getFirstVisiblePosition();
		if (mSrcPos < firstPos) {
			View v = getChildAt(0);
			int top = 0;
			if (v != null) {
				top = v.getTop();
			}
			setSelectionFromTop(firstPos - 1, top - getPaddingTop());
		}
	}

	public boolean stopDrag(float velocityX) {
		if (mFloatView != null) {
			mDragScroller.stopScrolling(true);
			if (mDropAnimator != null) {
				mDropAnimator.start();
			} else {
				dropFloatView();
			}
			return true;
		} else {
			return false;
		}
	}

	private void doActionUpOrCancel() {
		mCancelMethod = NO_CANCEL;
		mInTouchEvent = false;
		if (mDragState == STOPPED) {
			mDragState = IDLE;
		}
		mCurrFloatAlpha = mFloatAlpha;
		mListViewIntercepted = false;
		mChildHeightCache.clear();
	}

	private void saveTouchCoords(MotionEvent ev) {
		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		if (action != MotionEvent.ACTION_DOWN) {
			mLastY = mY;
		}
		mX = (int) ev.getX();
		mY = (int) ev.getY();
		if (action == MotionEvent.ACTION_DOWN) {
			mLastY = mY;
		}
	}

	public boolean listViewIntercepted() {
		return mListViewIntercepted;
	}

	private boolean mListViewIntercepted = false;

	public void setDragScrollStart(float heightFraction) {
		setDragScrollStarts(heightFraction, heightFraction);
	}

	public void setDragScrollStarts(float upperFrac, float lowerFrac) {
		if (lowerFrac > 0.5f) {
			mDragDownScrollStartFrac = 0.5f;
		} else {
			mDragDownScrollStartFrac = lowerFrac;
		}
		if (upperFrac > 0.5f) {
			mDragUpScrollStartFrac = 0.5f;
		} else {
			mDragUpScrollStartFrac = upperFrac;
		}
		if (getHeight() != 0) {
			updateScrollStarts();
		}
	}

	private void continueDrag(int x, int y) {

		mFloatLoc.x = x - mDragDeltaX;
		mFloatLoc.y = y - mDragDeltaY;

		doDragFloatView(true);

		int minY = Math.min(y, mFloatViewMid + mFloatViewHeightHalf);
		int maxY = Math.max(y, mFloatViewMid - mFloatViewHeightHalf);

		int currentScrollDir = mDragScroller.getScrollDir();

		if (minY > mLastY && minY > mDownScrollStartY && currentScrollDir != DragScroller.DOWN) {
			if (currentScrollDir != DragScroller.STOP) {
				// moved directly from up scroll to down scroll
				mDragScroller.stopScrolling(true);
			}
			mDragScroller.startScrolling(DragScroller.DOWN);
		} else if (maxY < mLastY && maxY < mUpScrollStartY && currentScrollDir != DragScroller.UP) {
			if (currentScrollDir != DragScroller.STOP) {
				mDragScroller.stopScrolling(true);
			}
			mDragScroller.startScrolling(DragScroller.UP);
		} else if (maxY >= mUpScrollStartY && minY <= mDownScrollStartY && mDragScroller.isScrolling()) {
			mDragScroller.stopScrolling(true);
		}
	}

	private void updateScrollStarts() {
		final int padTop = getPaddingTop();
		final int listHeight = getHeight() - padTop - getPaddingBottom();
		float heightF = (float) listHeight;

		mUpScrollStartYF = padTop + mDragUpScrollStartFrac * heightF;
		mDownScrollStartYF = padTop + (1.0f - mDragDownScrollStartFrac) * heightF;

		mUpScrollStartY = (int) mUpScrollStartYF;
		mDownScrollStartY = (int) mDownScrollStartYF;

		mDragUpScrollHeight = mUpScrollStartYF - padTop;
		mDragDownScrollHeight = padTop + listHeight - mDownScrollStartYF;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		updateScrollStarts();
	}

	private void adjustAllItems() {
		final int first = getFirstVisiblePosition();
		final int last = getLastVisiblePosition();
		int begin = Math.max(0, getHeaderViewsCount() - first);
		int end = Math.min(last - first, getCount() - 1 - getFooterViewsCount() - first);
		for (int i = begin; i <= end; ++i) {
			View v = getChildAt(i);
			if (v != null) {
				adjustItem(first + i, v, false);
			}
		}
	}

	private void adjustItem(int position, View v, boolean invalidChildHeight) {
		ViewGroup.LayoutParams lp = v.getLayoutParams();
		int height;
		if (position != mSrcPos && position != mFirstExpPos && position != mSecondExpPos) {
			height = ViewGroup.LayoutParams.WRAP_CONTENT;
		} else {
			height = calcItemHeight(position, v, invalidChildHeight);
		}

		if (height != lp.height) {
			lp.height = height;
			v.setLayoutParams(lp);
		}

		if (position == mFirstExpPos || position == mSecondExpPos) {
			if (position < mSrcPos) {
				((SwipeMenuLayout) v).setGravity(Gravity.BOTTOM);
			} else if (position > mSrcPos) {
				((SwipeMenuLayout) v).setGravity(Gravity.TOP);
			}
		}
		int oldVis = v.getVisibility();
		int vis = View.VISIBLE;

		if (position == mSrcPos && mFloatView != null) {
			vis = View.INVISIBLE;
		}
		if (vis != oldVis) {
			v.setVisibility(vis);
		}
	}

	private int getChildHeight(int position) {
		if (position == mSrcPos) {
			return 0;
		}
		View v = getChildAt(position - getFirstVisiblePosition());
		if (v != null) {
			return getChildHeight(position, v, false);
		} else {
			int childHeight = mChildHeightCache.get(position);
			if (childHeight != -1) {
				return childHeight;
			}
			final ListAdapter adapter = getAdapter();
			int type = adapter.getItemViewType(position);

			final int typeCount = adapter.getViewTypeCount();
			if (typeCount != mSampleViewTypes.length) {
				mSampleViewTypes = new View[typeCount];
			}
			if (type >= 0) {
				if (mSampleViewTypes[type] == null) {
					v = adapter.getView(position, null, this);
					mSampleViewTypes[type] = v;
				} else {
					v = adapter.getView(position, mSampleViewTypes[type], this);
				}
			} else {
				v = adapter.getView(position, null, this);
			}
			childHeight = getChildHeight(position, v, true);
			mChildHeightCache.add(position, childHeight);
			return childHeight;
		}
	}

	private int getChildHeight(int position, View item, boolean invalidChildHeight) {
		if (position == mSrcPos) {
			return 0;
		}
		View child;
		if (position < getHeaderViewsCount() || position >= getCount() - getFooterViewsCount()) {
			child = item;
		} else {
			child = ((ViewGroup) item).getChildAt(0);
		}
		ViewGroup.LayoutParams lp = child.getLayoutParams();

		if (lp != null) {
			if (lp.height > 0) {
				return lp.height;
			}
		}
		int childHeight = child.getHeight();

		if (childHeight == 0 || invalidChildHeight) {
			measureItem(child);
			childHeight = child.getMeasuredHeight();
		}

		return childHeight;
	}

	private int calcItemHeight(int position, View item, boolean invalidChildHeight) {
		return calcItemHeight(position, getChildHeight(position, item, invalidChildHeight));
	}

	private int calcItemHeight(int position, int childHeight) {

		boolean isSliding = mAnimate && mFirstExpPos != mSecondExpPos;
		int maxNonSrcBlankHeight = mFloatViewHeight - mItemHeightCollapsed;
		int slideHeight = (int) (mSlideFrac * maxNonSrcBlankHeight);

		int height;

		if (position == mSrcPos) {
			if (mSrcPos == mFirstExpPos) {
				if (isSliding) {
					height = slideHeight + mItemHeightCollapsed;
				} else {
					height = mFloatViewHeight;
				}
			} else if (mSrcPos == mSecondExpPos) {
				// if gets here, we know an item is sliding
				height = mFloatViewHeight - slideHeight;
			} else {
				height = mItemHeightCollapsed;
			}
		} else if (position == mFirstExpPos) {
			if (isSliding) {
				height = childHeight + slideHeight;
			} else {
				height = childHeight + maxNonSrcBlankHeight;
			}
		} else if (position == mSecondExpPos) {
			// we know an item is sliding (b/c 2ndPos != 1stPos)
			height = childHeight + maxNonSrcBlankHeight - slideHeight;
		} else {
			height = childHeight;
		}

		return height;
	}

	@Override
	public void requestLayout() {
		if (!mBlockLayoutRequests) {
			super.requestLayout();
		}
	}

	private int adjustScroll(int movePos, View moveItem, int oldFirstExpPos, int oldSecondExpPos) {
		int adjust = 0;

		final int childHeight = getChildHeight(movePos);

		int moveHeightBefore = moveItem.getHeight();
		int moveHeightAfter = calcItemHeight(movePos, childHeight);

		int moveBlankBefore = moveHeightBefore;
		int moveBlankAfter = moveHeightAfter;
		if (movePos != mSrcPos) {
			moveBlankBefore -= childHeight;
			moveBlankAfter -= childHeight;
		}

		int maxBlank = mFloatViewHeight;
		if (mSrcPos != mFirstExpPos && mSrcPos != mSecondExpPos) {
			maxBlank -= mItemHeightCollapsed;
		}

		if (movePos <= oldFirstExpPos) {
			if (movePos > mFirstExpPos) {
				adjust += maxBlank - moveBlankAfter;
			}
		} else if (movePos == oldSecondExpPos) {
			if (movePos <= mFirstExpPos) {
				adjust += moveBlankBefore - maxBlank;
			} else if (movePos == mSecondExpPos) {
				adjust += moveHeightBefore - moveHeightAfter;
			} else {
				adjust += moveBlankBefore;
			}
		} else {
			if (movePos <= mFirstExpPos) {
				adjust -= maxBlank;
			} else if (movePos == mSecondExpPos) {
				adjust -= moveBlankAfter;
			}
		}

		return adjust;
	}

	private void measureItem(View item) {
		ViewGroup.LayoutParams lp = item.getLayoutParams();
		if (lp == null) {
			lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			item.setLayoutParams(lp);
		}
		int wspec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec, getListPaddingLeft() + getListPaddingRight(), lp.width);
		int hspec;
		if (lp.height > 0) {
			hspec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
		} else {
			hspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		item.measure(wspec, hspec);
	}

	private void measureFloatView() {
		if (mFloatView != null) {
			measureItem(mFloatView);
			mFloatViewHeight = mFloatView.getMeasuredHeight();
			mFloatViewHeightHalf = mFloatViewHeight / 2;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mFloatView != null) {
			if (mFloatView.isLayoutRequested()) {
				measureFloatView();
			}
			mFloatViewOnMeasured = true; // set to false after layout
		}
		mWidthMeasureSpec = widthMeasureSpec;
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		if (mFloatView != null) {
			if (mFloatView.isLayoutRequested() && !mFloatViewOnMeasured) {
				measureFloatView();
			}
			mFloatView.layout(0, 0, mFloatView.getMeasuredWidth(), mFloatView.getMeasuredHeight());
			mFloatViewOnMeasured = false;
		}
	}

	protected boolean onDragTouchEvent(MotionEvent ev) {
		// we are in a drag
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_CANCEL:
			if (mDragState == DRAGGING) {
				cancelDrag();
			}
			doActionUpOrCancel();
			break;
		case MotionEvent.ACTION_UP:
			if (mDragState == DRAGGING) {
				stopDrag(0);
			}
			doActionUpOrCancel();
			break;
		case MotionEvent.ACTION_MOVE:
			continueDrag((int) ev.getX(), (int) ev.getY());
			break;
		}

		return true;
	}

	public boolean startDrag(int position, int dragFlags, int deltaX, int deltaY) {
		if (!mInTouchEvent || mFloatViewManager == null) {
			return false;
		}
		View v = mFloatViewManager.onCreateFloatView(position);

		if (v == null) {
			return false;
		} else {
			return startDrag(position, v, dragFlags, deltaX, deltaY);
		}
	}

	public boolean startDrag(int position, View floatView, int dragFlags, int deltaX, int deltaY) {
		if (mDragState != IDLE || !mInTouchEvent || mFloatView != null || floatView == null || !mDragEnabled) {
			return false;
		}
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		int pos = position + getHeaderViewsCount();
		mFirstExpPos = pos;
		mSecondExpPos = pos;
		mSrcPos = pos;
		mFloatPos = pos;

		mDragState = DRAGGING;
		mDragFlags = 0;
		mDragFlags |= dragFlags;

		mFloatView = floatView;
		measureFloatView(); // sets mFloatViewHeight

		mDragDeltaX = deltaX;
		mDragDeltaY = deltaY;

		mFloatLoc.x = mX - mDragDeltaX;
		mFloatLoc.y = mY - mDragDeltaY;

		final View srcItem = getChildAt(mSrcPos - getFirstVisiblePosition());

		if (srcItem != null) {
			srcItem.setVisibility(View.INVISIBLE);
		}
		switch (mCancelMethod) {
		case ON_TOUCH_EVENT:
			super.onTouchEvent(mCancelEvent);
			break;
		case ON_INTERCEPT_TOUCH_EVENT:
			super.onInterceptTouchEvent(mCancelEvent);
			break;
		}
		requestLayout();
		return true;
	}

	private void doDragFloatView(boolean forceInvalidate) {
		int movePos = getFirstVisiblePosition() + getChildCount() / 2;
		View moveItem = getChildAt(getChildCount() / 2);
		if (moveItem == null) {
			return;
		}
		doDragFloatView(movePos, moveItem, forceInvalidate);
	}

	private void doDragFloatView(int movePos, View moveItem, boolean forceInvalidate) {
		mBlockLayoutRequests = true;
		updateFloatView();

		int oldFirstExpPos = mFirstExpPos;
		int oldSecondExpPos = mSecondExpPos;
		boolean updated = updatePositions();

		if (updated) {
			adjustAllItems();
			int scroll = adjustScroll(movePos, moveItem, oldFirstExpPos, oldSecondExpPos);
			setSelectionFromTop(movePos, moveItem.getTop() + scroll - getPaddingTop());
			layoutChildren();
		}
		if (updated || forceInvalidate) {
			invalidate();
		}
		mBlockLayoutRequests = false;
	}

	/**
	 * Sets float View location based on suggested values and constraints set in mDragFlags.
	 */
	private void updateFloatView() {

		if (mFloatViewManager != null) {
			mTouchLoc.set(mX, mY);
		}
		final int floatX = mFloatLoc.x;
		final int floatY = mFloatLoc.y;

		int padLeft = getPaddingLeft();
		if ((mDragFlags & DRAG_POS_X) == 0 && floatX > padLeft) {
			mFloatLoc.x = padLeft;
		} else if ((mDragFlags & DRAG_NEG_X) == 0 && floatX < padLeft) {
			mFloatLoc.x = padLeft;
		}
		final int numHeaders = getHeaderViewsCount();
		final int numFooters = getFooterViewsCount();
		final int firstPos = getFirstVisiblePosition();
		final int lastPos = getLastVisiblePosition();

		int topLimit = getPaddingTop();
		if (firstPos < numHeaders) {
			topLimit = getChildAt(numHeaders - firstPos - 1).getBottom();
		}
		if ((mDragFlags & DRAG_NEG_Y) == 0) {
			if (firstPos <= mSrcPos) {
				topLimit = Math.max(getChildAt(mSrcPos - firstPos).getTop(), topLimit);
			}
		}
		int bottomLimit = getHeight() - getPaddingBottom();
		if (lastPos >= getCount() - numFooters - 1) {
			bottomLimit = getChildAt(getCount() - numFooters - 1 - firstPos).getBottom();
		}
		if ((mDragFlags & DRAG_POS_Y) == 0) {
			if (lastPos >= mSrcPos) {
				bottomLimit = Math.min(getChildAt(mSrcPos - firstPos).getBottom(), bottomLimit);
			}
		}
		if (floatY < topLimit) {
			mFloatLoc.y = topLimit;
		} else if (floatY + mFloatViewHeight > bottomLimit) {
			mFloatLoc.y = bottomLimit - mFloatViewHeight;
		}
		mFloatViewMid = mFloatLoc.y + mFloatViewHeightHalf;
	}

	private void destroyFloatView() {
		if (mFloatView != null) {
			mFloatView.setVisibility(GONE);
			if (mFloatViewManager != null) {
				mFloatViewManager.onDestroyFloatView(mFloatView);
			}
			mFloatView = null;
			invalidate();
		}
	}

	public interface FloatViewManager {
		public View onCreateFloatView(int position);

		public void onDestroyFloatView(View floatView);
	}

	public void setFloatViewManager(FloatViewManager manager) {
		mFloatViewManager = manager;
	}

	public void setDragEnabled(boolean enabled) {
		mDragEnabled = enabled;
	}

	public boolean isDragEnabled() {
		return mDragEnabled;
	}

	public void setDragListener(DragListener l) {
		mDragListener = l;
	}

	public interface DragListener {
		public void drag(int from, int to);
	}

	public void moveCheckState(int from, int to) {
		SparseBooleanArray cip = getCheckedItemPositions();
		int rangeStart = from;
		int rangeEnd = to;
		if (to < from) {
			rangeStart = to;
			rangeEnd = from;
		}
		rangeEnd += 1;

		int[] runStart = new int[cip.size()];
		int[] runEnd = new int[cip.size()];
		int runCount = buildRunList(cip, rangeStart, rangeEnd, runStart, runEnd);
		if (runCount == 1 && (runStart[0] == runEnd[0])) {
			return;
		}
		if (from < to) {
			for (int i = 0; i != runCount; i++) {
				setItemChecked(rotate(runStart[i], -1, rangeStart, rangeEnd), true);
				setItemChecked(rotate(runEnd[i], -1, rangeStart, rangeEnd), false);
			}
		} else {
			for (int i = 0; i != runCount; i++) {
				setItemChecked(runStart[i], false);
				setItemChecked(runEnd[i], true);
			}
		}
	}

	private static int buildRunList(SparseBooleanArray cip, int rangeStart, int rangeEnd, int[] runStart, int[] runEnd) {
		int runCount = 0;

		int i = findFirstSetIndex(cip, rangeStart, rangeEnd);
		if (i == -1)
			return 0;

		int position = cip.keyAt(i);
		int currentRunStart = position;
		int currentRunEnd = currentRunStart + 1;
		for (i++; i < cip.size() && (position = cip.keyAt(i)) < rangeEnd; i++) {
			if (!cip.valueAt(i)) // not checked => not interesting
				continue;
			if (position == currentRunEnd) {
				currentRunEnd++;
			} else {
				runStart[runCount] = currentRunStart;
				runEnd[runCount] = currentRunEnd;
				runCount++;
				currentRunStart = position;
				currentRunEnd = position + 1;
			}
		}

		if (currentRunEnd == rangeEnd) {
			currentRunEnd = rangeStart;
		}
		runStart[runCount] = currentRunStart;
		runEnd[runCount] = currentRunEnd;
		runCount++;

		if (runCount > 1) {
			if (runStart[0] == rangeStart && runEnd[runCount - 1] == rangeStart) {
				runStart[0] = runStart[runCount - 1];
				runCount--;
			}
		}
		return runCount;
	}

	private static int rotate(int value, int offset, int lowerBound, int upperBound) {
		int windowSize = upperBound - lowerBound;

		value += offset;
		if (value < lowerBound) {
			value += windowSize;
		} else if (value >= upperBound) {
			value -= windowSize;
		}
		return value;
	}

	private static int findFirstSetIndex(SparseBooleanArray sba, int rangeStart, int rangeEnd) {
		int size = sba.size();
		int i = insertionIndexForKey(sba, rangeStart);
		while (i < size && sba.keyAt(i) < rangeEnd && !sba.valueAt(i))
			i++;
		if (i == size || sba.keyAt(i) >= rangeEnd)
			return -1;
		return i;
	}

	private static int insertionIndexForKey(SparseBooleanArray sba, int key) {
		int low = 0;
		int high = sba.size();
		while (high - low > 0) {
			int middle = (low + high) >> 1;
			if (sba.keyAt(middle) < key)
				low = middle + 1;
			else
				high = middle;
		}
		return low;
	}

	public interface DragScrollProfile {
		float getSpeed(float w, long t);
	}

	private class DragScroller implements Runnable {
		private boolean mAbort;
		private long mPrevTime;
		private long mCurrTime;

		private int dy;
		private float dt;
		private long tStart;
		private int scrollDir;

		public final static int STOP = -1;
		public final static int UP = 0;
		public final static int DOWN = 1;

		private float mScrollSpeed; // pixels per ms

		private boolean mScrolling = false;

		public boolean isScrolling() {
			return mScrolling;
		}

		public int getScrollDir() {
			return mScrolling ? scrollDir : STOP;
		}

		public DragScroller() {
		}

		public void startScrolling(int dir) {
			if (!mScrolling) {
				mAbort = false;
				mScrolling = true;
				tStart = SystemClock.uptimeMillis();
				mPrevTime = tStart;
				scrollDir = dir;
				post(this);
			}
		}

		public void stopScrolling(boolean now) {
			if (now) {
				SwipeMenuDragSortListView.this.removeCallbacks(this);
				mScrolling = false;
			} else {
				mAbort = true;
			}
		}

		@Override
		public void run() {
			if (mAbort) {
				mScrolling = false;
				return;
			}
			final int first = getFirstVisiblePosition();
			final int last = getLastVisiblePosition();
			final int count = getCount();
			final int padTop = getPaddingTop();
			final int listHeight = getHeight() - padTop - getPaddingBottom();

			int minY = Math.min(mY, mFloatViewMid + mFloatViewHeightHalf);
			int maxY = Math.max(mY, mFloatViewMid - mFloatViewHeightHalf);

			if (scrollDir == UP) {
				View v = getChildAt(0);
				if (v == null) {
					mScrolling = false;
					return;
				} else {
					if (first == 0 && v.getTop() == padTop) {
						mScrolling = false;
						return;
					}
				}
				mScrollSpeed = mScrollProfile.getSpeed((mUpScrollStartYF - maxY) / mDragUpScrollHeight, mPrevTime);
			} else {
				View v = getChildAt(last - first);
				if (v == null) {
					mScrolling = false;
					return;
				} else {
					if (last == count - 1 && v.getBottom() <= listHeight + padTop) {
						mScrolling = false;
						return;
					}
				}
				mScrollSpeed = -mScrollProfile.getSpeed((minY - mDownScrollStartYF) / mDragDownScrollHeight, mPrevTime);
			}

			mCurrTime = SystemClock.uptimeMillis();
			dt = (float) (mCurrTime - mPrevTime);

			dy = (int) Math.round(mScrollSpeed * dt);

			int movePos;
			if (dy >= 0) {
				dy = Math.min(listHeight, dy);
				movePos = first;
			} else {
				dy = Math.max(-listHeight, dy);
				movePos = last;
			}

			final View moveItem = getChildAt(movePos - first);
			int top = moveItem.getTop() + dy;

			if (movePos == 0 && top > padTop) {
				top = padTop;
			}

			// always do scroll
			mBlockLayoutRequests = true;

			setSelectionFromTop(movePos, top - padTop);
			SwipeMenuDragSortListView.this.layoutChildren();
			invalidate();

			mBlockLayoutRequests = false;

			doDragFloatView(movePos, moveItem, false);

			mPrevTime = mCurrTime;
			post(this);
		}
	}
}
