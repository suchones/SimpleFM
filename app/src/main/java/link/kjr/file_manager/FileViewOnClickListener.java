package link.kjr.file_manager;

import android.graphics.Color;
import android.view.View;

import java.io.File;

/**
 * Created by kr on 12/5/15.
 */
public class FileViewOnClickListener implements View.OnClickListener ,View.OnLongClickListener{

    String path;
    MainActivity activity;
    FileViewOnClickListener(String path, MainActivity activity){
        this.path=path;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        File f=new File(path);
        if(f.isDirectory()){
            activity.setDirectoryView(f.getAbsolutePath());
        }else {
            activity.selectFileWithView(path,v);


            v.findViewById(R.id.internal).setBackgroundColor(activity.FileIsSelected(path) ? Color.MAGENTA :activity.getResources().getColor(R.color.item));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Factory.FilePreview(activity, path);
        return true;
    }
}
