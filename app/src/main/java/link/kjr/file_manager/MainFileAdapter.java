package link.kjr.file_manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import android.os.Handler;

/**
 * Created by kr on 12/11/15.
 */
public class MainFileAdapter extends BaseAdapter{

    java.util.Stack<Pair<ImageView,String>> stack;
    Queue<Pair<ImageView,String>> queue;
    Runnable Consumer;
    ArrayList<String> files= new ArrayList<>();
    MainActivity activity;
    HashMap<String,View> views= new HashMap<>();
    android.os.Handler handler;
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

    static MainFileAdapter BuilderSearch(String term,MainActivity activity,int layoutid){
        MainFileAdapter mfa= new MainFileAdapter();
        mfa.activity = activity;
        mfa.layoutid=layoutid;
        mfa.handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputmessage){

            }
        };
        mfa. stack= new java.util.Stack<>();
        mfa.Consumer = new ImageLoader(mfa.stack,mfa.handler,activity);
        new Thread(mfa.Consumer).start();

        File f = new File("/");
        mfa.ProcessDir(f,term,0);
        return mfa;
    }
    static MainFileAdapter BuilderDirectoryView(String path,MainActivity activity, int layoutid){
        MainFileAdapter mfa= new MainFileAdapter();
        mfa.activity = activity;
        mfa.handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputmessage){

            }
        };
        mfa. stack= new java.util.Stack<>();
        mfa.Consumer = new ImageLoader(mfa.stack,mfa.handler,activity);

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
        new Thread(mfa.Consumer).start();
        return mfa;
    }


    MainFileAdapter(){

        Log.i(BuildConfig.APPLICATION_ID, "MainFileAdapter created");
    }


    @Override
    public int getCount() {
        Log.i(BuildConfig.APPLICATION_ID, "getcount called");

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


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Deprecated
    class BitmapLoader extends AsyncTask<Integer,Void,Bitmap>{

        WeakReference<ImageView> iv;
        WeakReference<String> path;
        WeakReference<Boolean> type;
        public BitmapLoader(ImageView iv, String path, boolean type){
            this.iv= new WeakReference<>(iv);
            this.type=new WeakReference<>(type);
            this.path=new WeakReference<>(path);
        }
        @Override
        protected Bitmap doInBackground(Integer ... params){
            if(type.get()){
                return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path.get()),40,40,true);
            } else {
                return android.media.ThumbnailUtils.createVideoThumbnail(path.get(), MediaStore.Images.Thumbnails.MICRO_KIND);

            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(iv.get()!=null){
                iv.get().setImageBitmap(bitmap);
            }
        }


    }



    public View FileView(String abspath){


        Log.i(BuildConfig.APPLICATION_ID,"LayoutResId:"+layoutid);
        View rootView= activity.getLayoutInflater().inflate(layoutid, null);

        if(layoutid==R.layout.row_item){
            TextView path=(TextView)rootView.findViewById(R.id.path);
            ImageView icon=(ImageView)rootView.findViewById(R.id.icon);

            path.setText(abspath);
            stack.push(new Pair<>(icon, abspath));

            if( activity.FileIsSelected(abspath)){
                activity.selectFileView(rootView);
            }
            rootView.setBackgroundColor((activity.FileIsSelected(abspath)) ? Color.MAGENTA : activity.getResources().getColor(R.color.item));
            FileViewOnClickListener fvocl= new FileViewOnClickListener(abspath, activity);
            rootView.setOnClickListener(fvocl);
            rootView.setOnLongClickListener(fvocl);

            views.put(abspath,rootView);
           // MainActivity.views.put(abspath, rootView);

            return rootView;
        }
        else if(layoutid==R.layout.grid_item){


            TextView    filename=(TextView)rootView.findViewById(R.id.filename),
                        filesize=(TextView)rootView.findViewById(R.id.filesize);
            ImageView icon=(ImageView)rootView.findViewById(R.id.icon);

            File f=new File(abspath);
            stack.push(new Pair<>(icon,abspath));

            filesize.setText("" + f.length());
            String filenametext=f.getName();
            if(filenametext.length()>10){
                filenametext=filenametext.substring(0,9);
            }
            filename.setText(filenametext);

            View internal=rootView.findViewById(R.id.internal);
            if( activity.FileIsSelected(abspath)){
                activity.selectFileView(rootView);
            }
            internal.setBackgroundColor((activity.FileIsSelected(abspath)) ? Color.MAGENTA : activity.getResources().getColor(R.color.item));

            FileViewOnClickListener fvocl= new FileViewOnClickListener(abspath, activity);
            rootView.setOnClickListener(fvocl);
            rootView.setOnLongClickListener(fvocl);

            //MainActivity.views.put(abspath, rootView);
            views.put(abspath,rootView);

            return rootView;
        }
        return rootView;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String abs=new File(files.get(position)).getAbsolutePath();
        if(views.containsKey(abs)){
            return views.get(abs);
        }

           return FileView(files.get(position));

    }
}
