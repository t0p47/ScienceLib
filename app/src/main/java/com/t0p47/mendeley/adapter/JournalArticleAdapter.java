package com.t0p47.mendeley.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.t0p47.mendeley.R;
import com.t0p47.mendeley.model.JournalArticle;

import java.util.List;

/**
 * Created by 01 on 17.10.2017.
 */

public class JournalArticleAdapter extends RecyclerView.Adapter<JournalArticleAdapter.MyViewHolder> {

    private List<JournalArticle> articlesList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, tvAuthorAndJournal;
        public TextView tvCreationDate;
        public ToggleButton favoriteToggle;

        public MyViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.tvTitle);
            tvAuthorAndJournal = (TextView) view.findViewById(R.id.tvAuthorAndJournal);
            tvCreationDate = (TextView) view.findViewById(R.id.tvCreateDate);
            favoriteToggle = (ToggleButton) view.findViewById(R.id.favoriteToggle);
        }
    }

    public JournalArticleAdapter(List<JournalArticle> articlesList){
        this.articlesList = articlesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_list_row,parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        JournalArticle article = articlesList.get(position);
        holder.title.setText(article.getTitle());
        String authorAndJournal = article.getAuthors()+" в "+article.getJournal();
        holder.tvAuthorAndJournal.setText(authorAndJournal);
        holder.tvCreationDate.setText(article.getCreated_at());
    }

    @Override
    public int getItemCount(){
        return articlesList.size();
    }

}
