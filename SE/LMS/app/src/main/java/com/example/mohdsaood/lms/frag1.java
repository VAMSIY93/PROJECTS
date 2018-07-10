package com.example.mohdsaood.lms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

/**
 * Created by Mohd Saood on 15/04/16.
 */
public class frag1 extends Fragment {

    GridView listview;
    TextView search;
    JSONObject obj;
    JSONArray array;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.frag1, container, false);
        listview = (GridView) view.findViewById(R.id.search_results);
        search = (TextView) view.findViewById(R.id.editText);
        Button button = (Button) view.findViewById(R.id.search);
        spinner=(Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.search_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //Toast.makeText(getContext(), spinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//              Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_LONG).show();
                if(position>2)
                {
                    Intent intent = new Intent(getActivity(), searched_book.class);
                    try {
                        intent.putExtra("mid", array.getJSONObject(position / 3 - 1).getString("mid"));
                        intent.putExtra("name", array.getJSONObject(position / 3 - 1).getString("name"));
                        intent.putExtra("author", array.getJSONObject(position / 3 - 1).getString("author"));
                        intent.putExtra("type", array.getJSONObject(position / 3 - 1).getString("type"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                String url = "http://"+MainActivity.ip+"/library/search.php?username=" + MainActivity.userid+ "&password=" + MainActivity.pass+ "&find=" + search.getText().toString()+"&type="+spinner.getSelectedItem().toString();
                req(url);

            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!search.getText().toString().equals(""))
        {
            String url = "http://"+MainActivity.ip+"/library/search.php?username=" + MainActivity.userid+ "&password=" + MainActivity.pass+ "&find=" + search.getText().toString()+"&type="+spinner.getSelectedItem().toString();
            req(url);
        }
    }

    private void Result(String response)
    {
        Log.i("Search result",response);


        try {
            obj = new JSONObject(response);
            array = obj.getJSONArray("materialinfo");
            String[] books= new String[array.length()*3+3];
            int k=0;
            books[k++]="Name";
            books[k++]="Author";
            books[k++]="Availability";
            //books[k++]="Type";
            if(books.length==0)
            {

                Toast.makeText(getContext(), "No results!", Toast.LENGTH_SHORT).show();

            }
            for(int i=0;i<array.length();i++)
            {
                JSONObject obj2= array.getJSONObject(i);
                books[k++]=obj2.getString("name");
                books[k++]=obj2.getString("author");
                books[k++]=obj2.getString("availability");
                //books[k++]=obj2.getString("type");
                Log.i("books",books[i]);
            }

            listview.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, books));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void req(String url)
    {
        Log.i("url", url);

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