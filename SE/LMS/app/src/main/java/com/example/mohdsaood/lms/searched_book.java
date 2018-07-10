package com.example.mohdsaood.lms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class searched_book extends AppCompatActivity {

    String mid,name,author,type;
    TextView name_tv,author_tv,type_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book);
        Intent intent =getIntent();
        mid=intent.getStringExtra("mid");
        name=intent.getStringExtra("name");
        author=intent.getStringExtra("author");
        type=intent.getStringExtra("type");
        name_tv= (TextView) findViewById(R.id.name);
        author_tv= (TextView) findViewById(R.id.author);
        type_tv= (TextView) findViewById(R.id.type);
        name_tv.setText(name);
        author_tv.setText(author);
        type_tv.setText(type);

    }
    public void request_book(View view)
    {
        String url="http://"+MainActivity.ip+"/library/request.php?username="+MainActivity.userid+"&password="+MainActivity.pass+"&mid="+mid;
        req(url);
        finish();
    }

    private void Result(String response)
    {
        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
    }

    private void req(String url)
    {
        Log.i("Requeted url",url);
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
                Toast.makeText(getApplicationContext(), "Error requesting Url", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }
}
