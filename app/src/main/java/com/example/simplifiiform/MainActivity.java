package com.example.simplifiiform;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zhouyou.view.seekbar.SignSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import static android.view.View.GONE;
import static android.view.View.TEXT_ALIGNMENT_CENTER;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Something";
    static String data = "";
    static List<Model> models;
    LinearLayout linearLayout;
    ArrayList<View> views;

    int TITLE_ID = 6;
    int MIN_ID = 7;
    int MAX_ID = 8;
    int SEEK_ID = 9;

    RelativeLayout progressLayout;
    TextInputEditText editText;
    SignSeekBar signSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressLayout = findViewById(R.id.progress_layout);
        models = new ArrayList<>();
        linearLayout = findViewById(R.id.my_layout);
        FetchData fetchData = new FetchData(this, linearLayout);
        fetchData.execute();
        views = new ArrayList<>();
        disableAutoFill();
    }


    public class FetchData extends AsyncTask<Void, Void, Void> {

        Context context;
        LinearLayout linearLayout;

        FetchData(Context context, LinearLayout linearLayout) {
            this.context = context;
            this.linearLayout = linearLayout;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL apiUrl = new URL("https://ca.platform.simplifii.xyz/api/v1/static/assignment2");
                HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = reader.readLine();
                    data = data + line;
                }

                JSONObject object = new JSONObject(data);
                JSONObject response = (JSONObject) object.get("response");
                JSONArray jsonArray = response.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Model model = new Model();
                    if (jsonObject.get("type").toString().equals("range")) {
                        model.setType(jsonObject.get("type").toString());
                        model.setLabel(jsonObject.get("label").toString());
                        model.setName(jsonObject.get("name").toString());
                        model.setMin(Integer.parseInt(jsonObject.get("min").toString()));
                        model.setMax(Integer.parseInt(jsonObject.get("max").toString()));
                        model.setInterval(Integer.parseInt(jsonObject.get("intervals").toString()));
                    }
                    if (jsonObject.get("type").toString().equals("input")) {
                        model.setType(jsonObject.get("type").toString());
                        model.setLabel(jsonObject.get("label").toString());
                        model.setName(jsonObject.get("name").toString());
                        model.setInputType(jsonObject.get("inputType").toString());
                        JSONArray jsonArray1 = jsonObject.getJSONArray("validations");
                        JSONObject jsonObject1 = (JSONObject) jsonArray1.get(0);
                        model.setValidationName(jsonObject1.get("name").toString());
                        model.setValidationMsg(jsonObject1.get("message").toString());
                    }
                    if (jsonObject.get("type").toString().equals("button")) {
                        model.setType(jsonObject.get("type").toString());
                        model.setAction(jsonObject.get("action").toString());
                        model.setLabel(jsonObject.get("label").toString());
                        JSONObject obj = jsonObject.getJSONObject("api");
                        model.setApiUri(obj.getString("uri"));
                        Log.d("wtf", "doInBackground: " + model.getApiUri());
                        model.setApiMethod(obj.getString("method"));
                        model.setAuthEnabled(Boolean.parseBoolean(obj.getString("authEnabled")));
                    }

                    Log.d(TAG, "doInBackground: " + model.getType());

                    models.add(model);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressLayout.setVisibility(GONE);

            if (!models.isEmpty()) {
                for (int i = 0; i < models.size(); i++) {
                    final Model model = models.get(i);
                    if (model.getType().equals("range")) {
                        inputTypeRange(model);
                    }
                    if (model.getType().equals("input")) {
                        makeInputField(model);
                    }
                    if (model.getType().equals("button")) {
                        makeButton(model);
                    }

                }

            }
        }

        private void makeButton(final Model model) {
            Button button = new Button(context);
            button.setText(model.getLabel());
            button.setPadding(8, 8, 8, 8);
            linearLayout.setPadding(16, 0, 16, 0);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(buttonParams);
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            button.setTextColor(Color.WHITE);

            linearLayout.addView(button);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText.getText().toString().isEmpty()) {
                        Log.d(TAG, "onClick: Something bad");
                        Snackbar snackbar = Snackbar.make(linearLayout, Html.fromHtml("<font color=\"#FFFFFF\">Fields can't be empty</font>"), Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                        snackbar.show();
                    } else {
                        postData(model);
                    }
                }
            });
        }

        void postData(Model model) {

            try {
                AsyncHttpClient client = new AsyncHttpClient();
                // Http Request Params Object
                RequestParams params = new RequestParams();

                int i =0;
                for (View items : views){
                    Model model1 = models.get(i);
                    if (items instanceof EditText){
                        params.put(model1.getLabel(), ((EditText)items).getText().toString());
                    }
                    if (items instanceof SignSeekBar){
                        params.put(model1.getLabel(), String.valueOf(((SignSeekBar)items).getProgress()));
                    }
                    i++;
                }

                client.post(model.getApiUri(), params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        showDialog();
                        Log.i("Response", "Response SP Status. " + response);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        super.onFailure(throwable);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void makeInputField(final Model model) {
            editText = new TextInputEditText(context);
            LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            TextInputLayout textInputLayout = new TextInputLayout(context);
            LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            textInputLayout.setLayoutParams(textInputLayoutParams);
            textInputLayout.addView(editText, editTextParams);
            if (model.getValidationName().equals("required")) {
                textInputLayout.setHint(model.getLabel() + "*");
            } else {
                textInputLayout.setHint(model.getLabel() + "*");

            }
            editTextParams.setMargins(16, 16, 16, 16);
            linearLayout.addView(textInputLayout);

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (model.getValidationName().equals("required") && editText.getText().toString().isEmpty() || !isValidEmail(editText.getText().toString())) {
                            editText.setError(model.getValidationMsg());
                        }
                    }
                }
            });

            views.add(editText);

            for (final View items : views) {
                if (items instanceof EditText) {
                    if (!items.hasFocus()) {
                        ((EditText) items).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if ((model.getValidationName().equals("required") && ((EditText)items).getText().toString().isEmpty()) || !isValidEmail(((EditText)items).getText().toString())) {
                                    ((EditText)items).setError(model.getValidationMsg());
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                    }
                }
            }

        }


        private void inputTypeRange(Model model) {

            //dynamic layout
            RelativeLayout relativeLayout = new RelativeLayout(context);
            relativeLayout.setPadding(16, 16, 16, 0);

            //layout params for title and seekbar
            RelativeLayout.LayoutParams titlePars = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams seekBar = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            //seekbar should be below title as one unit
            titlePars.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            seekBar.addRule(RelativeLayout.BELOW, TITLE_ID);

            //title text
            TextView title = new TextView(context);
            title.setText(model.getLabel());
            title.setTextSize(18);
            title.setPadding(0, 0, 0, 32);
            title.setId(TITLE_ID);
            title.setLayoutParams(titlePars);

            //seekbar for range
            signSeekBar = new SignSeekBar(context);
            signSeekBar.getConfigBuilder()
                    .min(model.getMin())
                    .max(model.getMax())
                    .sectionCount(model.getInterval())
                    .sectionTextSize(16)
                    .thumbTextSize(18)
                    .signTextSize(18)
                    .showThumbText()
                    .autoAdjustSectionMark()
                    .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                    .build();

            signSeekBar.setLayoutParams(seekBar);
            signSeekBar.setPadding(16, 0, 16, 0);
            signSeekBar.setId(SEEK_ID);

            //add view to layout
            relativeLayout.addView(title);
            relativeLayout.addView(signSeekBar);

            linearLayout.addView(relativeLayout);
            views.add(signSeekBar);


        }


        public final boolean isValidEmail(CharSequence target) {
            if (target == null)
                return false;

            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }

        private void showDialog() {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.completion_dialog);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutoFill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }
}

