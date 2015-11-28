package link.kjr.file_manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.io.File;

/**
 * Created by kr on 11/26/15.
 */
public class DirViewFragment extends android.support.v4.app.Fragment {

    GridView gv;
    FileAdapter fa;
    ViewGroup container;
    TabsActivity activity;
    LinearLayout layout;
    File directory;

    public String get_Title(){
        return fa.get_directory().getName();
    }

    public void set_file(File f){
        this.directory=f;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(BuildConfig.APPLICATION_ID,"fragment created");
        this.container=container;
        this.activity=(TabsActivity)getActivity();
        this.fa= new FileAdapter(activity);
        this.gv=new GridView(container.getContext());
        this.gv.setAdapter(fa);
        this.gv.setNumColumns(4);


        this.layout= new LinearLayout(container.getContext());
        this.layout.addView(gv);
        if(this.directory!=null){
            this.fa.setDirectory(directory);
        }
        return this.layout;
    }


    public DirViewFragment(){}
}
