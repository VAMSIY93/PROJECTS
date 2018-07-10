package com.example.mohdsaood.lms;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
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


public class frag5 extends Fragment {
    boolean add=true;
    EditText fname_et,lname_et,userid_et,password_et;
    TextView tv;
    Button add_del,submit;
    GridView users_lv;
    JSONObject obj;
    JSONArray array;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.frag5, container, false);

        users_lv= (GridView) view.findViewById(R.id.users);
        tv=(TextView)view.findViewById(R.id.textView);
        add_del=(Button) view.findViewById(R.id.Add_del);
        submit=(Button) view.findViewById(R.id.submit);
        fname_et=(EditText)view.findViewById(R.id.fname);
        lname_et=(EditText)view.findViewById(R.id.lname);
        userid_et=(EditText)view.findViewById(R.id.mid);
        password_et=(EditText)view.findViewById(R.id.pass);
        //users.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, books));

        add_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add) {
                    add = false;
                    tv.setText("Delete a User");
                    fname_et.setVisibility(View.GONE);
                    lname_et.setVisibility(View.GONE);
                    password_et.setVisibility(View.GONE);
                    users_lv.setVisibility(View.VISIBLE);

                } else {
                    add = true;
                    tv.setText("Add user");
                    fname_et.setVisibility(View.VISIBLE);
                    lname_et.setVisibility(View.VISIBLE);
                    password_et.setVisibility(View.VISIBLE);
                    users_lv.setVisibility(View.GONE);


                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                if(add)
                {
                    url = "http://"+MainActivity.ip+"/library/add_student.php?username=" + userid_et.getText().toString() + "&password=" + password_et.getText().toString()+"&fname=" + fname_et.getText().toString()+"&lname=" + lname_et.getText().toString();

                }
                else
                {
                    url = "http://"+MainActivity.ip+"/library/delete_student.php?username=" + userid_et.getText().toString();
                }
                req(url,true);

            }
        });
        String  url = "http://"+MainActivity.ip+"/library/all_users.php?username=" + MainActivity.userid+"&type="+MainActivity.type;
        req(url,false);
        users_lv.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String  url = "http://"+MainActivity.ip+"/library/all_users.php?username=" + MainActivity.userid+"&type="+MainActivity.type;
        req(url, false);

    }

    private void Result(String response)
    {
        Log.i("Result ",response);
        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
        String  url = "http://"+MainActivity.ip+"/library/all_users.php?username=" + MainActivity.userid+"&type="+MainActivity.type;
        req(url, false);

    }
    private void Result2(String response)
    {
        try {
            obj = new JSONObject(response);
            array = obj.getJSONArray("usersinfo");
            String[] users= new String[array.length()*4+4];
            int k=0;
            users[k++]="ID";
            users[k++]="Fname";
            users[k++]="Lname";
            users[k++]="Type";
            if(users.length==0)
            {
                Toast.makeText(getContext(), "No results!", Toast.LENGTH_SHORT).show();
            }
            for(int i=0;i<array.length();i++)
            {
                JSONObject obj2= array.getJSONObject(i);
                users[k++]=obj2.getString("username");
                users[k++]=obj2.getString("fname");
                users[k++]=obj2.getString("lname");
                users[k++]=obj2.getString("type");
                Log.i("users",users[i]);
            }
            users_lv.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, users));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void req(String url, final boolean check)
    {
        Log.i("url", url);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        if(check)
                        {Result(response);}
                        else
                        {Result2(response);}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getContext(),"Error requesting Url",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

}
