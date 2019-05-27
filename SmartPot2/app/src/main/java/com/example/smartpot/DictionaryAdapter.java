package com.example.smartpot;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class DictionaryAdapter extends BaseAdapter {

    private Context context;
    private List<Dictionary> dictionaries;

    public DictionaryAdapter(Context context, List<Dictionary> dictionaries) {
        this.context = context;
        this.dictionaries = dictionaries;
    }

    @Override
    public int getCount() {
        return dictionaries.size();
    }

    @Override
    public Object getItem(int position) {
        return dictionaries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        View view = convertView;
        final ViewHolder holder;

        if(view == null)    {
            view = View.inflate(context, R.layout.dictionary, null);
            holder = new ViewHolder();

            holder.name = (TextView) view.findViewById(R.id.nameText);
            holder.image = (ImageView) view.findViewById(R.id.imageV);
            holder.content = (TextView) view.findViewById(R.id.contentText);
            holder.hideLinear  = (LinearLayout) view.findViewById(R.id.hideLinear);
            view.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        String url = dictionaries.get(position).getImage();
        if("존재하지 않는 이미지".equals(url)){
            holder.image.setBackgroundResource(R.drawable.ic_clear_black_24dp);
        } else {
            Glide.with(holder.image).load(dictionaries.get(position).getImage()).apply(RequestOptions.circleCropTransform()).into(holder.image);
        }
        final ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        holder.name.setText(dictionaries.get(position).getName());
        holder.content.setText(dictionaries.get(position).getContent());
        holder.hideLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.content.getVisibility() == View.GONE) {
                    holder.content.setVisibility(View.VISIBLE);
                    // content 클릭 시 화살표 이미지 변경
                    imageView.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else {
                    holder.content.setVisibility(View.GONE);
                    // content 클릭 시 화살표 이미지 변경
                    imageView.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);

                }
            }
        });


        return view;
    }




    static class ViewHolder {
        TextView name;
        ImageView image;
        TextView content;
        LinearLayout hideLinear;
    }
}
