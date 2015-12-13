package link.kjr.file_manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by kr on 12/3/15.
 */
public class Factory {
    static View FilePreview(final MainActivity activity, final String path){

        final File file= new File(path);
        if(file!=null && file.exists() && file.isDirectory()){

            View view = activity.getLayoutInflater().inflate(R.layout.alert_on_directory_layout,null);
            TextView title=(TextView)view.findViewById(R.id.title);
            title.setText("delete " + file.getName());

            AlertDialog.Builder b= new AlertDialog.Builder(activity);
            b.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.deleteDir(file.getAbsolutePath());
                }
            });
            b.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            b.setView(view);
            b.create().show();
            return view;


        }else{

            View view= activity.getLayoutInflater().inflate(R.layout.alert_on_file_dialog_layout, null);
            Button openbutton=(Button)view.findViewById(R.id.move_button);
            Button deletebutton=(Button)view.findViewById(R.id.deletebutton);
            FrameLayout frameLayout=(FrameLayout)view.findViewById(R.id.body);
            TextView title =(TextView)view.findViewById(R.id.title);
            title.setText(file.getName());

            if(openbutton!=null){
                openbutton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        activity.openfile(path);

                    }
                });
            }
            if(deletebutton!=null){
                deletebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean result=(new File(path).delete());
                        if(result){
                            activity.postMessage(path+" deleted.");
                        }else {
                            activity.postMessage("could not delete");

                        }
                    }
                });

            }
            String suffix=path.substring(path.lastIndexOf(".") + 1, path.length());
            View preview= new TextView(activity);
            switch (suffix){
                case "mp4":
                    ImageView iv=  new ImageView(activity);
                    iv.setImageBitmap(android.media.ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND));
                    preview= iv;
                    break;

                case "jpg":
                case "png":
                case "ico":
                    ImageView iv1=  new ImageView(activity);
                    iv1.setImageBitmap(BitmapFactory.decodeFile(path));
                    preview= iv1;
                    break;
                case "txt":
                case "conf":
                case "py":
                case "html":
                case "css":
                case "js":
                case "rc":
                case "java":
                case "xml":

                    Log.e(BuildConfig.APPLICATION_ID,"generating Textview");
                    TextView tv = new TextView(activity);

                    try {
                        Reader r= new FileReader(path);
                        char h[] = new char[700];
                        r.read(h);
                        String s= String.copyValueOf(h);
                        tv.setText(s);


                    }catch (FileNotFoundException fnfe){
                        fnfe.printStackTrace();
                    }catch (IOException ioe){
                        ioe.printStackTrace();
                    }
                    preview= tv;
                    break;
                default:
                    TextView tv1 = new TextView(activity);
                    tv1.setText("there is no preview");
                    preview= tv1;
                    break;
            }

            frameLayout.addView(preview);
            AlertDialog.Builder b= new AlertDialog.Builder(activity);

            b.setView(view);
            b.create().show();


            return frameLayout;
        }
    }
}
