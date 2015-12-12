package link.kjr.file_manager;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kr on 12/11/15.
 */
public class MainFileAdapter extends BaseAdapter{

    ArrayList<String> files= new ArrayList<>();
    Act act;
    int layoutid;
    public void ProcessDir(File f, String term, int depth){
        if (depth>=4){
            return;
        }
        if(f.exists()){
            if(f.isDirectory() && f.listFiles()!=null){
                for(File iter:f.listFiles()){
                    ProcessDir(iter,term,(depth+1));
                }
            }else {
                if(f.getAbsolutePath().contains(term)){
                    files.add(f.getAbsolutePath());
                }
            }
        }

    }

    static MainFileAdapter BuilderSearch(String term,Act act,int layoutid){
        MainFileAdapter mfa= new MainFileAdapter();
        mfa.act=act;
        mfa.layoutid=layoutid;
        File f = new File("/");
        mfa.ProcessDir(f,term,0);
        return mfa;
    }
    static MainFileAdapter BuilderDirectoryView(String path,Act act, int layoutid){
        MainFileAdapter mfa= new MainFileAdapter();
        mfa.act=act;
        File f = new File(path);
        mfa.layoutid=layoutid;

        if(f.exists() && f.isDirectory() &&f.listFiles()!=null){
            for(File iter:f.listFiles()){
                mfa.files.add(iter.getAbsolutePath());
            }
        }
        else {
            f= new File("/");
            for(File iter:f.listFiles()){
                mfa.files.add(iter.getAbsolutePath());
            }
        }
        return mfa;
    }


    MainFileAdapter(){
        Log.i(BuildConfig.APPLICATION_ID,"MainFileAdapter created");
    }


    @Override
    public int getCount() {
        Log.e(BuildConfig.APPLICATION_ID, "getcount called");

        if(files==null){
            return 0;
        }
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i(BuildConfig.APPLICATION_ID, "getitem called");

        return null;
    }
    public boolean isImage(File f){
        String file_name=f.getName();
        String suffix=file_name.substring(file_name.lastIndexOf(".")+1,file_name.length());

        switch (suffix){
            case "jpg":
            case "png":
                return true;
            default:
                return false;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class BitmapLoader extends AsyncTask<Integer,Void,Bitmap>{

        ImageView iv;
        String path;
        public BitmapLoader(ImageView iv, String path){
            this.iv=iv;
            this.path=path;
        }
        @Override
        protected Bitmap doInBackground(Integer ... params){
            return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path),40,40,true);

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iv.setImageBitmap(bitmap);
        }


    }

    public View FileView(String abspath){


        Log.e(BuildConfig.APPLICATION_ID,"LayoutResId:"+layoutid);
        View rootView=act.getLayoutInflater().inflate(layoutid, null);

        if(layoutid==R.layout.rowitem){
            TextView path=(TextView)rootView.findViewById(R.id.path);
            ImageView icon=(ImageView)rootView.findViewById(R.id.icon);

            path.setText(abspath);

            icon.setImageDrawable(act.getDrawable(Act.ip.getDrawableIdforSuffix(abspath)));


            rootView.setBackgroundColor((act.FileIsSelected(abspath)) ? Color.MAGENTA : act.getBaseContext().getColor(R.color.body1));

            FileViewOnClickListener fvocl= new FileViewOnClickListener(abspath,act);
            rootView.setOnClickListener(fvocl);
            rootView.setOnLongClickListener(fvocl);

            Act.views.put(abspath, rootView);

            return rootView;
        }
        else if(layoutid==R.layout.griditem){


            TextView filename=(TextView)rootView.findViewById(R.id.filename),
                    filesize=(TextView)rootView.findViewById(R.id.filesize);
            ImageView icon=(ImageView)rootView.findViewById(R.id.icon);

            File f=new File(abspath);
            if(f!=null && f.exists() && isImage(f)){
              BitmapLoader bl= new BitmapLoader(icon,abspath);

                bl.execute(4,5,6);
            }
            else if( f.isDirectory()){
                icon.setImageDrawable(act.getDrawable(R.drawable.inode_directory));
            }else{
                icon.setImageDrawable(act.getDrawable(Act.ip.getDrawableIdforSuffix(abspath)));

            }


            filesize.setText(""+f.length());
            String filenametext=f.getName();
            if(filenametext.length()>10){
                filenametext=filenametext.substring(0,9);
            }
            filename.setText(filenametext);

            rootView.setBackgroundColor((act.FileIsSelected(abspath)) ? Color.MAGENTA : act.getBaseContext().getColor(R.color.body1));

            FileViewOnClickListener fvocl= new FileViewOnClickListener(abspath,act);
            rootView.setOnClickListener(fvocl);
            rootView.setOnLongClickListener(fvocl);

            Act.views.put(abspath, rootView);

            return rootView;
        }
        return rootView;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String path=files.get(position);
        if (Act.views.containsKey(path) ){
            Log.e(BuildConfig.APPLICATION_ID, "returning cached view");
            return Act.views.get(path);
        } else {
            Log.i(BuildConfig.APPLICATION_ID, "generating new view");
           return FileView(files.get(position));
        }
    }
}
