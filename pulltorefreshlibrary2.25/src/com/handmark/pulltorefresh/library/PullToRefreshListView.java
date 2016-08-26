/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class PullToRefreshListView extends
		PullToRefreshAdapterViewBase<ListView> {

	private LoadingLayout mHeaderLoadingView;
	private LoadingLayout mFooterLoadingView;

	private FrameLayout mLvFooterLoadingFrame;

	private boolean mListViewExtrasEnabled;
	
	private boolean mRequtest = false;
	private boolean mIsWifi = false;
	private boolean mIsLoadingMore = false;
	private boolean mLoadMore = false;
	private int mScrollState = SCROLL_STATE_IDLE;

	public PullToRefreshListView(Context context) {
		super(context);
		init(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullToRefreshListView(Context context, Mode mode) {
		super(context, mode);
		init(context);
	}

	public PullToRefreshListView(Context context, Mode mode,
			AnimationStyle style) {
		super(context, mode, style);
		init(context);
	}
	
	private void init(Context context) {
		//setOnScrollListener(this);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected void onRefreshing(final boolean doScroll) {
		/**
		 * If we're not showing the Refreshing view, or the list is empty, the
		 * the header/footer views won't show so we use the normal method.
		 */
		if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || islastItemLow()) {
			Log.v(LOG_TAG, "ListView onRefreshing...");
			super.onRefreshing(doScroll);
			return;
		}

		final LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
		final int selection, scrollToY;

		switch (getCurrentMode()) {
		case MANUAL_REFRESH_ONLY:
		case PULL_FROM_END:
			origLoadingView = getFooterLayout();
			listViewLoadingView = mFooterLoadingView;
			oppositeListViewLoadingView = mHeaderLoadingView;
			selection = mRefreshableView.getCount() - 1;
			scrollToY = getScrollY() - getFooterSize();
			break;
		case PULL_FROM_START:
		default:
			origLoadingView = getHeaderLayout();
			listViewLoadingView = mHeaderLoadingView;
			oppositeListViewLoadingView = mFooterLoadingView;
			selection = 0;
			scrollToY = getScrollY() + getHeaderSize();
			break;
		}


		// Show the ListView Loading View and set it to refresh.
		listViewLoadingView.setVisibility(View.VISIBLE);
		listViewLoadingView.refreshing();
		
		// call up to super
		super.onRefreshing(false);
		
		// Hide our original Loading View
		origLoadingView.reset();
		origLoadingView.hideAllViews();

		// Make sure the opposite end is hidden too
		oppositeListViewLoadingView.setVisibility(View.GONE);

		if (doScroll) {
			// We need to disable the automatic visibility changes for now
			disableLoadingLayoutVisibilityChanges();

			// We scroll slightly so that the ListView's header/footer is at the
			// same Y position as our normal header/footer
			setHeaderScroll(scrollToY);

			// Make sure the ListView is scrolled to show the loading
			// header/footer
			mRefreshableView.setSelection(selection);

			// Smooth scroll as normal
			smoothScrollTo(0);
		}
		
	}

	@Override
	protected void onReset() {
		/**
		 * If the extras are not enabled, just call up to super and return.
		 */
		if (!mListViewExtrasEnabled || islastItemLow()) {
			Log.v(LOG_TAG, "ListView onReset...");
			super.onReset();
			return;
		}

		final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
		final int scrollToHeight, selection;
		final boolean scrollLvToEdge;

		switch (getCurrentMode()) {
		case MANUAL_REFRESH_ONLY:
		case PULL_FROM_END:
			originalLoadingLayout = getFooterLayout();
			listViewLoadingLayout = mFooterLoadingView;
			selection = mRefreshableView.getCount() - 1;
			scrollToHeight = getFooterSize();
			scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition()
					- selection) <= 1;
			break;
		case PULL_FROM_START:
		default:
			originalLoadingLayout = getHeaderLayout();
			listViewLoadingLayout = mHeaderLoadingView;
			scrollToHeight = -getHeaderSize();
			selection = 0;
			scrollLvToEdge = Math.abs(mRefreshableView
					.getFirstVisiblePosition() - selection) <= 1;
			break;
		}

		// If the ListView header loading layout is showing, then we need to
		// flip so that the original one is showing instead
		if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {

			// Set our Original View to Visible
			originalLoadingLayout.showInvisibleViews();

			// Hide the ListView Header/Footer
			listViewLoadingLayout.setVisibility(View.GONE);

			/**
			 * Scroll so the View is at the same Y as the ListView
			 * header/footer, but only scroll if: we've pulled to refresh, it's
			 * positioned correctly
			 */
			if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
				mRefreshableView.setSelection(selection);
				setHeaderScroll(scrollToHeight);
			}
		}

		// Finally, call up to super
		super.onReset();
	}

	@Override
	protected LoadingLayoutProxy createLoadingLayoutProxy(
			final boolean includeStart, final boolean includeEnd) {
		LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart,
				includeEnd);

		if (mListViewExtrasEnabled) {
			final Mode mode = getMode();

			if (includeStart && mode.showHeaderLoadingLayout()) {
				proxy.addLayout(mHeaderLoadingView);
			}
			if (includeEnd && mode.showFooterLoadingLayout()) {
				proxy.addLayout(mFooterLoadingView);
			}
		}

		return proxy;
	}

	protected ListView createListView(Context context, AttributeSet attrs) {
		final ListView lv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			lv = new InternalListViewSDK9(context, attrs);
		} else {
			lv = new InternalListView(context, attrs);
		}
		return lv;
	}

	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		ListView lv = createListView(context, attrs);

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	@Override
	protected void handleStyledAttributes(TypedArray a) {
		super.handleStyledAttributes(a);

		mListViewExtrasEnabled = a.getBoolean(
				R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

		if (mListViewExtrasEnabled) {
			final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT,
					Gravity.CENTER_HORIZONTAL);

			// Create Loading Views ready for use later
			FrameLayout frame = new FrameLayout(getContext());
			mHeaderLoadingView = createLoadingLayout(getContext(),
					Mode.PULL_FROM_START, a);
			mHeaderLoadingView.setVisibility(View.GONE);
			frame.addView(mHeaderLoadingView, lp);
			mRefreshableView.addHeaderView(frame, null, false);

			mLvFooterLoadingFrame = new FrameLayout(getContext());
			mFooterLoadingView = createLoadingLayout(getContext(),
					Mode.PULL_FROM_END, a);
			mFooterLoadingView.setVisibility(View.GONE);
			mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

			/**
			 * If the value for Scrolling While Refreshing hasn't been
			 * explicitly set via XML, enable Scrolling While Refreshing.
			 */
			if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
				setScrollingWhileRefreshingEnabled(true);
			}
		}
	}

	@TargetApi(9)
	final class InternalListViewSDK9 extends InternalListView {

		public InternalListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY,
					scrollX, scrollY, scrollRangeX, scrollRangeY,
					maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			// OverscrollHelper.overScrollBy(PullToRefreshListView.this, deltaX,
			// scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}

	protected class InternalListView extends ListView implements
			EmptyViewMethodAccessor {

		private boolean mAddedLvFooter = false;
		private MotionEvent mEv;
		private int mTouchSlop;
		private boolean mCanDrag = false;

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
			ViewConfiguration config = ViewConfiguration.get(context);
			mTouchSlop = config.getScaledTouchSlop();
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				super.dispatchDraw(canvas);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				return super.dispatchTouchEvent(ev);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {

			final Mode mode = getMode();
			
			if(mode == Mode.DISABLED) 
				return super.onTouchEvent(ev); 
			
			if(isRefreshing()) {
				return super.onTouchEvent(ev); 
			}
			
			final int action = ev.getAction();
			final ViewGroup vg = (ViewGroup) this.getParent().getParent();

			if (action == MotionEvent.ACTION_DOWN) {
				mCanDrag = false;
				mEv = MotionEvent.obtain(ev);
				return super.onTouchEvent(ev);
			} else if (action == MotionEvent.ACTION_MOVE) {

				if (mCanDrag) {
					vg.onInterceptTouchEvent(ev);
					vg.onTouchEvent(ev);
					return true;
				}

				if (mEv != null) {
					float diffY = ev.getY() - mEv.getY();

					if (Math.abs(diffY) <= mTouchSlop) {
						return super.onTouchEvent(ev);
					} 

					final int lastItemPosition = mRefreshableView.getCount() - 1;		

					if (diffY > 0 && (getFirstVisiblePosition() == 0)) {
						if(mode == Mode.PULL_FROM_END) return super.onTouchEvent(ev); 
						
						if (!mCanDrag) {
							vg.onInterceptTouchEvent(mEv);
						}
						vg.onInterceptTouchEvent(ev);
						vg.onTouchEvent(ev);
						mCanDrag = true;
						return true;
					} else if (diffY < 0
							&& (getLastVisiblePosition() == lastItemPosition)) {
						if(mode == Mode.PULL_FROM_START) return super.onTouchEvent(ev); 
						if(mIsWifi) return super.onTouchEvent(ev); 
						
						if (!mCanDrag) {
							vg.onInterceptTouchEvent(mEv);
						}
						vg.onInterceptTouchEvent(ev);
						vg.onTouchEvent(ev);
						mCanDrag = true;
						return true;
					}

					mEv = MotionEvent.obtain(ev);
					mEv.setAction(MotionEvent.ACTION_DOWN);

				} else {

					mEv = MotionEvent.obtain(ev);
					mEv.setAction(MotionEvent.ACTION_DOWN);

				}
			} else if (action == MotionEvent.ACTION_CANCEL
					|| action == MotionEvent.ACTION_UP) {
				if (mEv != null) {
					mEv.recycle();
					mEv = null;
				}
				if (mCanDrag) {
					vg.onTouchEvent(ev);
					mCanDrag = false;
					return true;
				}
			}

			return super.onTouchEvent(ev);
		}

		@Override
		public void setAdapter(ListAdapter adapter) {
			// Add the Footer View at the last possible moment
			if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
				addFooterView(mLvFooterLoadingFrame, null, false);
				mAddedLvFooter = true;
			}
			
			super.setAdapter(adapter);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		mLoadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
		if(mScrollState == SCROLL_STATE_TOUCH_SCROLL && !mRequtest) {
			if(mRequtest) return;
			mRequtest = true;
			if(mIsLoadingMore) return;
			if(isRefreshing()) return;
			
			mIsWifi = false; 
			final Mode mode = getMode();
			if(mode == Mode.PULL_FROM_END || mode == Mode.BOTH) {
				if(isWifiState()) {
					mIsWifi = !isReadyForPullEnd();
				}
			}
			
			Log.v(LOG_TAG, "onScroll mIsWifi:" + mIsWifi);
			if(mIsWifi) {
				mFooterLoadingView.setVisibility(View.VISIBLE);
				mFooterLoadingView.refreshing();
			} else {
				mFooterLoadingView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int state) {
		super.onScrollStateChanged(view, state);		
		Log.v(LOG_TAG, "onScrollStateChanged:" + state);
		mScrollState = state;
		
		if(state != SCROLL_STATE_IDLE) return;
		mRequtest = false;
		
		if(!mIsWifi) return;
		
		if(!mLoadMore) {
			Log.v(LOG_TAG, "onScrollStateChanged mLoadMore:" + mLoadMore);
			if(!isRefreshing()) {
				mFooterLoadingView.setVisibility(View.GONE);
			}	
			return;
		}
		
		if(mIsLoadingMore) {
			Log.v(LOG_TAG, "onScrollStateChanged mIsLoadingMore:" + mIsLoadingMore);
			return;
		}
		
		Log.v(LOG_TAG, "state:" + getState());
		if(isRefreshing()) {
			if(mHeaderLoadingView.getVisibility() == View.VISIBLE) {
				mFooterLoadingView.setVisibility(View.GONE);
			}
			return;
		}	
		
		mIsLoadingMore = true;
		onLoadMore();	
	}
	
	public void onLoadMore() {
		Log.v(LOG_TAG, "onLoadMore...");
		setCurrentMode(Mode.PULL_FROM_END);
		setRefreshing(false);
		mIsWifi = false;
	}

	@Override
	public void onRefreshComplete() {
		super.onRefreshComplete();
		Log.v(LOG_TAG, "onRefreshComplete...");
		mIsLoadingMore = false;
	}
	
	private boolean islastItemLow() {
		final Adapter adapter = mRefreshableView.getAdapter();

		if (null == adapter || adapter.isEmpty()) {
			if (DEBUG) {
				Log.d(LOG_TAG, "isLastItemVisible. Empty View.");
			}
			return true;
		} else {
			final int lastItemPosition = mRefreshableView.getCount() - 1;
			final int lastVisiblePosition = mRefreshableView
					.getLastVisiblePosition();

			if (DEBUG) {
				Log.d(LOG_TAG, "isLastItemVisible. Last Item Position: "
						+ lastItemPosition + " Last Visible Pos: "
						+ lastVisiblePosition);
			}

			/**
			 * This check should really just be: lastVisiblePosition ==
			 * lastItemPosition, but PtRListView internally uses a FooterView
			 * which messes the positions up. For me we'll just subtract one to
			 * account for it and rely on the inner condition which checks
			
			 * getBottom().
			 */

			if (lastVisiblePosition >= lastItemPosition - 1) {
				final int childIndex = lastVisiblePosition
							- mRefreshableView.getFirstVisiblePosition();
				final View lastVisibleChild = mRefreshableView
						.getChildAt(childIndex);
				if (lastVisibleChild != null) {
					return mRefreshableView.getHeight() - lastVisibleChild.getBottom() > 50;
				}
			}

			return false;
		}
	}

}
