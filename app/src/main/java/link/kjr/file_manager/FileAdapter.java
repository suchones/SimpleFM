package link.kjr.file_manager;

import android.app.ActionBar;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by kr on 11/26/15.
 */
public class FileAdapter extends BaseAdapter {

    File directory;
    //IconProvider iconProvider;
    Context context;
    TabsActivity activity;

    FileAdapter(TabsActivity activity){

        Log.i(BuildConfig.APPLICATION_ID,"FileAdapter created");
        directory=android.os.Environment.getRootDirectory();
        this.activity=activity;
        this.context=activity.getBaseContext();
    }

    public File get_directory(){
        return directory;
    }
    public void setDirectory(File dir){
        if(dir!=null){
            this.directory=dir;

        }else {
            Log.i(BuildConfig.APPLICATION_ID,"Fileadapter.setdir was called, however dir was null");
            this.directory=android.os.Environment.getRootDirectory();
        }
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        Log.i(BuildConfig.APPLICATION_ID,"getCount  called,:"+directory.listFiles().length);

        return directory.listFiles().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Deprecated
    public ImageView getImageView(File f){

/*
        ImageView iv= new ImageView(this.context);
        iv.setMaxHeight(10);

        try {
            if(f.isDirectory()){
                iv.setImageDrawable(this.context.getDrawable(R.drawable.inode_directory));
            }else{
                String file_suffix=f.getName().substring(f.getName().lastIndexOf(".")+1,f.getName().length());
                int icon_id= this.iconProvider.get_id(file_suffix);
                Log.e(BuildConfig.APPLICATION_ID, "got icon_id:" + icon_id + " for suffix:" + file_suffix);
                if(icon_id!=-1){
                    iv.setImageDrawable(this.context.getDrawable(icon_id));
                }else {
                    iv.setImageDrawable(context.getDrawable(R.drawable.application_msword));
                }

            }
        } catch (StringIndexOutOfBoundsException sioobe){

        }
        
        return iv;
        */
        return null;

    }
    void old(){
        /*
        final BaseAdapter ba=this;
        RelativeLayout ll= new RelativeLayout(this.context);
        ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setBackgroundColor(position % 3 == 0 ? Color.DKGRAY:((position%3)%2==0?Color.MAGENTA:Color.GREEN));

        TextView tv=new TextView(this.context);
        tv.setTextColor(Color.BLUE);
        tv.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        String fname=ma.getRootfile().list()[position];
        tv.setText(fname.length() > 10 ? (fname.substring(0, 9)) : (fname));

        ll.addView(tv);
        final File f=ma.getRootfile().listFiles()[position];
        ll.addView(getImageView(f));
        ll.setGravity(View.FOCUS_UP);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(f.isDirectory()){
                    ba.notifyDataSetChanged();
                    ma.setFile(f);

                }else{
                    Toast.makeText(this.context, "this is a normal file", Toast.LENGTH_LONG).show();
                }
            }
        });
        return ll;
        */
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i(BuildConfig.APPLICATION_ID, "getView called");


        TextView tv= new TextView(context);
        tv.setText(directory.listFiles()[position].getName());
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.GREEN);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File c = directory.listFiles()[position];
                if (c.isDirectory()) {
                    activity.addTab(c);
                } else {
                    Toast.makeText(context,"this is a regualer file",Toast.LENGTH_LONG).show();
                }

            }
        });

        LinearLayout ll=new LinearLayout(context);
        ll.addView(tv);
        return ll;
    }
}
