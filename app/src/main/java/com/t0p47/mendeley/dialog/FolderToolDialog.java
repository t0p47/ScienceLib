package com.t0p47.mendeley.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.t0p47.mendeley.R;

/**
 * Created by 01Laptop on 10.11.2017.
 */

public class FolderToolDialog extends DialogFragment {

    Button btnAddFolder, btnRenameFolder, btnDeleteFolder;

    public interface FolderToolDialogListener{
        void onFinishAddFolder();
        void onFinishRenameFolder();
        void onFinishDeleteFolder();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.dialog_folder_tool, container);

        btnAddFolder = (Button) view.findViewById(R.id.btnDialogAddFolder);
        btnRenameFolder = (Button) view.findViewById(R.id.btnDialogRenameFolder);
        btnDeleteFolder = (Button) view.findViewById(R.id.btnDialogDeleteFolder);

        btnAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderToolDialogListener activity = (FolderToolDialogListener)getActivity();
                activity.onFinishAddFolder();

                dismiss();
            }
        });

        btnRenameFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderToolDialogListener activity = (FolderToolDialogListener)getActivity();
                activity.onFinishRenameFolder();

                dismiss();
            }
        });

        btnDeleteFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderToolDialogListener activity = (FolderToolDialogListener)getActivity();
                activity.onFinishDeleteFolder();

                dismiss();
            }
        });

        return view;

    }

}
