package com.yinyutech.xiaolerobot.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yinyutech.xiaolerobot.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>
{

	public interface OnItemClickLitener
	{
		void onItemClick(View view, int position);
	}
	public interface OnItemLongClickListener{
		void onItemLongClick(View view, int position);
	}

	private OnItemClickLitener mOnItemClickLitener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
	{
		this.mOnItemClickLitener = mOnItemClickLitener;
	}

	public void setmOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener){

		this.mOnItemLongClickListener = mOnItemLongClickListener;
	}

	private LayoutInflater mInflater;
//	private List<Integer> mDatas;
	public List<String> mDatas;

//	public GalleryAdapter(Context context, List<Integer> datats)
//	{
//		mInflater = LayoutInflater.from(context);
//		mDatas = datats;
//	}
	public GalleryAdapter(Context context, List<String> datats)
	{
		mInflater = LayoutInflater.from(context);
		mDatas = datats;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder
	{
		public ViewHolder(View arg0)
		{
			super(arg0);
		}

		ImageView mImg;
		TextView mTxt;
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
	{
		View view = mInflater.inflate(R.layout.activity_index_gallery_item,
				viewGroup, false);
		ViewHolder viewHolder = new ViewHolder(view);

		viewHolder.mImg = (ImageView) view.findViewById(R.id.id_index_gallery_item_image);
		viewHolder.mTxt = (TextView)view.findViewById(R.id.id_index_gallery_item_text);

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int i)
	{
//		viewHolder.mImg.setImageResource(mDatas.get(i));
		Bitmap bitmap = BitmapFactory.decodeFile(mDatas.get(i));
		viewHolder.mImg.setImageBitmap(bitmap);

		if (mOnItemClickLitener != null)
		{
			viewHolder.itemView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
				}
			});
		}

		if (mOnItemLongClickListener != null){
			viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
//					mDatas.remove(viewHolder.getPosition());
////					notifyDataSetChanged();
//					notifyItemRemoved(viewHolder.getPosition());
//					notifyItemRangeChanged(0, mDatas.size());
////					if (i != mDatas.size()){
////						notifyItemRangeChanged(i, mDatas.size()-i);
////					}
					int position = viewHolder.getPosition();
					mOnItemLongClickListener.onItemLongClick(viewHolder.itemView, position);
					return false;
				}
			});
		}

	}

}
