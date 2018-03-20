package com.mfast.square.ordercollection;

import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScrollManager implements AbsListView.OnScrollListener {

	private boolean scrolled = false;
	private BaseAdapter mAdapter;
	private TextView tv = null;
	private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
	
//	public ScrollManager(CursorAdapter ad){
//		mAdapter = ad;
//	}
	public ScrollManager(BaseAdapter ad)
	{
		mAdapter = ad;
	}
	public ScrollManager(BaseAdapter ad,TextView tv)
	{
		mAdapter = ad;
		this.tv = tv;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) 
	{
		if(tv!=null)
		{
			tv.setText(firstVisibleItem+":"+totalItemCount);
		}
		//System.out.println("First Visible Index : "+firstVisibleItem);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) 
	{
		if (scrollState == SCROLL_STATE_FLING) 
		{
			scrolled = true;
		} 
		else if (scrollState == SCROLL_STATE_IDLE && mScrollState == SCROLL_STATE_FLING) 
		{
			if (scrolled) 
			{
				scrolled = false;
				mAdapter.notifyDataSetChanged();
			}
		}
		mScrollState = scrollState;
	}
	
	public int getScrollState()
	{
		return mScrollState;
	}
}