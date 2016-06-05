package net.jeremycasey.hamiltonheatalert.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.utils.PreferenceUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GooglePlayServicesNotSupportedDialog extends DialogFragment {
    private static final String PREFERENCE_KEY = "GooglePlayServicesNotSupportedDialogCanBeShown";
    @Bind(R.id.messageTextView) TextView mMessageTextView;
    @Bind(R.id.doNotShowAgainCheckbox) CheckBox mDoNotShowAgainCheckbox;

    public static boolean shouldShow(Context context) {
        return !PreferenceUtil.getBoolean(context, PREFERENCE_KEY, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_do_not_show_again, null);
        ButterKnife.bind(this, view);

        mMessageTextView.setText(this.getString(R.string.push_not_supported_dialog_text));

        builder.setTitle(this.getString(R.string.push_not_supported_dialog_title))
            .setPositiveButton(this.getString(R.string.ok), mOnOkClicked)
            .setView(view)
        ;

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(getActivity());
    }

    DialogInterface.OnClickListener mOnOkClicked = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog,int which) {
            PreferenceUtil.put(getActivity(), PREFERENCE_KEY, mDoNotShowAgainCheckbox.isChecked());
            dialog.dismiss();
        }
    };
}
