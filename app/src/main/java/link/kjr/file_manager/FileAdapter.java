package link.kjr.file_manager;

import android.app.ActionBar;
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
    MainActivity ma;
    IconProvider iconProvider;
    FileAdapter(MainActivity ma){
        this.ma=ma;
        this.iconProvider=new IconProvider(ma.getBaseContext());
    }
    @Override
    public int getCount() {
        return ma.getRootfile().listFiles().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public ImageView getImageView(File f){


        ImageView iv= new ImageView(ma.getBaseContext());
iv.setMaxHeight(10);

        try {
            if(f.isDirectory()){
                iv.setImageDrawable(ma.getBaseContext().getDrawable(R.drawable.inode_directory));
            }else{
                String file_suffix=f.getName().substring(f.getName().lastIndexOf(".")+1,f.getName().length());
                int icon_id= this.iconProvider.get_id(file_suffix);
                Log.e(BuildConfig.APPLICATION_ID, "got icon_id:" + icon_id + " for suffix:" + file_suffix);
                if(icon_id!=-1){
                    iv.setImageDrawable(ma.getBaseContext().getDrawable(icon_id));
                }else {
                    iv.setImageDrawable(ma.getDrawable(R.drawable.application_msword));
                }

            }
        } catch (StringIndexOutOfBoundsException sioobe){

        }
        
        return iv;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BaseAdapter ba=this;
        RelativeLayout ll= new RelativeLayout(ma.getBaseContext());
        ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setBackgroundColor(position % 3 == 0 ? Color.DKGRAY:((position%3)%2==0?Color.MAGENTA:Color.GREEN));

        TextView tv=new TextView(ma.getBaseContext());
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
                    Toast.makeText(ma.getBaseContext(), "this is a normal file", Toast.LENGTH_LONG).show();
                }
            }
        });
        return ll;
    }
}
