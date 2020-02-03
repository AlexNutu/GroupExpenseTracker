package com.example.expensetracker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.expensetracker.R;

public class DemoDialog extends AppCompatDialogFragment {

    private TextView dialogTitle;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.demo_layout_dialog, null);

        builder.setView(view)
                .setTitle("Demo")
//                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dialogTitle = (TextView) view.findViewById(R.id.demoDialogTitle);

        final AlertDialog dialog = builder.create();
        final VideoView video_player_view = (VideoView) view.findViewById(R.id.demo_vieo_view);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.copyFrom(dialog.getWindow().getAttributes());
        dialog.getWindow().setAttributes(lp);

        String videoPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.group_expense_demo;
        Uri uri = Uri.parse(videoPath);

        video_player_view.setVideoURI(uri);
        video_player_view.start();


        return dialog;
    }
}
