package com.example.API_CRUD;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.API_CRUD.adapters.ListUserAdapter;
import com.example.API_CRUD.entities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CrudActivity extends AppCompatActivity {
    String url = "https://5fcec9123e19cc00167c62a3.mockapi.io/user";
    private ArrayList<User> users;
    private ListUserAdapter listUserAdapter;
    Button btnAdd, btnDelete, btnUpdate;
    EditText txtName, txtUsername, txtPassword;
    ListView listView;
    int position = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivy_crud);

        // init api
        users = new ArrayList<>();
        getArrayJson();

        // listview
        listView = findViewById(R.id.listView);
        listUserAdapter = new ListUserAdapter(users);
        listView.setAdapter(listUserAdapter);

        txtName = findViewById(R.id.txtName);
        txtUsername = findViewById(R.id.txtUserName);
        txtPassword = findViewById(R.id.txtPassword);

        // Add user
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = true;
                if (txtUsername.getText().toString().trim().isEmpty()) {

                    txtUsername.setError("Please fill out this field");
                    isValid = false;
                }
                if (txtPassword.getText().toString().trim().isEmpty()) {
                    txtPassword.setError("Please fill out this field");
                    isValid = false;
                }
                if (txtName.getText().toString().trim().isEmpty()) {

                    txtName.setError("Please fill out this field");
                    isValid = false;
                }

                if(isValid) {
                    addUser();
                }
            }
        });

        // select user
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              position = i;
              txtName.setText(users.get(i).getName());
              txtUsername.setText(users.get(i).getUsername());
              txtPassword.setText(users.get(i).getPassword());
            }
        });

        // delete user
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position != -1) {
                    deleteUser(users.get(position).getId());
                } else {
                    Toast.makeText(CrudActivity.this, "Chọn user để xóa", Toast.LENGTH_LONG).show();
                }
            }
        });

        // update user
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(txtName.getText().toString(), txtUsername.getText().toString(), txtPassword.getText().toString());
                if(position != -1) {
                    updateUser(user, users.get(position).getId());
                } else {
                    Toast.makeText(CrudActivity.this, "Chọn user để update", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getArrayJson() {
        users = new ArrayList<>();
        final ProgressDialog dialog = ProgressDialog.show(CrudActivity.this, null, "Please Wait");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = (JSONObject) response.get(i);
                        users.add(new User(object.getString("id"), object.getString("name"), object.getString("username"), object.getString("password")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
                listUserAdapter = new ListUserAdapter(users);
                listView.setAdapter(listUserAdapter);
                listUserAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CrudActivity.this, "Error by get Json Array!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(CrudActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    private void addUser() {
        position = -1;
        final ProgressDialog dialog = ProgressDialog.show(CrudActivity.this, null, "Please Wait");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++){
                    try {
                        JSONObject object = (JSONObject) response.get(i);
                        if(object.getString("username").equals(txtUsername.getText().toString())) {
                            User user = new User(object.getString("id"), object.getString("name"), object.getString("username"), object.getString("password"));
                            if(user != null) {
                                dialog.dismiss();
                                Toast.makeText(CrudActivity.this, "User Exist", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                User user = new User(txtName.getText().toString(), txtUsername.getText().toString(), txtPassword.getText().toString());
                postApi(user);
                clearInput();
                // reload data
                getArrayJson();
                dialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(CrudActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    private void postApi(final User user){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CrudActivity.this, "Successfully", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CrudActivity.this, "Error by Post data!", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("name", user.getName());
                params.put("username", user.getUsername());
                params.put("password", user.getPassword());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CrudActivity.this);
        requestQueue.add(stringRequest);
    }

    private void deleteUser(String id){
        final ProgressDialog dialog = ProgressDialog.show(CrudActivity.this, null, "Please Wait");
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE, url + '/' + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(CrudActivity.this, "Successfully", Toast.LENGTH_LONG).show();
                clearInput();
                // reload data
                getArrayJson();
                position = -1;
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CrudActivity.this, "Error by Post data!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(CrudActivity.this);
        requestQueue.add(stringRequest);
    }

    private void updateUser(final User user, String id) {
        position = -1;
        final ProgressDialog dialog = ProgressDialog.show(CrudActivity.this, null, "Please Wait");
        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url + '/' + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Toast.makeText(CrudActivity.this, "Successfully", Toast.LENGTH_LONG).show();
                clearInput();
                // reload data
                getArrayJson();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(CrudActivity.this, "Error by Post data!", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("name", user.getName());
                params.put("username", user.getUsername());
                params.put("password", user.getPassword());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CrudActivity.this);
        requestQueue.add(stringRequest);
    }
    // clear
    private void clearInput() {
        txtName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
    }

}
