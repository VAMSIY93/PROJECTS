package com.example.mohdsaood.lms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

/**
 * Created by Mohd Saood on 15/04/16.
 */
public class frag2 extends Fragment {

    JSONArray array;
    GridView listview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.frag2, container, false);
        listview = (GridView) view.findViewById(R.id.issued_books);

        if(MainActivity.type.equals("admin"))
        {
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(),return_book.class);
//              Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_LONG).show();
                    try {
                        intent.putExtra("mid", array.getJSONObject(position/3-1).getString("mid"));
                        intent.putExtra("name",array.getJSONObject(position/3-1).getString("name"));
                        intent.putExtra("author", array.getJSONObject(position/3-1).getString("author"));
                        intent.putExtra("type",array.getJSONObject(position/3-1).getString("type"));
                        intent.putExtra("issuedto",array.getJSONObject(position/3-1).getString("username"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            });
        }

        String url = "http://"+MainActivity.ip+"/library/get_issued.php?username=" + MainActivity.userid + "&password=" + MainActivity.pass+ "&type=" + MainActivity.type;
        req(url);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String url = "http://"+MainActivity.ip+"/library/get_issued.php?username=" + MainActivity.userid + "&password=" + MainActivity.pass+ "&type=" + MainActivity.type;
        req(url);
    }

    private void Result(String response)
    {
        Log.i("Issued result",response);
        if(response.equals("NULL"))
        {

            String[] books=new String []{};
            listview.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, books));
        }
        else
        {
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                array = obj.getJSONArray("materialinfo");
                String[] books= new String[array.length()*3+3];
                int k=0;
                books[k++]="Name";
                books[k++]="Issue Date";
                if(MainActivity.type.equals("admin"))
                {
                    books[k++]="username";
                }
                else
                {
                    books[k++]="author";
                }

                //Toast.makeText(getContext(), Integer.toString(array.length()), Toast.LENGTH_SHORT).show();

                for(int i=0;i<array.length();i++)
                {
                    JSONObject obj2= array.getJSONObject(i);
                    books[k++]=obj2.getString("name");
                    books[k++]=obj2.getString("issue_date");
                    if(MainActivity.type.equals("admin"))
                    {books[k++]=obj2.getString("username");}
                    else
                    {books[k++]=obj2.getString("author");}
                    Log.i("books", books[i]);
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