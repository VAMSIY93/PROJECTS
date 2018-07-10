package com.example.mohdsaood.lms;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
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


public class frag3 extends Fragment {
    GridView listview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.frag3, container, false);
        listview = (GridView) view.findViewById(R.id.issued_books);
        String url = "http://"+MainActivity.ip+"/library/get_returned.php?username=" + MainActivity.userid + "&password=" + MainActivity.pass+ "&type=" + MainActivity.type;
        req(url);
        return view;
    }
    private void Result(String response)
    {
        Log.i("Issued result", response);

        JSONObject obj = null;
        try {
            obj = new JSONObject(response);
            JSONArray array = obj.getJSONArray("materialinfo");
            String[] books= new String[array.length()*3+3];
            int k=0;
            books[k++]="Name";
            books[k++]="Iss. Dt";
            books[k++]="Ret. Dt";

            //Toast.makeText(getContext(), Integer.toString(array.length()), Toast.LENGTH_SHORT).show();

            for(int i=0;i<array.length();i++)
            {
                JSONObject obj2= array.getJSONObject(i);
                books[k++]=obj2.getString("name");
                books[k++]=obj2.getString("issue_date");
                books[k++]=obj2.getString("return_date");

                Log.i("books",books[i]);
            }
            listview.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, books));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void req(String url)
    {
        Log.i("url",url);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                Toast.makeText(getContext(),"Error requesting Url",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }
}