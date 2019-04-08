package com.example.simplifiiform;

import android.content.Context;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ramotion.fluidslider.FluidSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Something";
    static String data = "";
    static List<Model> models;
    LinearLayout linearLayout;

    int TEXT_ID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        models = new ArrayList<>();
        linearLayout = findViewById(R.id.my_layout);
        FetchData fetchData = new FetchData(this, linearLayout);
        fetchData.execute();
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
            if (!models.isEmpty()) {
                for (int i = 0; i < models.size(); i++) {
                    final Model model = models.get(i);
                    if (model.getType().equals("range")) {
                        inputTypeRange(model);
                    }
                    if (model.getType().equals("input")){
                        final EditText editText = new EditText(context);
                        editText.setHint(model.getLabel());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(16, 16, 16, 16);
                        editText.setLayoutParams(lp);
                        linearLayout.addView(editText);

                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                if (!isValidEmail(s.toString())){
                                    editText.setError("Invalid email");
                                }
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (!isValidEmail(s.toString())){
                                    editText.setError("Invalid email");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (!isValidEmail(s.toString())){
                                    editText.setError("Invalid email");
                                }
                            }
                        });
                    }
                }

            }
        }

        private void inputTypeRange(Model model) {
            TextView title = new TextView(context);
            title.setText(model.getLabel());
            title.setTextSize(18);
            title.setId(TEXT_ID);
            LinearLayout.LayoutParams trp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            trp.setMargins(16,2,0,4);
            title.setLayoutParams(trp);


            final FluidSlider fluidSlider = new FluidSlider(context);
            fluidSlider.setTextSize(40);
            final int min = model.getMin();

            final int max = model.getMax();
            int initialText = (int) (0.5 * (max - min));
            fluidSlider.setBubbleText(String.valueOf(initialText));
            fluidSlider.setPositionListener(new Function1<Float, Unit>() {
                @Override
                public Unit invoke(Float aFloat) {
                    float pos = aFloat;
                    int total = max - min;
                    int text = (int) (min + total * pos);
                    fluidSlider.setBubbleText(String.valueOf(text));
                    return null;
                }
            });
            fluidSlider.setColorBar(R.color.colorPrimary);
            fluidSlider.setStartText(String.valueOf(model.getMin()));
            fluidSlider.setEndText(String.valueOf(model.getMax()));

            LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            flp.setMargins(16, 2, 16, 2);
            fluidSlider.setLayoutParams(flp);

            linearLayout.addView(title);
            linearLayout.addView(fluidSlider);
        }


        public final  boolean isValidEmail(CharSequence target) {
            if (target == null)
                return false;

            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}

