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
import android.widget.Spinner;
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


public class frag6 extends Fragment {

    JSONObject obj;
    JSONArray array;
    boolean add=true;
    TextView tv;
    EditText name_et,mid_et,type_et,author_et;
    Button add_res,submit;
    GridView res_lv;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.frag6, container, false);
        tv= (TextView)view.findViewById(R.id.textView);
        res_lv= (GridView) view.findViewById(R.id.resources);
        add_res=(Button) view.findViewById(R.id.Add_res);
        submit=(Button) view.findViewById(R.id.submit);
        name_et=(EditText)view.findViewById(R.id.name);
        author_et=(EditText)view.findViewById(R.id.author);
        mid_et=(EditText)view.findViewById(R.id.mid);

        spinner=(Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.res_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        add_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add) {
                    add = false;
                    tv.setText("Delete Resources");
                    name_et.setVisibility(View.GONE);
                    author_et.setVisibility(View.GONE);
                    spinner.setVisibility(View.GONE);
                    res_lv.setVisibility(View.VISIBLE);

                } else {
                    add = true;
                    tv.setText("Add Resources");
                    spinner.setVisibility(View.VISIBLE);
                    name_et.setVisibility(View.VISIBLE);
                    author_et.setVisibility(View.VISIBLE);
                    res_lv.setVisibility(View.GONE);

                }

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                if(add)
                {
                    url = "http://"+MainActivity.ip+"/library/add_resource.php?mid=" + mid_et.getText().toString() + "&name=" + name_et.getText().toString()+"&type=" + spinner.getSelectedItem().toString()+"&author=" + author_et.getText().toString();

                }
                else
                {
                    url = "http://"+MainActivity.ip+"/library/delete_resource.php?mid=" + mid_et.getText().toString();

                }
                req(url,true);

            }
        });
        res_lv.setVisibility(View.GONE);
        String  url = "http://"+MainActivity.ip+"/library/all_resources.php?username=" + MainActivity.userid+"&type="+MainActivity.type;
        req(url, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String  url = "http://"+MainActivity.ip+"/library/all_resources.php?username=" + MainActivity.userid+"&type="+MainActivity.type;
        req(url, false);
    }

    private void Result(String response)
    {
        String  url = "http://"+MainActivity.ip+"/library/all_resources.php?username=" + MainActivity.userid+"&type="+MainActivity.type;
        req(url, false);
        Log.i("Result ",response);
        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
    }
    private void Result2(String response)
    {
        try {
            obj = new JSONObject(response);
            array = obj.getJSONArray("materialinfo");
            String[] users= new String[array.length()*4+4];
            int k=0;
            users[k++]="MID";
            users[k++]="Name";
            users[k++]="Author";
            users[k++]="Type";
            if(users.length==0)
            {
                Toast.makeText(getContext(), "No results!", Toast.LENGTH_SHORT).show();
            }
            for(int i=0;i<array.length();i++)
            {
                JSONObject obj2= array.getJSONObject(i);
                users[k++]=obj2.getString("mid");
                users[k++]=obj2.getString("name");
                users[k++]=obj2.getString("author");
                users[k++]=obj2.getString("type");
                Log.i("users",users[i]);
            }
            res_lv.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, users));
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
                Toast.makeText(getContext(), "Error requesting Url", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

}
