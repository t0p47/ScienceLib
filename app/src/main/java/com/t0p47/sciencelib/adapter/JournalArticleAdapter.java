package com.t0p47.sciencelib.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.t0p47.sciencelib.R;
import com.t0p47.sciencelib.db.DatabaseHandler;
import com.t0p47.sciencelib.helper.FontManager;
import com.t0p47.sciencelib.model.JournalArticle;

import java.util.List;

/**
 * Created by 01 on 17.10.2017.
 */

public class JournalArticleAdapter extends RecyclerView.Adapter<JournalArticleAdapter.MyViewHolder> {

    private static final String TAG = "LOG_TAG";

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
            favoriteToggle.setTypeface(FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME));
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
    public void onBindViewHolder(final MyViewHolder holder, int position){
        final JournalArticle article = articlesList.get(position);
        holder.title.setText(article.getTitle());
        String authorAndJournal = article.getAuthors()+" в "+article.getJournal();
        holder.tvAuthorAndJournal.setText(authorAndJournal);
        holder.tvCreationDate.setText(article.getCreated_at());
        if(article.getFavorite()==1){
            holder.favoriteToggle.setChecked(true);
        }else{
            holder.favoriteToggle.setChecked(false);
        }
        holder.favoriteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG,"Favorite is "+isChecked);
                DatabaseHandler dbh = new DatabaseHandler(holder.tvCreationDate.getContext());
                if(article.getFavorite()==1){
                    dbh.setFavorite(article.getLocal_id(),0);
                }else{
                    dbh.setFavorite(article.getLocal_id(),1);
                }
            }
        });

    }

    @Override
    public int getItemCount(){
        return articlesList.size();
    }

}
