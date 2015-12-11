package link.kjr.file_manager;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by kr on 11/26/15.
 */
public class DirViewFragment extends android.support.v4.app.Fragment {


    public void processArgs(){
        Bundle b= getArguments();
        if( b!=null){
            for (String s:b.keySet()){

                    Log.e(BuildConfig.APPLICATION_ID, " got args: " +s+" value:"+ b.getString(s)+" ;");
                }

            }

        else {
            Log.e(BuildConfig.APPLICATION_ID," no keys in fragment");
        }
    }
    @Override
    public void onAttach(Context c){
        super.onAttach(c);
        Log.i(BuildConfig.APPLICATION_ID,"running onattch");
        processArgs();
    }
public void onStart(){
    super.onStart();


    Log.e(BuildConfig.APPLICATION_ID, "running onstart,:");

   processArgs();
}
    @Override
    public void onResume(){
super.onResume();

        Log.e(BuildConfig.APPLICATION_ID,"running onresume,:");
processArgs();
    }
    @Override
    public void onCreate(Bundle sa){
            super.onCreate(sa);
        Log.e(BuildConfig.APPLICATION_ID,"running oncreated,:");
processArgs();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    Log.e(BuildConfig.APPLICATION_ID, " running oncreatview");
        String path="", mode="",regex="",term="";
        BaseAdapter ap;
        View view;

        int layout=0;
        //0 Grid
        //1 List

        Bundle b=getArguments();
        if(b!=null){
            path =b.getString("path");
            mode= b.getString("mode");
            term=b.getString("term");
            layout=b.getInt("layout");
        }else {
            Log.e(BuildConfig.APPLICATION_ID," getargs was nulll");
            System.exit(-1);
        }


        switch (mode==null?"":mode) {
            case "search":
                Log.e(BuildConfig.APPLICATION_ID, "using mode search");
                ap=MainFileAdapter.BuilderSearch(term,(Act)getActivity(),layout==0?R.layout.griditem:R.layout.rowitem);
                break;
            case "":

            case "normal":
            default:
               ap=MainFileAdapter.BuilderDirectoryView(path,(Act)getActivity(),layout==0?R.layout.griditem:R.layout.rowitem);

        }

        if(layout==0){
            view=inflater.inflate(R.layout.gridviewlayout,null);

            GridView gv=(GridView)view.findViewById(R.id.contentview);
            WindowManager wm=getActivity().getWindowManager();
            Point p;
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            int measuredWidth = size.x/300;
            gv.setNumColumns(measuredWidth+1);
            gv.setAdapter(ap);
        }
        else {
            view=inflater.inflate(R.layout.listviewlayout,null);
            ListView lv=(ListView)view.findViewById(R.id.contentview);
            lv.setAdapter(ap);

        }

        return view;


    }
    public DirViewFragment(){}


}
