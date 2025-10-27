package com.malta_mqf.malta_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.UserModel;
import com.malta_mqf.malta_mobile.Utilities.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    EditText user_name;
    EditText password;
    EditText van_Name;
    Button login;
    String user, pass, van_name;
    UserDetailsDb userDetailsDb;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        userDetailsDb = new UserDetailsDb(this);
        user_name = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        van_Name = findViewById(R.id.vechilename);
        login = findViewById(R.id.login);
        //    getSupportActionBar().hide();
        if (mPrefs.getBoolean(Constants.LOGGED_IN, false) == true) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = user_name.getText().toString().trim();
                pass = password.getText().toString().trim();
                van_name = van_Name.getText().toString().trim();
                System.out.println(user + " " + pass + " " + van_name);
                if (user.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your username...", Toast.LENGTH_SHORT).show();
                    return;
                } else if (pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your password...", Toast.LENGTH_SHORT).show();
                    return;
                } else if (van_name.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your van name...", Toast.LENGTH_SHORT).show();
                    return;
                }
                userlogin();
            }
        });
    }


    private void userlogin() {
        showProgressDialog();
        String url = ApiLinks.loginurl + "?username=" + user + "&password=" + pass + "&name=" + van_name;
        System.out.println(url);
        Log.d("LoginUrl", url);
        Call<UserModel> logincall = apiInterface.loginUser(url);
        logincall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                dismissProgressDialog();
                if (response.body().getMessage().equalsIgnoreCase("Success")) {
                    // Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    UserModel item = response.body();
                    System.out.println("item" + item);
                    mPrefs.edit()
                            .putString("Id", String.valueOf(item.getId()))
                            .putString("Emp_code", item.getEmpCode())
                            .putString(Constants.NAME, item.getName())
                            .putString(Constants.USER_NAME, user)
                            .putString("Category", item.getCategory())
                            .putString("Van name", van_name)
                            .putString("vanId", item.getVanId())
                            .putString("routeName", item.getRouteName())
                            .putString(Constants.ROLE, item.getRole())
                            .putString("vehicleno", item.getVehicleNo())
                            .putBoolean(Constants.LOGGED_IN, true)
                            .commit();

                    Cursor cursor = userDetailsDb.readAllData();
                    if (cursor.getCount() == 0) {
                        userDetailsDb.addDetails(item.getName(), String.valueOf(item.getId()), item.getCategory(), item.getVanId(), item.getRouteName(), item.getEmailid(), item.getMobileno(), item.getRole(), item.getVehicleNo(), item.getEmpCode(), "1975-08-05 12:00:00", "D3S160920240000", "D3S160920240000");//this  is for approved sync purpose
                    } else {
                        userDetailsDb.UpdateData("1", item.getName(), String.valueOf(item.getId()), item.getCategory(), item.getVanId(), item.getRouteName(), item.getEmailid(), item.getMobileno(), item.getRole(), item.getVehicleNo(), item.getEmpCode(), "1975-08-05 12:00:00");//this is for approved order sync purpose
                    }

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    cursor.close();
                } else {
                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                user = null;
                pass = null;
                van_name = null;

            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                dismissProgressDialog();
                displayAlert("Alert", t.getMessage());
            }
        });
    }
}