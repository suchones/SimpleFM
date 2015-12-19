package link.kjr.file_manager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by kr on 12/17/15.
 */
public class ImageLoader implements Runnable {


    java.util.Stack<Pair<ImageView,String>> stack;
    android.os.Handler handler;
    Activity activity;
    ImageLoader(java.util.Stack<Pair<ImageView,String>> stack, android.os.Handler handler,Activity activity){
        this.stack=stack;
        this.handler=handler;
        if(activity==null){
            Log.e(BuildConfig.APPLICATION_ID,"activity was null");
            System.exit(-1);
        }
        this.activity=activity;

    }


    class CustomRunnable implements Runnable {

        ImageView iv;
        Drawable d;
        CustomRunnable(ImageView iv,Drawable d){
            this.iv=iv;
            this.d=d;
        }

        @Override
        public void run() {
            iv.setImageDrawable(d);

        }
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

    public void setDrawable(ImageView icon,String path, File f){

        if(f!=null && f.exists() && isImage(f)){

            Drawable drawable;
            if(MainActivity.imageCache.contains(path)){
                drawable=MainActivity.imageCache.get(path);
            }
            else {
                Bitmap bm= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 40, 40);
                drawable = new BitmapDrawable(activity.getResources(),bm);
                MainActivity.imageCache.put(path,drawable);

            }

            handler.post(new CustomRunnable(icon,drawable));

        }else if(f!=null && f.exists() && isVideo(f)){
            Bitmap bm=android.media.ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
            Drawable d= new BitmapDrawable(activity.getResources(),bm);

            handler.post(new CustomRunnable(icon,d));
        }
        else if( f.isDirectory()){
            Drawable d=activity.getDrawable(R.drawable.folder);
            handler.post(new CustomRunnable(icon,d));
            //icon.setImageDrawable(activity.getDrawable(R.drawable.folder));
        }else if(f.canExecute()){

            Drawable d=activity.getDrawable(R.drawable.application_x_executable);
            handler.post(new CustomRunnable(icon,d));
            //   icon.setImageDrawable(activity.getDrawable(R.drawable.application_x_executable));

        }else{

            Drawable d=activity.getDrawable(MainActivity.ip.getDrawableIdforSuffix(path));
            handler.post(new CustomRunnable(icon,d));
            //    icon.setImageDrawable(activity.getDrawable(MainActivity.ip.getDrawableIdforSuffix(path)));

        }
    }


    @Override
    public void run() {
        long sleep=1000;
        try {
            while (true){
                Thread.sleep(sleep);
                Pair<ImageView,String> item;
                while (stack.size()>0){
                    item =stack.pop();
                    //Log.e(BuildConfig.APPLICATION_ID,"image loader is running");
                    String path= item.second;
                    ImageView iv= item.first;
                    File f= new File(path);
                    setDrawable(iv,path,f);

                }
            }


        } catch (InterruptedException ire){
            ire.printStackTrace();
        }
    }

}
