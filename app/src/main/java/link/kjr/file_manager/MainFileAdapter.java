package link.kjr.file_manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by kr on 12/11/15.
 */
public class MainFileAdapter extends BaseAdapter{

    ArrayList<String> files= new ArrayList<>();
    MainActivity activity;
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
        File f = new File("/");
        mfa.ProcessDir(f,term,0);
        return mfa;
    }
    static MainFileAdapter BuilderDirectoryView(String path,MainActivity activity, int layoutid){
        MainFileAdapter mfa= new MainFileAdapter();
        mfa.activity = activity;
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
        Log.i(BuildConfig.APPLICATION_ID, "MainFileAdapter created");
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

    public boolean isVideo(File f){
        String file_name=f.getName();
        String suffix=file_name.substring(file_name.lastIndexOf(".")+1,file_name.length());

        switch (suffix){
            case "mp4":
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
            iv.get().setImageBitmap(bitmap);
        }


    }

    public void setDrawable(ImageView icon,String path, File f){
        if(f!=null && f.exists() && isImage(f)){
            BitmapLoader bl= new BitmapLoader(icon,path,true);

            bl.execute(4,5,6);
        }else if(f!=null && f.exists() && isVideo(f)){
            BitmapLoader bl= new BitmapLoader(icon,path,false);

            bl.execute(4,5,6);

        }
        else if( f.isDirectory()){
            icon.setImageDrawable(activity.getDrawable(R.drawable.folder));
        }else if(f.canExecute()){
            icon.setImageDrawable(activity.getDrawable(R.drawable.application_x_executable));

        }else{
            icon.setImageDrawable(activity.getDrawable(MainActivity.ip.getDrawableIdforSuffix(path)));

        }
    }
    public View FileView(String abspath){


        Log.e(BuildConfig.APPLICATION_ID,"LayoutResId:"+layoutid);
        View rootView= activity.getLayoutInflater().inflate(layoutid, null);

        if(layoutid==R.layout.row_item){
            TextView path=(TextView)rootView.findViewById(R.id.path);
            ImageView icon=(ImageView)rootView.findViewById(R.id.icon);

            path.setText(abspath);


            File f=new File(abspath);
            setDrawable(icon,abspath,f);


            rootView.setBackgroundColor((activity.FileIsSelected(abspath)) ? Color.MAGENTA : activity.getBaseContext().getColor(R.color.body1));

            FileViewOnClickListener fvocl= new FileViewOnClickListener(abspath, activity);
            rootView.setOnClickListener(fvocl);
            rootView.setOnLongClickListener(fvocl);

            MainActivity.views.put(abspath, rootView);

            return rootView;
        }
        else if(layoutid==R.layout.grid_item){


            TextView filename=(TextView)rootView.findViewById(R.id.filename),
                    filesize=(TextView)rootView.findViewById(R.id.filesize);
            ImageView icon=(ImageView)rootView.findViewById(R.id.icon);

            File f=new File(abspath);
            setDrawable(icon,abspath,f);

            filesize.setText(""+f.length());
            String filenametext=f.getName();
            if(filenametext.length()>10){
                filenametext=filenametext.substring(0,9);
            }
            filename.setText(filenametext);

            rootView.setBackgroundColor((activity.FileIsSelected(abspath)) ? Color.MAGENTA : activity.getBaseContext().getColor(R.color.body1));

            FileViewOnClickListener fvocl= new FileViewOnClickListener(abspath, activity);
            rootView.setOnClickListener(fvocl);
            rootView.setOnLongClickListener(fvocl);

            MainActivity.views.put(abspath, rootView);

            return rootView;
        }
        return rootView;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String path=files.get(position);
        if (MainActivity.views.containsKey(path) ){
            Log.e(BuildConfig.APPLICATION_ID, "returning cached view");
            return MainActivity.views.get(path);
        } else {
            Log.i(BuildConfig.APPLICATION_ID, "generating new view");
           return FileView(files.get(position));
        }
    }
}
