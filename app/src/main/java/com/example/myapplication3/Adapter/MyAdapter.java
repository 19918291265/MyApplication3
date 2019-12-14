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

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Map<String, Object>> list;
    private Context context;
    private String news_id;
    private int DATE = 0;
    private int NEWS = 1;



    public MyAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType == DATE){
            View view = LayoutInflater.from(context).inflate(R.layout.time_item, parent, false);

            return new ViewHolder_date(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);

            return new ViewHolder_news(view);
        }







    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof ViewHolder_news){

            ViewHolder_news viewHolder_news = (ViewHolder_news) holder;

            viewHolder_news.textView.setText(list.get(position).get("title").toString());
            viewHolder_news.textView2.setText(list.get(position).get("hint").toString());

            final String picture = list.get(position).get("images").toString();

            final String url = list.get(position).get("url").toString();

            news_id = list.get(position).get("news_id").toString();

            String picture_1 = picture.replace("\\","");
            String picture_2 = picture_1.replace("\"","");
            String picture_3 = picture_2.replace("[","");
            String picture_4 = picture_3.replace("]","");

//        Glide.with(context).load(list.get(position).get("images").toString()).into(holder.picture);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.test)
                    .error(R.drawable.test)
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(this.context)
                    .load(picture_4)
                    .apply(options)
                    .into(viewHolder_news.picture);
//        final String shop_id = list.get(position).get("shop_id").toString();
//        final String account_id = list.get(position).get("his").toString();

            viewHolder_news.to_news.setOnClickListener(new View.OnClickListener()
            {
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

        else {
            ViewHolder_date viewHolder_date = (ViewHolder_date) holder;
            viewHolder_date.textView_2.setText(list.get(position).get("date").toString());
        }

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {

        final String check = list.get(position).get("check").toString();

        if (check.equals("0")){
            return DATE;
        }
        else
        {
            return NEWS;
        }
    }

    class ViewHolder_news extends RecyclerView.ViewHolder {
        private TextView textView;
        private TextView textView2;
        private RelativeLayout to_news;
        private ImageView picture;

        ViewHolder_news(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_title);
            to_news = itemView.findViewById(R.id.news);
            picture = itemView.findViewById(R.id.picture_show);
            textView2 = itemView.findViewById(R.id.tv_introduce);

        }

    }



    class ViewHolder_date extends RecyclerView.ViewHolder{
        private TextView textView_2;

        ViewHolder_date(@NonNull View itemView) {
            super(itemView);

            textView_2 = itemView.findViewById(R.id.date);
        }
    }



}