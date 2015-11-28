package link.kjr.file_manager;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by kr on 11/27/15.
 */
public class TabsActivity extends AppCompatActivity {

    public void addTab(){
        ta.addTab();
    }
    public void addTab(File dir){
        ta.addTab(dir);
    }

    public  class TabsAdapter extends android.support.v4.app.FragmentPagerAdapter implements ViewPager.OnPageChangeListener,TabListener{

        ArrayList<android.support.v4.app.Fragment> fragments;
        TabsAdapter(AppCompatActivity a, ViewPager vp){
            super(a.getSupportFragmentManager());
            fragments= new ArrayList<>();
            fragments.add(new DirViewFragment());

            Log.i(BuildConfig.APPLICATION_ID, "TabsAdapter created");

        }

        public void  addTab(){
            fragments.add( new DirViewFragment());
            notifyDataSetChanged();
        }
        public void addTab(File dir){
            DirViewFragment dvf=  new DirViewFragment();
            dvf.set_file(dir);
            fragments.add( dvf);
            notifyDataSetChanged();

        }

        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
tab.setText("blah");
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            tab.setText("blah");
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            tab.setText("blah");
        }
    }

    TabsAdapter ta;
    ViewPager vp;
    android.support.v7.app.ActionBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout= new LinearLayout(this);
        vp= new ViewPager(this);
        vp.setId(0x1000);

        ta = new TabsAdapter(this,vp);
        vp.setAdapter(ta);
setContentView(vp);

        Log.i(BuildConfig.APPLICATION_ID, "TabsActivity created");

    }




}
