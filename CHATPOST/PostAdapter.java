package com.example.ioc.CHATPOST;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ioc.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyHolder> {

    Context context;
    List<PostModel> postModelList;

    public PostAdapter(final Context context, final List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_row_list, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        PostModel postion = postModelList.get(position);
        String title = postModelList.get(position).getpTitle();
        String Description = postModelList.get(position).getpDescription();
        String Email = postModelList.get(position).getuEmail();
        String image = postModelList.get(position).getpImage();
        holder.post_tit.setText(title);
        holder.post_name.setText(Email);
        holder.post_description.setText(Description);

        Glide.with(context).load(image).into(holder.post_image);

    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView post_image;
        TextView post_tit, post_description, post_name;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_Image);
            post_description = itemView.findViewById(R.id.post_text);
            post_name = itemView.findViewById(R.id.post_username);
            post_tit = itemView.findViewById(R.id.post_Title);
        }
    }
}
