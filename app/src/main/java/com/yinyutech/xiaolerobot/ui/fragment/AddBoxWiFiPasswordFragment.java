package com.yinyutech.xiaolerobot.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lezhi.soundbox.model.AddBoxButtonEnableEvent;
import com.lezhi.soundbox.model.AddBoxStatus;

import de.greenrobot.event.EventBus;

public class AddBoxWiFiPasswordFragment extends Fragment {
    private TextView ssidTextView;
    private EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_box_wi_fi_password, container, false);

        ssidTextView = (TextView)v.findViewById(R.id.ssidTextView);
        ssidTextView.setText(AddBoxStatus.getInstance().uploadWiFiName);

        passwordEditText = (EditText)v.findViewById(R.id.passwordEditText);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkStepStatus();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        checkStepStatus();
    }

    private void checkStepStatus() {
        // 允许WIFI空密码
        EventBus.getDefault().post(new AddBoxButtonEnableEvent(true, false));

        AddBoxStatus abs = AddBoxStatus.getInstance();
        abs.uploadWiFiPassword = passwordEditText.getText().toString();
    }
}
