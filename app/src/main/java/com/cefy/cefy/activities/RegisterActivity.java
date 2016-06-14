package com.cefy.cefy.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cefy.cefy.Constants;
import com.cefy.cefy.R;
import com.cefy.cefy.models.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_name) MaterialEditText name;
    @BindView(R.id.register_email) MaterialEditText email;
    @BindView(R.id.register_phone) MaterialEditText phone;
    @BindView(R.id.btn_proceed) Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEmailValid = email.validateWith(new RegexpValidator(getString(R.string.invalid_email), ".+@.+\\.[a-z]+"));
                boolean isPhoneValid = phone.validateWith(new RegexpValidator(getString(R.string.invalid_phone), "^\\d{10}$"));
                if (isEmailValid && isPhoneValid) {
                    User user = new User();
                    user.name = name.getText().toString().trim();
                    user.email = email.getText().toString().trim();
                    user.mobile = phone.getText().toString().trim();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.General.USER_DATA, user);
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                }
            }
        });
    }
}
