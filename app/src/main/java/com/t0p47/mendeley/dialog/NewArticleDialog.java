package com.t0p47.mendeley.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.t0p47.mendeley.R;
import com.t0p47.mendeley.model.JournalArticle;

/**
 * Created by 01Laptop on 24.10.2017.
 */

public class NewArticleDialog extends DialogFragment {

    EditText etTitle,etAuthors,etAbstract, etJournal, etYear, etVolume, etIssue, etPages, etArXivID, etDOI, etPMID;
    TextView tvTitle, tvAuthros, tvAbstract, tvJournal , tvYear, tvVolume, tvIssue, tvPages, tvArXivID, tvDOI, tvPMID;
    Button btnDone;

    static String DialogBoxTitle;

    public interface NewArticleDialogListener{
        void onFinishNewArticleDialog(JournalArticle article);
    }

    //Set the title of the dialog window
    public void setDialogTitle(String title){
        DialogBoxTitle = title;
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

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewArticleDialogListener activity = (NewArticleDialogListener)getActivity();
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

            }
        });

        getDialog().setTitle(DialogBoxTitle);

        return view;
    }

}
