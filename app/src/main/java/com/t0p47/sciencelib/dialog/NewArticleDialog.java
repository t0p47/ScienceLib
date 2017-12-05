package com.t0p47.sciencelib.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.t0p47.sciencelib.R;
import com.t0p47.sciencelib.model.JournalArticle;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 01Laptop on 24.10.2017.
 */

public class NewArticleDialog extends DialogFragment {

    private static final String TAG = "LOG_TAG";

    EditText etTitle,etAuthors,etAbstract, etJournal, etYear, etVolume, etIssue, etPages, etArXivID, etDOI, etPMID;
    TextView tvTitle, tvAuthors, tvAbstract, tvJournal , tvYear, tvVolume, tvIssue, tvPages, tvArXivID, tvDOI, tvPMID;
    Button btnDone;
    boolean isEdit = false;
    JournalArticle receivedArticle;
    int receivedListPosition;

    static String DialogBoxTitle;

    public interface NewArticleDialogListener{
        void onFinishNewArticleDialog(JournalArticle article);

        void onFinishEditArticleDialog(int listPosition, JournalArticle article);
    }

    //Set the title of the dialog window
    public void setDialogTitle(String title){
        DialogBoxTitle = title;
    }

    public void setArticleDetails(int listPosition, JournalArticle article){
        isEdit = true;
        receivedArticle = article;
        receivedListPosition = listPosition;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.dialog_add_article, container);

        //Get EditText's and button
        etTitle = (EditText) view.findViewById(R.id.etTitle);
        etAuthors = (EditText) view.findViewById(R.id.etAuthors);
        etAbstract = (EditText) view.findViewById(R.id.etAbstract);
        etJournal = (EditText) view.findViewById(R.id.etJournal);
        etYear = (EditText) view.findViewById(R.id.etYear);
        etVolume = (EditText) view.findViewById(R.id.etVolume);
        etIssue = (EditText) view.findViewById(R.id.etIssue);
        etPages = (EditText) view.findViewById(R.id.etPages);
        etArXivID = (EditText) view.findViewById(R.id.etArXivID);
        etDOI = (EditText) view.findViewById(R.id.etDOI);
        etPMID = (EditText) view.findViewById(R.id.etPMID);

        btnDone = (Button) view.findViewById(R.id.btnDialogDone);

        if(isEdit){
            etTitle.setText(receivedArticle.getTitle());
            etAuthors.setText(receivedArticle.getAuthors());
            etAbstract.setText(receivedArticle.getAbstractField());
            etJournal.setText(receivedArticle.getJournal());
            etYear.setText(String.valueOf(receivedArticle.getYear()));
            etVolume.setText(String.valueOf(receivedArticle.getVolume()));
            etIssue.setText(String.valueOf(receivedArticle.getIssue()));
            etPages.setText(String.valueOf(receivedArticle.getPages()));
            etArXivID.setText(String.valueOf(receivedArticle.getArXivID()));
            etDOI.setText(String.valueOf(receivedArticle.getDOI()));
            etPMID.setText(String.valueOf(receivedArticle.getPMID()));
        }

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewArticleDialogListener activity = (NewArticleDialogListener)getActivity();
                if(!isEdit){
                    if(!etTitle.getText().toString().isEmpty() && !etAuthors.getText().toString().isEmpty() && !etJournal.getText().toString().isEmpty()){
                        //String title, String authors, String abstractField, String journal, int volume, int issue,
                        // int year, int pages, int arXivID, int DOI, int PMID, int folder, String created_at
                        String title = etTitle.getText().toString();
                        String authors = etAuthors.getText().toString();
                        String abstractField = etAbstract.getText().toString();
                        String journal = etJournal.getText().toString();

                        int volume = 0;
                        if(!etVolume.getText().toString().isEmpty()){
                            volume = Integer.parseInt(etVolume.getText().toString());
                        }

                        int issue = 0;
                        if(!etIssue.getText().toString().isEmpty()){
                            issue = Integer.parseInt(etIssue.getText().toString());
                        }

                        int year = 0;
                        if(!etYear.getText().toString().isEmpty()){
                            year = Integer.parseInt(etYear.getText().toString());
                        }

                        int pages = 0;
                        if(!etPages.getText().toString().isEmpty()){
                            pages = Integer.parseInt(etPages.getText().toString());
                        }

                        int ArXivID = 0;
                        if(!etArXivID.getText().toString().isEmpty()){
                            ArXivID = Integer.parseInt(etArXivID.getText().toString());
                        }

                        int DOI = 0;
                        if(!etDOI.getText().toString().isEmpty()){
                            DOI = Integer.parseInt(etDOI.getText().toString());
                        }

                        int PMID = 0;
                        if(!etPMID.getText().toString().isEmpty()){
                            PMID = Integer.parseInt(etPMID.getText().toString());
                        }

                        String filePath = null;

                        JournalArticle article = new JournalArticle(title,authors,abstractField,journal,volume,issue,year,pages,ArXivID,DOI,PMID, filePath);
                        activity.onFinishNewArticleDialog(article);


                        dismiss();
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "Required fields is empty",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(!receivedArticle.getTitle().equals(etTitle.getText()) || !receivedArticle.getAuthors().equals(etAuthors.getText()) || !receivedArticle.getAbstractField().equals(etAbstract.getText()) ||
                            !receivedArticle.getJournal().equals(etJournal.getText()) || !String.valueOf(receivedArticle.getYear()).equals(etYear.getText()) || !String.valueOf(receivedArticle.getVolume()).equals(etVolume.getText()) ||
                            !String.valueOf(receivedArticle.getIssue()).equals(etIssue.getText()) || !String.valueOf(receivedArticle.getPages()).equals(etPages.getText()) || !String.valueOf(receivedArticle.getArXivID()).equals(etArXivID.getText()) ||
                            !String.valueOf(receivedArticle.getDOI()).equals(etDOI.getText()) || !String.valueOf(receivedArticle.getPMID()).equals(etTitle.getText())){
                        Log.d(TAG,"NewArticleDialog: title changed");

                        String title = etTitle.getText().toString();
                        String authors = etAuthors.getText().toString();
                        String abstractField = etAbstract.getText().toString();
                        String journal = etJournal.getText().toString();

                        int volume = 0;
                        if(!etVolume.getText().toString().isEmpty()){
                            volume = Integer.parseInt(etVolume.getText().toString());
                        }

                        int issue = 0;
                        if(!etIssue.getText().toString().isEmpty()){
                            issue = Integer.parseInt(etIssue.getText().toString());
                        }

                        int year = 0;
                        if(!etYear.getText().toString().isEmpty()){
                            year = Integer.parseInt(etYear.getText().toString());
                        }

                        int pages = 0;
                        if(!etPages.getText().toString().isEmpty()){
                            pages = Integer.parseInt(etPages.getText().toString());
                        }

                        int ArXivID = 0;
                        if(!etArXivID.getText().toString().isEmpty()){
                            ArXivID = Integer.parseInt(etArXivID.getText().toString());
                        }

                        int DOI = 0;
                        if(!etDOI.getText().toString().isEmpty()){
                            DOI = Integer.parseInt(etDOI.getText().toString());
                        }

                        int PMID = 0;
                        if(!etPMID.getText().toString().isEmpty()){
                            PMID = Integer.parseInt(etPMID.getText().toString());
                        }

                        String filePath = null;

                        JournalArticle article = new JournalArticle(receivedArticle.getLocal_id(), title,authors,abstractField,journal,volume,issue,year,pages,ArXivID,DOI,PMID, filePath);

                        /*Date date = new Date();

                        //PHP: 2017-09-28 06:57:34
                        //JAVA: Tue Oct 24 16:41:50 GMT+07:00 2017
                        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = dateFormat.format(date);
                        Log.d(TAG,"MainActivity: current date: "+formattedDate);
                        article.setCreated_at(formattedDate);*/

                        Date date = new Date();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = dateFormat.format(date);
                        article.setUpdated_at(formattedDate);

                        activity.onFinishEditArticleDialog(receivedListPosition, article);

                        dismiss();

                    }
                }

            }
        });

        getDialog().setTitle(DialogBoxTitle);

        return view;
    }

}
