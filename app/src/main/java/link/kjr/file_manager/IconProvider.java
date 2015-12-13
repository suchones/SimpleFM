package link.kjr.file_manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class IconProvider {
    HashMap<String,String> db;
    Context c;

    public int getDrawableIdforSuffix(String file_name){
        String suffix=file_name.substring(file_name.lastIndexOf(".")+1,file_name.length());
        if(db.containsKey(suffix)){
            String fname=db.get(suffix);
            fname=fname.substring(0, fname.lastIndexOf("."));
            int identifer=c.getResources().getIdentifier("link.kjr.file_manager:drawable/"+fname,null,null);
            return identifer;
        }else{
            return R.drawable.unknown;
        }
    }


    public IconProvider(Context context){
        c=context;
        db= new HashMap<>();
        Scanner scan = null;
            scan=new Scanner(context.getResources().openRawResource(R.raw.icon_codes_3));
            while (scan.hasNext()) {
                String filename=scan.next();

                String v=scan.next();
                while(!v.equals("\\")){
                    db.put(v,filename);
                    v=scan.next();
                }
            }
    }

    public void print() {
        for (Map.Entry<String, String> e : db.entrySet()) {
            Log.i(BuildConfig.APPLICATION_ID, "key:" + e.getKey() + " value:" + e.getValue());
        }
    }
}
