package com.t0p47.sciencelib.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.t0p47.sciencelib.R;

/**
 * Created by 01Laptop on 24.10.2017.
 */

public class NewFolderDialog extends DialogFragment {

    private static final String TAG = "LOG_TAG";

    EditText etTitle;
    Button btnDone;
    static String DialogBoxTitle;
    static boolean IsRename;
    static String FolderTitle;

    public interface NewFolderDialogListener{
        void onFinishNewFolderDialog(String inputText);
        void onFinishRenameFolderDialog(String inputText);
    }

    //empty constructor
    public NewFolderDialog(){}



    //Set the title of the dialog window
    public void setDialogTitle(String title){
        DialogBoxTitle = title;
    }

    public void setIsRename(boolean isRename){
        IsRename = isRename;
    }

    public void setFolderTitle(String folderTitle){
        FolderTitle = folderTitle;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        View view = inflater.inflate(R.layout.dialog_add_folder,container);


        etTitle = (EditText) view.findViewById(R.id.etFolderTitle);
        btnDone = (Button) view.findViewById(R.id.btnDialogDone);
        if(IsRename){
            etTitle.setText(FolderTitle);
        }

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewFolderDialogListener activity = (NewFolderDialogListener)getActivity();
                if(!etTitle.getText().toString().isEmpty()){
                    Log.d(TAG,"NewFolderDialog: FolderTitle "+FolderTitle+", renamedValue: "+etTitle.getText().toString()+", are they equals: "+etTitle.getText().toString().equals(FolderTitle));
                    if(IsRename && !etTitle.getText().toString().equals(FolderTitle)){
                        Toast.makeText(getActivity().getApplicationContext(), "Rename folder", Toast.LENGTH_SHORT).show();
                        activity.onFinishRenameFolderDialog(etTitle.getText().toString());
                        dismiss();
                    }else if(!IsRename){
                        Toast.makeText(getActivity().getApplicationContext(), "New folder", Toast.LENGTH_SHORT).show();
                        activity.onFinishNewFolderDialog(etTitle.getText().toString());
                        dismiss();
                    }else if(IsRename && etTitle.getText().toString().equals(FolderTitle)){
                        Toast.makeText(getActivity().getApplicationContext(), "You don't change the title", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Field title is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        etTitle.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        getDialog().setTitle(DialogBoxTitle);

        return view;
    }

}
