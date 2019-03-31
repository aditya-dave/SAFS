package com.test.safs.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.test.safs.R;

public class ConfirmPasswordDialog extends AppCompatDialogFragment {


    //variables
    private EditText mPassword;
    private static final String TAG = "ConfirmPasswordDialog";
    private OnConfirmPasswordListener listener;
    public interface OnConfirmPasswordListener{
        void applyTexts(String password);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirm_password,null);
        mPassword = (EditText) view.findViewById(R.id.passwordedittext);


        builder.setView(view)
                .setTitle("Enter Password")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String password = mPassword.getText().toString();
                listener.applyTexts(password);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnConfirmPasswordListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must Implement Confirm Dialog Listener");
        }
    }


}
