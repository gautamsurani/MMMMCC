package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.model.PackageList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONObject;

import static com.makemusiccount.android.util.Util.convertDpToPixel;

public class PaymentActivity extends AppCompatActivity {

    EditText etCardNumber1, etCardNumber2, etCardNumber3, etCardNumber4, etMonth, etYear, etCVV;

    TextView btnSubmit, tvSubHeading, tvHeading;

    String card_number1, card_number2, card_number3, card_number4, month, year, token_card = "",
            cvv, card_number = "", resMessage = "", resCode = "", title_1 = "", title_2 = "",
            transction_id = "", amount = "", validity = "";

    Activity context;

    ProgressDialog progressDialog;

    Card card;

    PackageList packageList;

    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment);

        context = this;

        packageList = (PackageList) getIntent().getSerializableExtra("package");

        progressDialog = new ProgressDialog(context);
        etCardNumber1 = findViewById(R.id.etCardNumber1);
        etCardNumber2 = findViewById(R.id.etCardNumber2);
        etCardNumber3 = findViewById(R.id.etCardNumber3);
        etCardNumber4 = findViewById(R.id.etCardNumber4);
        tvHeading = findViewById(R.id.tvHeading);
        ivBack = findViewById(R.id.ivBack);
        tvSubHeading = findViewById(R.id.tvSubHeading);
        etMonth = findViewById(R.id.etMonth);
        etYear = findViewById(R.id.etYear);
        etCVV = findViewById(R.id.etCVV);

        tvHeading.setText(packageList.getName());
        tvSubHeading.setText(packageList.getPlan_price_info());

        btnSubmit = findViewById(R.id.btnSubmit);

        etCardNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 4) {
                    etCardNumber1.clearFocus();
                    etCardNumber2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etCardNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 4) {
                    etCardNumber2.clearFocus();
                    etCardNumber3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etCardNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 4) {
                    etCardNumber3.clearFocus();
                    etCardNumber4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnSubmit.setOnClickListener(view -> {
            card_number1 = etCardNumber1.getText().toString();
            card_number2 = etCardNumber2.getText().toString();
            card_number3 = etCardNumber3.getText().toString();
            card_number4 = etCardNumber4.getText().toString();
            month = etMonth.getText().toString();
            year = etYear.getText().toString();
            cvv = etCVV.getText().toString();
            card_number = card_number1 + card_number2 + card_number3 + card_number4;

            if (card_number.length() < 16) {
                Toast.makeText(context, "Enter correct card number", Toast.LENGTH_SHORT).show();
            } else if (month.isEmpty()) {
                Toast.makeText(context, "Enter month", Toast.LENGTH_SHORT).show();
            } else if (year.isEmpty()) {
                Toast.makeText(context, "Enter year", Toast.LENGTH_SHORT).show();
            } else if (cvv.isEmpty()) {
                Toast.makeText(context, "Enter cvv", Toast.LENGTH_SHORT).show();
            } else {
                card = new Card(card_number1 + "-" + card_number2 + "-" + card_number3 + "-" + card_number4,
                        Integer.parseInt(month),
                        Integer.parseInt(year),
                        cvv);
                if (!card.validateCard()) {
                    Toast.makeText(context, "Invalid Card Detail", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    createTaken();
                }
            }
        });

        ivBack.setOnClickListener(view -> onBackPressed());
    }

    private void createTaken() {
        Stripe stripe = new Stripe(context, "pk_live_4GaYIsUbKEW0Zj4knTIGU04b");
        stripe.createToken(card, new TokenCallback() {
                    public void onSuccess(Token token) {
                        token_card = token.getId();
                        new Payment().execute();
                    }

                    public void onError(Exception error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        dismissProgressDialog();
                    }
                }
        );
    }

    @SuppressLint("StaticFieldLeak")
    private class Payment extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String strLogin = AppConstant.API_Payment;
            String strTrim = strLogin.replaceAll(" ", "%20");
            Log.d("strTrim", strTrim);
            try {
                RestClient restClient = new RestClient(strTrim);
                try {
                    restClient.AddParam("userID", Util.getUserId(context));
                    restClient.AddParam("packageID", packageList.getPackID());
                    restClient.AddParam("userEMAIL", Util.getEmail(context));
                    restClient.AddParam("package_amount", packageList.getPrice());
                    restClient.AddParam("card_number", card_number);
                    restClient.AddParam("cvc_number", cvv);
                    restClient.AddParam("expire_month", month);
                    restClient.AddParam("expire_year", "20" + year);
                    restClient.AddParam("stripeToken", token_card);
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String APIString = restClient.getResponse();
                Log.e("result", APIString);

                if (APIString != null && APIString.length() != 0) {
                    jsonObjectList = new JSONObject(APIString);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        title_1 = jsonObjectList.getString("title_1");
                        title_2 = jsonObjectList.getString("title_2");
                        transction_id = jsonObjectList.getString("transction_id");
                        amount = jsonObjectList.getString("amount");
                        validity = jsonObjectList.getString("validity");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                openPopup();
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            } else {
                openPopupFail();
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    AlertDialog dialog;

    @SuppressLint("SetTextI18n")
    private void openPopupFail() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_payment_fail, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvTitle1 = alert_layout.findViewById(R.id.tvTitle1);
        TextView tvId = alert_layout.findViewById(R.id.tvId);
        TextView tvAmount = alert_layout.findViewById(R.id.tvAmount);
        TextView tvBtn = alert_layout.findViewById(R.id.tvBtn);

        tvTitle.setText(title_1);
        tvTitle1.setText(title_2);
        tvId.setText(transction_id);
        tvAmount.setText(amount);

        tvBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = convertDpToPixel(500, context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);
    }

    @SuppressLint("SetTextI18n")
    private void openPopup() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_payment_status, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();
        dialog.setCancelable(false);

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvTitle1 = alert_layout.findViewById(R.id.tvTitle1);
        TextView tvId = alert_layout.findViewById(R.id.tvId);
        TextView tvAmount = alert_layout.findViewById(R.id.tvAmount);
        TextView tvValidity = alert_layout.findViewById(R.id.tvValidity);
        TextView tvBtn = alert_layout.findViewById(R.id.tvBtn);

        tvTitle.setText(title_1);
        tvTitle1.setText(title_2);
        tvId.setText(transction_id);
        tvAmount.setText(amount);
        tvValidity.setText(validity);

        tvBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.setOnDismissListener(dialogInterface -> {
            startActivity(new Intent(context, MyAccountActivity.class));
            finish();
        });

        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = convertDpToPixel(500, context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
