package com.t0p47.sciencelib.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.t0p47.sciencelib.R;
import com.t0p47.sciencelib.app.AppConfig;
import com.t0p47.sciencelib.app.AppController;
import com.t0p47.sciencelib.db.DatabaseHandler;
import com.t0p47.sciencelib.helper.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "LOG_TAG";
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private DatabaseHandler dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        //Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Session manager
        session = new SessionManager(getApplicationContext());

        dbh = new DatabaseHandler(getApplicationContext());

        if(session.isLoggedIn()){
            //User already logged in. Go to MainActivty
            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    registerUser(name, email, password);
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter your details!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void registerUser(final String name, final String email, final String password){
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RegisterActivity: response " + response.toString());
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"RegisterActivity: error "+ error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }){

            @Override
            protected Map<String,String> getParams(){

                Map<String,String> params = new HashMap<>();
                params.put("name",name);
                params.put("email", email);
                params.put("password",password);

                return params;

            }

        };

        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    private void showDialog(){
        if(!pDialog.isShowing()){
            pDialog.show();
        }
    }

    private void hideDialog(){
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
}
