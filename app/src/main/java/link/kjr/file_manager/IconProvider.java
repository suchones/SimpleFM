package link.kjr.file_manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by kr on 11/25/15.
 */
public class IconProvider {
    HashMap<String,String> db;
    Context c;


    public int get_id(String suffix){

        if(db.containsKey(suffix)){


            String fname=db.get(suffix);
            fname=fname.substring(0, fname.lastIndexOf("."));

            Log.i(BuildConfig.APPLICATION_ID, "suffix:" + suffix + " db.get:" + fname);
            int identifer=c.getResources().getIdentifier("link.kjr.file_manager:drawable/"+fname,null,null);
            return identifer;

        }else{
            return R.drawable.application_x_ruby;
        }

    }


    public IconProvider(Context context){
        c=context;
        db= new HashMap<>();

        Log.i("file_manager","starting getIconForFile");
        XmlPullParser xmlp= Xml.newPullParser();
        try {
            xmlp.setInput(context.getResources().openRawResource(R.raw.icon_codes),null);
            int xml_event_type=xmlp.getEventType();


            String id="unset", value="";
            ArrayList<String>ids = new ArrayList<>();
            while(xml_event_type!=XmlPullParser.END_DOCUMENT){
                if(xml_event_type==XmlPullParser.START_TAG) {
                    String element_name = xmlp.getName() == null ? "" : xmlp.getName();

                    if (element_name.equals("id")) {
                        xml_event_type = xmlp.next();
                        if (xml_event_type == XmlPullParser.TEXT && !xmlp.isWhitespace()) {
                            id = xmlp.getText();
                            if(id.equals("unset")){
                                continue;
                            }
                            ids.add(id);
                        }
                        continue;
                    }

                    if (element_name.equals("value")) {
                        xml_event_type = xmlp.next();
                        if (xml_event_type == XmlPullParser.TEXT && !xmlp.isWhitespace()) {
                            value = xmlp.getText();
                            if(id.equals("unset")){
                                continue;
                            }
                            for (String _id:ids){
                                db.put(_id,value);
                            }
                            ids=new ArrayList<>();
                        }
                        continue;
                    }
                }
                xml_event_type=xmlp.next();
            }
        }catch (org.xmlpull.v1.XmlPullParserException p){
p.printStackTrace();
        }catch(IOException io){
io.printStackTrace();
        }



    }

    public void print() {
        for (Map.Entry<String, String> e : db.entrySet()) {
            Log.i(BuildConfig.APPLICATION_ID, "key:" + e.getKey() + "_value:" + e.getValue());
        }
    }
}
