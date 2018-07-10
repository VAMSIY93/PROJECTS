package com.example.mohdsaood.lms;

/**
 * Created by Avantika on 17/04/16.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class drawer_test extends AppCompatActivity  {

    private DrawerLayout drawerLayout;
    private ListView listview;
    ActionBarDrawerToggle drawerListner;
    List<Fragment> list_fragment;
    RelativeLayout drawerpane;
    private String[] options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        listview= (ListView) findViewById(R.id.left_drawer);
        options=getResources().getStringArray(R.array.student_array);
        listview.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options));
        drawerpane = (RelativeLayout) findViewById(R.id.drawer_screen);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//              Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_LONG).show();
                if(position<3) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_screen, list_fragment.get(position)).commit();
                    drawerLayout.closeDrawer(drawerpane);
                }
            }
        });

        drawerListner = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
                //Toast.makeText(getApplicationContext(), "Opened up", Toast.LENGTH_SHORT).show();
            };

            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            };
        };
        drawerLayout.setDrawerListener(drawerListner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list_fragment = new ArrayList<Fragment>();
        list_fragment.add(new frag1());
        list_fragment.add(new frag2());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_screen, list_fragment.get(0) ).commit();
        list_fragment.get(0);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListner.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerListner.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
