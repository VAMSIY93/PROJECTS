package com.example.mohdsaood.lms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    static String userid,pass,type,ip;
    EditText userid_et,pass_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip="10.237.27.98";

    }
    public void login(View view)
    {
        userid= ((EditText)findViewById(R.id.mid)).getText().toString();
        pass= ((EditText)findViewById(R.id.pass)).getText().toString();

        /*if(userid.equals(""))
        {userid="3";}
        if(pass.equals(""))
        {pass="priya123";}*/

        String url="http://"+ip+"/library/login.php?username="+userid+"&password="+pass;
        //Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
        //url="http://10.237.27.98/library/logout.php";
        //url="http://www.iitd.ac.in";
        req(url);
        /*Intent intent = new Intent(this, drawer.class);
        startActivity(intent);*/
    }
    void Result(String response)
    {
        //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
        Log.i("response",response);
        if(response.equals("invalid login"))
        {

            Toast.makeText(getApplicationContext(),"Wrong Userid or password!",Toast.LENGTH_LONG).show();
        }
        else
        {

            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                JSONArray array = obj.getJSONArray("userinfo");
                JSONObject obj2= array.getJSONObject(0);


                Intent intent;
                if(obj2.getString("type").equals("student"))
                {
                    type="student";
                    Log.i("Student login","Student Login");
                    intent = new Intent(this, drawer.class);
                    Toast.makeText(getApplicationContext(),"Student login",Toast.LENGTH_LONG).show();
                }
                else
                {
                    type="admin";
                    Log.i("Admin login","Admin Login");
                    Toast.makeText(getApplicationContext(),"Admin login",Toast.LENGTH_LONG).show();
                    intent = new Intent(this, drawer_admin.class);
                }

                intent.putExtra("userid",obj2.getString("username"));
                intent.putExtra("fname",obj2.getString("fname"));
                intent.putExtra("lname",obj2.getString("lname"));
                intent.putExtra("pass",obj2.getString("password"));
                intent.putExtra("type",obj2.getString("type"));
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void req(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Result(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(),"Error requesting Url",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

}
