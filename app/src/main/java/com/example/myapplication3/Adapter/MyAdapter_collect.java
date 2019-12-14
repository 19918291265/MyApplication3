package com.example.myapplication3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication3.NewsShowActivity;
import com.example.myapplication3.R;

import java.util.List;
import java.util.Map;

public class MyAdapter_collect extends RecyclerView.Adapter<MyAdapter_collect.ViewHolder>{

    private List<Map<String, Object>> list;
    private Context context;
    private String news_id;



    public MyAdapter_collect(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }



    @NonNull
    @Override
    public MyAdapter_collect.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);

        return new MyAdapter_collect.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyAdapter_collect.ViewHolder holder, final int position) {
        holder.textView.setText(list.get(position).get("title").toString());

        final String picture = list.get(position).get("image_url").toString();
        final String url = list.get(position).get("url").toString();

//        news_id = list.get(position).get("id").toString();

        String picture_1 = picture.replace("\\","");
        String picture_2 = picture_1.replace("\"","");
        String picture_3 = picture_2.replace("[","");
        String picture_4 = picture_3.replace("]","");


        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.test)
                .error(R.drawable.test)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(this.context)
                .load(picture_4)
                .apply(options)
                .into(holder.picture);

        holder.to_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewsShowActivity.class);

                intent.putExtra("id",news_id);
                intent.putExtra("url",url);
                intent.putExtra("title",list.get(position).get("title").toString());
                intent.putExtra("images",picture);

                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private RelativeLayout to_news;
        private ImageView picture;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_title);
            to_news = itemView.findViewById(R.id.news);
            picture = itemView.findViewById(R.id.picture_show);


        }

    }
}