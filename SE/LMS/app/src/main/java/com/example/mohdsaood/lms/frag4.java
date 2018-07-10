package com.example.mohdsaood.lms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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



public class frag4 extends Fragment {
    GridView listview;
    JSONObject obj;
    JSONArray array;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.frag4, container, false);
        listview = (GridView) view.findViewById(R.id.issued_books);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position>2){
                    Intent intent = new Intent(getActivity(), requested_book.class);
                    try {
                        intent.putExtra("mid", array.getJSONObject(position/3-1).getString("mid"));
                        intent.putExtra("name",array.getJSONObject(position/3-1).getString("name"));
                        intent.putExtra("author",array.getJSONObject(position/3-1).getString("author"));
                        intent.putExtra("type",array.getJSONObject(position/3-1).getString("type"));
                        if(MainActivity.type.equals("admin"))
                        {
                            intent.putExtra("req_user", array.getJSONObject(position / 3 - 1).getString("username"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);

                }
//              Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_LONG).show();

            }
        });

        String url = "http://"+MainActivity.ip+"/library/get_requested.php?username=" + MainActivity.userid + "&password=" + MainActivity.pass+"&type=" + MainActivity.type;
        req(url);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getContext(),"Resumed",Toast.LENGTH_SHORT).show();
        String url = "http://"+MainActivity.ip+"/library/get_requested.php?username=" + MainActivity.userid + "&password=" + MainActivity.pass+"&type=" + MainActivity.type;
        req(url);
    }

    private void Result(String response)
    {
        Log.i("Requested result", response);
        if(response.equals("NULL"))
        {

            String[] books=new String []{};
            listview.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, books));
        }
        else
        {
            try {
                obj = new JSONObject(response);
                array = obj.getJSONArray("materialinfo");
                String[] books= new String[array.length()*3+3];
                int k=0;
                books[k++]="Name";
                books[k++]="Date";
                if(MainActivity.type.equals("student"))
                {
                    books[k++]="Author";
                }
                else
                {
                    books[k++]="Userid";
                }


                //Toast.makeText(getContext(), "Arraylength"+Integer.toString(array.length()), Toast.LENGTH_SHORT).show();

                for(int i=0;i<array.length();i++)
                {
                    JSONObject obj2= array.getJSONObject(i);
                    books[k++]=obj2.getString("name");
                    books[k++]=obj2.getString("request_date");

                    if(MainActivity.type.equals("student"))
                    {
                        books[k++]=obj2.getString("author");
                    }
                    else
                    {
                        books[k++]=obj2.getString("username");
                    }
                    Log.i("books",books[i]);
                }
                listview.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, books));

            } catch (JSONException e) {
                e.printStackTrace();
            }
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