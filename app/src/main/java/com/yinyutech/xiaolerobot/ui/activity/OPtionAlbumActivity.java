package com.yinyutech.xiaolerobot.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.adapter.GalleryAdapter;
import com.yinyutech.xiaolerobot.adapter.GalleryAdapter.OnItemClickLitener;
import com.yinyutech.xiaolerobot.adapter.MyRecyclerView;
import com.yinyutech.xiaolerobot.adapter.MyRecyclerView.OnItemScrollChangeListener;

import java.io.File;
import java.util.List;
import java.util.Vector;

import static com.yinyutech.xiaolerobot.utils.FileUtil.getSD;

/**
 * Created by yinyu-tiejiang on 17-9-8.
 */

public class OPtionAlbumActivity extends Activity {


    private MyRecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
//    private List<Integer> mDatas;
    private ImageView mImg ;
    private Button mButtonReturn;
    private ProgressBar mProgressBar;
    private List<String> imageList;
    private Vector<String> mWaitDeleteImage = new Vector<String>();
    private Handler mScanSDcardHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Log.d("TIEJIANG", "SCAN SDCARD FAILED");
                    break;
                case 1:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mImg.setVisibility(View.VISIBLE);

                    mAdapter = new GalleryAdapter(getApplicationContext(), imageList);
                    mRecyclerView.setAdapter(mAdapter);

                    mRecyclerView.setOnItemScrollChangeListener(new OnItemScrollChangeListener()
                    {
                        @Override
                        public void onChange(View view, int position)
                        {
                            if (!imageList.isEmpty()){
                                Bitmap bitmap = BitmapFactory.decodeFile(imageList.get(position));
                                mImg.setImageBitmap(bitmap);
                            }else {
                                mImg.setImageResource(R.drawable.xiaole_image_down_failed);
                            }

                        }
                    });

                    mAdapter.setOnItemClickLitener(new OnItemClickLitener()
                    {
                        @Override
                        public void onItemClick(View view, int position)
                        {
                            if (!imageList.isEmpty()){
                                Bitmap bitmap = BitmapFactory.decodeFile(imageList.get(position));
                                mImg.setImageBitmap(bitmap);
                            }else {
                                mImg.setImageResource(R.drawable.xiaole_image_down_failed);
                            }
                        }
                    });

                    //long click event --- delete image
                    mAdapter.setmOnItemLongClickListener(new GalleryAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(View view, int position) {
                            Log.d("TIEJIANG", "OptionAlbumActivity---LongClick"+" position= "+position);
                            showDeletConfirm(position);
                        }
                    });
                    break;

            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_album);

        initImageSource();

        mImg = (ImageView) findViewById(R.id.id_content);
        mImg.setVisibility(View.INVISIBLE);
        mButtonReturn = (Button)findViewById(R.id.album_return);

        mProgressBar = (ProgressBar) findViewById(R.id.scan_sdcard_progressBar);
        mRecyclerView = (MyRecyclerView) findViewById(R.id.id_recyclerview_horizontal);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mButtonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mIntent = new Intent(OPtionAlbumActivity.this, OptionFragment.class);
//                startActivity(mIntent);
                finish();
            }
        });

//        mAdapter = new GalleryAdapter(this, imageList);
//        mRecyclerView.setAdapter(mAdapter);
//
//        mRecyclerView.setOnItemScrollChangeListener(new OnItemScrollChangeListener()
//        {
//            @Override
//            public void onChange(View view, int position)
//            {
////                mImg.setImageResource(imageList.get(position));
//                Bitmap bitmap = BitmapFactory.decodeFile(imageList.get(position));
//                mImg.setImageBitmap(bitmap);
//            }
//        });
//
//        mAdapter.setOnItemClickLitener(new OnItemClickLitener()
//        {
//            @Override
//            public void onItemClick(View view, int position)
//            {
////				Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT)
////						.show();
////                mImg.setImageResource(imageList.get(position));
//                Bitmap bitmap = BitmapFactory.decodeFile(imageList.get(position));
//                mImg.setImageBitmap(bitmap);
//            }
//        });
//
//        //long click event --- delete image
//        mAdapter.setmOnItemLongClickListener(new GalleryAdapter.OnItemLongClickListener() {
//            @Override
//            public void onItemLongClick(View view, int position) {
//                Log.d("TIEJIANG", "OptionAlbumActivity---LongClick"+" position= "+position);
//                showDeletConfirm(position);
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mWaitDeleteImage.isEmpty()){
            deleteImageFile(mWaitDeleteImage);
            mWaitDeleteImage.removeAllElements();
        }
    }

    /**
     * function: show whether delete the checked image or not
     *
     * */
    private void showDeletConfirm(final int position){

        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("确认删除?");
        mDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String str = imageList.get(position);
//                String str = mAdapter.mDatas.get(position);
                mWaitDeleteImage.add(str); //add image file path to vector to delete
                imageList.remove(position);
                //test code begin
//                for (String image:imageList){
//                    Log.d("TIEJIANG", "OPtionAlbumActivity---showDeleteConfirm"+" imageList= " + image);
//                }
//                for (String imageDatas:mAdapter.mDatas){
//                    Log.d("TIEJIANG", "OPtionAlbumActivity---showDeleteConfirm"+" imageDatas= " + imageDatas);
//                }
                //test code end
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(0, mAdapter.mDatas.size());

//                mAdapter.notifyDataSetChanged();  //use this to refresh data ok ,but without animation
                //delete from sdcard
//
            }
        });
        mDialog.create();
        mDialog.show();
    }


    /**
     * function: delete imagefile(s) in the list when destroy activity
     *
     * */
    public void deleteImageFile(Vector<String> image_vector){

        if (!image_vector.isEmpty()){

            for(String mImageFile:image_vector){

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = this.getContentResolver();
                String where = MediaStore.Images.Media.DATA + "='" + mImageFile + "'";
                //删除图片
                mContentResolver.delete(mImageUri, where, null);

                //发送广播
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File file = new File(mImageFile);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                OPtionAlbumActivity.this.sendBroadcast(intent);
            }
        }
    }

    public void initImageSource(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                imageList = getSD();
                if (imageList.isEmpty()){
                    mScanSDcardHandler.obtainMessage(0, "scan_failed").sendToTarget();
                }else {
                    mScanSDcardHandler.obtainMessage(1, "scan_over").sendToTarget();
                }
            }
        }).start();


//        for (int i=0; i<imageList.size(); i++){
//            Log.d("TIEJIANG", "OptionAlbumActivity---List<String>"+imageList.get(i));
//        }
    }
}
