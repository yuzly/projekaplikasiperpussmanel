package com.wahyuzulya.perpustakaansmanel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wahyuzulya.perpustakaansmanel.peminjam.PeminjamPinjam;
import com.wahyuzulya.perpustakaansmanel.staf.StafDashboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static public String errorn;
    EditText username,password;
    Button login;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(getApplicationContext());
        CheckSession();
        username = (EditText) findViewById(R.id.edt_username);
        password = (EditText) findViewById(R.id.edt_password);
        login = (Button) findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(MainActivity.this);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sUsername = username.getText().toString();
                String sPassword = password.getText().toString();

                if ((!sUsername.equals(""))&&(!sPassword.equals(""))){
                    CheckLogin(sUsername,sPassword);
                }
                else {
                    username.setError("Masukkan username!");
                    password.setError("Masukkan sandi!");
                }

            }
        });
    }
    public void CheckSession(){
        if (sessionManager.getLogin()==true){
            //Toast.makeText(getApplicationContext(),sessionManager.getUsername(),Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, PeminjamPinjam.class));
            finish();
        } else {
            //Toast.makeText(getApplicationContext(),sessionManager.getUsername(),Toast.LENGTH_SHORT).show();
        }
    }
    public void CheckLogin(final String username, final String password){

        if (checkNetworkConnection()){
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String resp = jsonObject.getString("server_response");
                                String userResp = jsonObject.getString("user_response");
                                String idResp = jsonObject.getString("id");
                                if (resp.equals("[{\"status\":\"OK\"}]")){

                                    Toast.makeText(getApplicationContext(),"Login Berhasil!",Toast.LENGTH_SHORT).show();
                                    if (userResp.equals("[{\"user\":\"STAF\"}]")){
                                        sessionManager.setUsername(username);
                                        sessionManager.setLogin(true);

                                        startActivity(new Intent(MainActivity.this, StafDashboard.class));
                                    } else if (userResp.equals("[{\"user\":\"PEMINJAM\"}]"))
                                    {
                                        String rem1 = idResp.replace("[{","");
                                        String rem2 = rem1.replace("}]","");
                                        String rem3 = rem2.replace("\"","");
                                        String rem4 = rem3.replace("id:","");
                                        sessionManager.setId(rem4);
                                        sessionManager.setUsername(username);
                                        sessionManager.setLogin(true);
                                        sessionManager.setStatus("peminjam");


                                        startActivity(new Intent(MainActivity.this, PeminjamPinjam.class));


                                        finish();
                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(),resp,Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                errorn = e.getMessage();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("username",username);
                    params.put("password",password);
                    return params;
                }
            };

            VolleyConnection.getInstance(MainActivity.this).addToRequest(stringRequest);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            },2000);
        }else {
            Toast.makeText(getApplicationContext(),"Tidak ada koneksi internet",Toast.LENGTH_SHORT).show();
            Intent gagalIntent = new Intent(MainActivity.this,Gagal.class);
            startActivity(gagalIntent);
        }
    }
    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null&&networkInfo.isConnected());
    }
}