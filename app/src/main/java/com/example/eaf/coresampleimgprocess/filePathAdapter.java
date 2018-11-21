package com.example.eaf.coresampleimgprocess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class filePathAdapter extends BaseAdapter {

        private Context mContext;
        private List<String> mList = new ArrayList<>();
        //构造函数
        public filePathAdapter(Context context, List<String> list) {
            mContext = context;
            mList = list;
        }
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.filepath_item, null);
                viewHolder.filePathText=view.findViewById(R.id.filepath_Text);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.filePathText.setText(mList.get(i));
            return view;
        }

        class ViewHolder {
            TextView filePathText;
        }

        public void clear(){

        }
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

    }

