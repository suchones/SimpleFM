package link.kjr.file_manager;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.WeakHashMap;


public class MainActivity extends ActionBarActivity  implements View.OnClickListener,AdapterView.OnItemClickListener {



    ArrayList<View> selectedFileViews;
    ArrayList<String> selectedFiles;
    static WeakHashMap<String,View> views= new WeakHashMap<>();
    static IconProvider ip;
    String currentPath;
    ArrayList<String> p;
    AlertDialog.Builder ab;
    AlertDialog ad;


    public void refresh(){
        destroyCache();
        File dir = new File(currentPath);
        if(dir.exists()){
            setDirectoryView(dir.getAbsolutePath());
        }else{
            setDirectoryView("/");
        }

    }

    public String getSelectedFiles(){
        String ret="";
        for(String file:selectedFiles){
            ret+=file+"\n";
        }
        return ret;
    }
    public void openfile(String path){
        Intent i= new Intent();

        i.setAction(Intent.ACTION_VIEW);
        String mimetype= MimeTypeMap.getSingleton().getMimeTypeFromExtension(path.substring(path.lastIndexOf(".")+1,path.length()));
        if(mimetype!= null && mimetype.length()>2){
            Log.e(BuildConfig.APPLICATION_ID,"mimetype "+mimetype+" for "+path);
            i.setDataAndType(Uri.fromFile(new File(path)),mimetype);
           startActivity(i);
        }


    }
    public void AddSelectedFile(String fname){
        if (selectedFileViews==null){
            selectedFileViews= new ArrayList<>();
        }


        if (selectedFiles.contains(fname)) {
            selectedFiles.remove(fname);
        }else {
            selectedFiles.add(fname);

        }
    }

    public void deselectFiles(){
        selectedFiles= new ArrayList<>();
        for(View v:selectedFileViews){
            v.setBackgroundColor(getColor(R.color.item));
        }
        selectedFileViews= new ArrayList<>();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater mi= new MenuInflater(this);
        mi.inflate(R.menu.menu, menu);
        return true;
    }


    boolean onSaveCalled=false;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        onSaveCalled=true;
        Log.e(BuildConfig.APPLICATION_ID, "onSaveInstance called");
        savedInstanceState.putStringArrayList("tabs", p);
        savedInstanceState.putString("currentPath", currentPath);
        savedInstanceState.putStringArrayList("selectedFiles", selectedFiles);
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        Log.e(BuildConfig.APPLICATION_ID, " onRestoreInstance called");

        ArrayList<String> s=savedInstanceState.getStringArrayList("selectedFiles");
        onSaveCalled=false;
        if(s!=null){
            selectedFiles=s;
        }
        String new_currentPath=savedInstanceState.getString("currentPath");
        if(new_currentPath!=null){
            currentPath=new_currentPath;
        }
        setDirectoryView(currentPath);

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(BuildConfig.APPLICATION_ID, "onResume called");
    }

    @Override
    public void onBackPressed(){

        Log.i(BuildConfig.APPLICATION_ID, "onBackPressed");
        String newPath= currentPath.substring(0,currentPath.lastIndexOf("/"));
        if (newPath.equals("")){
            newPath="/";
        }

            setDirectoryView(newPath);
        refresh();

    }
    public void destroyCache(){
        views= new WeakHashMap<>();
    }
    public void flushCache(){
        ArrayList<String> files=new ArrayList<>();
        if(views.size()>200){
            for (String  file:views.keySet()) {
                files.add(file);
            }
        }
        for (String  file:files) {
            views.remove(file);
        }

    }
    public void setDirectoryView(Bundle b){
        if(onSaveCalled){
            Toast.makeText(this,"cannot change because onSave was called",Toast.LENGTH_SHORT).show();
        }
        flushCache();
        currentPath=b.getString("path");
        TextView addressbar=(TextView)findViewById(R.id.addressbar);
        addressbar.setText(new File(b.getString("path")).getName());

        DirectoryViewFragment dvf= (DirectoryViewFragment)Fragment.instantiate(this,DirectoryViewFragment.class.getName(),b);
        getSupportFragmentManager().beginTransaction().replace(R.id.placeholer2,dvf).commitAllowingStateLoss();

    }

    public void setDirectoryView(String path){
        Bundle extra=new Bundle();
        extra.putString("mode","normal");
        extra.putString("path", path);
        setDirectoryView(extra);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onSaveCalled=false;
        destroyCache();


        setContentView(R.layout.main_layout);
        if(!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE )==PackageManager.PERMISSION_GRANTED &&
                this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED )){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},9);

        }
        Log.i(BuildConfig.APPLICATION_ID, "printing db");


        selectedFiles=new ArrayList<>();
        ip= new IconProvider(this);

        ip.print();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setDirectoryView("/");
        currentPath="/";
    }

    public void directoryChanged(File dir) {
        if(onSaveCalled){
            return;
        }
        setDirectoryView(dir.getAbsolutePath());
    }


    public void selectFile(String path) {
        AddSelectedFile(path);
        Snackbar sb=Snackbar.make(findViewById(R.id.drawer_layout), "there are " + selectedFiles.size() + " files selected", Snackbar.LENGTH_SHORT);
        sb.show();
    }

    public void selectFileWithView(String path, View v) {
        selectFile(path);
        selectedFileViews.add(v);
    }

    public boolean FileIsSelected(String path) {
        return this.selectedFiles.contains(path);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.e(BuildConfig.APPLICATION_ID, "onItemclick called but not an etv");

    }


    public void onSeachClicked(View view) {

        ab= new AlertDialog.Builder(this);
        View seachdialogview=getLayoutInflater().inflate(R.layout.search_dialog, null);
        TextView textView3=(TextView)seachdialogview.findViewById(R.id.searchdialogtextview);

        ab.setView(seachdialogview);
        ad=ab.create();
        ad.show();
    }

    public void dismiss(View view) {
        ad.dismiss();
    }


    public void searchfiles(View view) {

        Bundle b= new Bundle();
        b.putString("path", "/");
        b.putInt("layout",1);
        b.putString("mode", "search");
        View view1=view.getRootView();
        view1=view1.findViewById(R.id.searchdialogedittext);

        if(view1!=null && view1 instanceof  EditText && ((EditText) view1).getText()!=null){
            b.putString("term", ((EditText) view1).getText().toString());
        }
        else {
            Log.e(BuildConfig.APPLICATION_ID,"could not get search term");
            b.putString("term","xml");
        }

        setDirectoryView(b);
        ad.dismiss();
    }

    public void postMessage(String msg){
        Snackbar.make(findViewById(R.id.drawer_layout), msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }

    public void open(View view) {


    }
    public void deleteDir(String path){
        Log.e(BuildConfig.APPLICATION_ID, "will delete dir");
            try {
                Runtime.getRuntime().exec("rm " + path + " -Rf");
            } catch (IOException e) {
                Toast.makeText(this,"could not delete dir",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        refresh();
    }

    public void clickDownloads(View view) {
        String path=Environment.getDownloadCacheDirectory().getAbsolutePath();
        setDirectoryView(path);
        Log.e(BuildConfig.APPLICATION_ID, " set to Download");
    }

    public void clickPictures(View view) {
        String path=Environment.getDownloadCacheDirectory().getAbsolutePath();
        setDirectoryView(path);
        Log.e(BuildConfig.APPLICATION_ID, " set to Pictures");
    }

    public void clickRoot(View view) {
        setDirectoryView("/");
        Log.e(BuildConfig.APPLICATION_ID, " set to Root");
    }

    public void deleteFiles(){
        Log.e(BuildConfig.APPLICATION_ID, "will now delete files");

        for (String s : selectedFiles) {
            try {
                Runtime.getRuntime().exec("rm " + s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void deleteFiles(MenuItem item) {
        if(selectedFiles==null || (selectedFiles.size()==0)){
            Toast.makeText(this,"no files selected",Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder ab= new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.confirm_dialog,null);
        ((TextView)view.findViewById(R.id.title)).setText("Delete the following files:");
        ((TextView)view.findViewById(R.id.body)).setText(getSelectedFiles());

        ab.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFiles();
                deselectFiles();
                refresh();
            }
        });
        ab.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ab.setView(view);

        ab.create().show();


    }

    public void moveFiles(){
        Log.e(BuildConfig.APPLICATION_ID, "will now move files");
        for (String s : selectedFiles) {
            try {
                Runtime.getRuntime().exec("mv " + s + " " + currentPath + "/");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void moveFiles(MenuItem item) {
        if(selectedFiles==null || (selectedFiles.size()==0)){
            Toast.makeText(this,"no files selected",Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder ab= new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.confirm_dialog,null);
        ((TextView)view.findViewById(R.id.title)).setText("move the following files to :"+currentPath);
        ((TextView)view.findViewById(R.id.body)).setText(getSelectedFiles());

        ab.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveFiles();
                deselectFiles();
                refresh();

            }
        });
        ab.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ab.setView(view);

        ab.create().show();

        refresh();

    }

    public void copyFiles(){
        Log.e(BuildConfig.APPLICATION_ID, "will now copy files");
        for (String s : selectedFiles) {
            try {
                Runtime.getRuntime().exec("cp " + s + " " + currentPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void copyFiles(MenuItem item) {
        if(selectedFiles==null || (selectedFiles.size()==0)){
            Toast.makeText(this,"no files selected",Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog ad=null;
        AlertDialog.Builder ab= new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.confirm_dialog,null);
        ((TextView)view.findViewById(R.id.title)).setText("move the following files to :"+currentPath);
        ((TextView)view.findViewById(R.id.body)).setText(getSelectedFiles());
        ab.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copyFiles();

                refresh();
            }
        });
        ab.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ab.setView(view);

        ad=ab.create();
        ad.show();
        refresh();

    }

    public void unselectFiles(MenuItem item) {
        deselectFiles();
    }


    public void mkdir(String dirpath){
        try {

            Runtime.getRuntime().exec("mkdir "+dirpath);
        } catch (IOException e) {
            Toast.makeText(this,"could not make dir;"+dirpath,Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
    }

    public void mkdir(MenuItem item) {
        AlertDialog ad=null;
        AlertDialog.Builder ab= new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.mkdir_dialog,null);
        final EditText et=(EditText)view.findViewById(R.id.searchdialogedittext);
TextView title=(TextView)view.findViewById(R.id.title);
        title.setText("make new directory in "+currentPath);

     ab.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             Log.e(BuildConfig.APPLICATION_ID, "will now mkdir");

                     Toast.makeText(getApplicationContext(),"mkdir " + currentPath+"/"+et.getText(),Toast.LENGTH_SHORT).show();
                        mkdir(currentPath+"/"+et.getText());

                 refresh();

             }
         }
     );
     ab.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             refresh();
         }
     });

        ab.setView(view);

        ad=ab.create();
        ad.show();

    }

    public void refresh(MenuItem item) {
        refresh();
    }
}
