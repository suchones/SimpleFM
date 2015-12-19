package link.kjr.file_manager;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.WeakHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class MainActivity extends ActionBarActivity  implements AdapterView.OnItemClickListener {





    ArrayList<View> selectedFileViews;
    ArrayList<String> selectedFiles;
    static ImageCache imageCache;
    static ThumbnailIdProvider ip;
    String currentPath;
    ArrayList<String> p;
    AlertDialog.Builder ab;
    AlertDialog ad;
    android.os.Handler handler;

    static String getSuffix(String path){
        return  path.substring(path.lastIndexOf(".")+1,path.length());

    }

    public void refresh(){
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

        String suffix=getSuffix(path);
        if(suffix.equals("zip") || suffix.equals("apk") || suffix.equals("jar")){
            try{
                decompressZipFile(path);

            } catch (IOException ioe){
                ioe.printStackTrace();
            }

            return;
        }


        i.setAction(Intent.ACTION_VIEW);
        String mimetype= MimeTypeMap.getSingleton().getMimeTypeFromExtension(path.substring(path.lastIndexOf(".") + 1, path.length()));
        if(mimetype!= null && mimetype.length()>2){
            Log.i(BuildConfig.APPLICATION_ID, "mimetype " + mimetype + " for " + path);
            i.setDataAndType(Uri.fromFile(new File(path)), mimetype);
            startActivity(i);
        }


    }


    public void selectFileView(View v){
        selectedFileViews.add(v);
    }
    public void deselectFiles(){
        selectedFiles= new ArrayList<>();
        for(View v:selectedFileViews){
            v.findViewById(R.id.internal).setBackgroundColor(getResources().getColor(R.color.item));
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



    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        Log.i(BuildConfig.APPLICATION_ID, "onSaveInstance called");
        savedInstanceState.putStringArrayList("tabs", p);
        savedInstanceState.putString("currentPath", currentPath);
        savedInstanceState.putStringArrayList("selectedFiles", selectedFiles);
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        Log.i(BuildConfig.APPLICATION_ID, " onRestoreInstance called");

        ArrayList<String> s=savedInstanceState.getStringArrayList("selectedFiles");
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
        String newPath= currentPath.substring(0, currentPath.lastIndexOf("/"));
        if (newPath.equals("")){
            newPath="/";
        }
        setDirectoryView(newPath);
        refresh();

    }

    public void setDirectoryView(Bundle b){

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
        imageCache= new ImageCache(600);
        selectedFiles= new ArrayList<>();
        selectedFileViews= new ArrayList<>();
        handler= new android.os.Handler(getMainLooper());
        setContentView(R.layout.main_layout);

        /*
        if (BuildConfig.VERSION_CODE== Build.VERSION_CODES.M){
            if(!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE )==PackageManager.PERMISSION_GRANTED &&
                    this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED )){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},9);

            }
        }/**/


        getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar));
        Log.i(BuildConfig.APPLICATION_ID, "printing db");


        selectedFiles=new ArrayList<>();
        ip= new ThumbnailIdProvider(this);

        ip.print();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setDirectoryView("/");
        currentPath="/";
    }


    public void selectFileWithView(String path, View v) {
        if (selectedFiles.contains(path)){
            selectedFileViews.remove(v);
            selectedFiles.remove(path);
        }else{
            selectedFiles.add(path);
            if(v!=null){
                selectedFileViews.add(v);
            }
        }


        Snackbar sb=Snackbar.make(findViewById(R.id.drawer_layout), "there are " + selectedFiles.size() + " files selected", Snackbar.LENGTH_SHORT);
        sb.show();
    }

    public boolean FileIsSelected(String path) {
        return this.selectedFiles.contains(path);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.i(BuildConfig.APPLICATION_ID, "onItemclick called but not an etv");

    }


    public void onSearchClicked(View view) {

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


    public void search_for_files(View view) {

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
            Log.i(BuildConfig.APPLICATION_ID, "could not get search term");
            b.putString("term","xml");
        }

        setDirectoryView(b);
        ad.dismiss();
    }

    public void postMessage(String msg){
        Snackbar.make(findViewById(R.id.drawer_layout), msg, Snackbar.LENGTH_SHORT).show();
    }
    public void deleteDir(String path){
        Log.i(BuildConfig.APPLICATION_ID, "will delete dir");
            try {
                Runtime.getRuntime().exec("rm " + path + " -Rf");
            } catch (IOException e) {
                Toast.makeText(this,"could not delete dir",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        refresh();
    }

    public void clickDownloads(View view) {
        String path=(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath();
        setDirectoryView(path);
        Log.i(BuildConfig.APPLICATION_ID, " set to Download");
    }

    public void clickPictures(View view) {
        String path=(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)).getAbsolutePath();
        setDirectoryView(path);
        Log.i(BuildConfig.APPLICATION_ID, " set to Pictures");
    }

    public void clickRoot(View view) {
        setDirectoryView("/");
        Log.i(BuildConfig.APPLICATION_ID, " set to Root");
    }
    public void clickDCIM(View view) {
        String path=(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).getAbsolutePath();
        setDirectoryView(path);
    }

    public void clickMusic(View view) {
        String path=(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)).getAbsolutePath();
        setDirectoryView(path);

    }

    public void deleteFiles(MainActivity activity){
        Log.i(BuildConfig.APPLICATION_ID, "will now delete files");

        for (String s : selectedFiles) {
            try {
                Runtime.getRuntime().exec("rm " + s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        activity.deselectFiles();
        activity.refresh();
    }
    public void deleteFiles(MenuItem item) {
        if(selectedFiles==null || (selectedFiles.size()==0)){
            Toast.makeText(this,R.string.no_files_selected,Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder ab= new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.confirm_dialog,null);
        ((TextView)view.findViewById(R.id.title)).setText("Delete the following files:");
        ((TextView)view.findViewById(R.id.body)).setText(getSelectedFiles());
        final MainActivity activity=this;
        ab.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFiles(activity);
            }
        });
        ab.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refresh();

            }
        });

        ab.setView(view);

        AlertDialog ad=ab.create();
        ad.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                refresh();
            }
        });
        ad.show();
    }

    public void moveFiles(MainActivity activity){
        Log.i(BuildConfig.APPLICATION_ID, "will now move files");
        for (String s : selectedFiles) {
            try {
                Runtime.getRuntime().exec("mv " + s + " " + currentPath + "/");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        activity.deselectFiles();
        activity.refresh();
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

        final  MainActivity activity=this;
        ab.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        moveFiles(activity);
                    }
                });
            }
        });
        ab.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        ab.setView(view);

        AlertDialog ad=ab.create();
        ad.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        ad.show();
    }

    public void copyFiles(MainActivity activity){
        Log.i(BuildConfig.APPLICATION_ID, "will now copy files");
        for (String s : selectedFiles) {
            try {
                Runtime.getRuntime().exec("cp " + s + " " + currentPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        activity.deselectFiles();
        activity.refresh();
    }
    public void copyFiles(MenuItem item) {
        if(selectedFiles==null || (selectedFiles.size()==0)){
            Toast.makeText(this,R.string.no_files_selected,Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder ab= new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.confirm_dialog,null);
        ((TextView)view.findViewById(R.id.title)).setText(R.string.move_selected_files_here+":"+currentPath);
        ((TextView)view.findViewById(R.id.body)).setText(getSelectedFiles());
        final MainActivity activity=this;
        ab.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        copyFiles(activity);
                        refresh();
                    }
                });
            }
        });
        ab.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refresh();
            }
        });

        ab.setView(view);
        AlertDialog ad=ab.create();
        ad.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                refresh();
            }
        });
        ad.show();

    }

    public void deselectFiles(MenuItem item) {
        deselectFiles();
    }


    public void mkdir(String dirpath,MainActivity activity){
        try {

            Runtime.getRuntime().exec("mkdir "+dirpath);
        } catch (IOException e) {
            Toast.makeText(this,"could not make dir;"+dirpath,Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        activity.refresh();
    }

    public void mkdir(MenuItem item) {
        AlertDialog.Builder ab= new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.mkdir_dialog,null);
        final EditText et=(EditText)view.findViewById(R.id.searchdialogedittext);
        TextView title=(TextView)view.findViewById(R.id.title);
        title.setText(getString(R.string.make_directory) + currentPath);
        final MainActivity activity=this;
        ab.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(BuildConfig.APPLICATION_ID, "will now mkdir");
                        activity.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mkdir(currentPath + "/" + et.getText(),activity);
                                }
                        });
                        Toast.makeText(getApplicationContext(), "mkdir " + currentPath + "/" + et.getText(), Toast.LENGTH_SHORT).show();

                    }
                }
        );
        ab.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             refresh();
         }
     });

        ab.setView(view);
        AlertDialog ad=ab.create();
        ad.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                refresh();
            }
        });
        ad.show();

    }

    public void refresh(MenuItem item) {
        refresh();
    }

    public void sendSingleFile(String path){

        Intent i= new Intent();
        ArrayList<Uri> files= new ArrayList<>();
        files.add(Uri.fromFile(new File(path)));
        i.putParcelableArrayListExtra(Intent.EXTRA_STREAM,files);
        i.setType("*/*");
        i.setAction(Intent.ACTION_SEND_MULTIPLE);
        startActivity(Intent.createChooser(i,getString(R.string.send_file)));


    }
    public void send(MenuItem item) {
        ArrayList<Uri> files = new ArrayList<>();
        for ( String file: selectedFiles){

            files.add(Uri.fromFile(new File(file)));
        }
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND_MULTIPLE);
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM,files);
        share.setType("*/*");
        startActivity(Intent.createChooser(share,getString(R.string.send_files)));
    }

    public void decompressZipFile(String path) throws IOException {
        FileInputStream fis= new FileInputStream(path);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze= zis.getNextEntry();
        while (ze!=null){


            File f= new File(currentPath+"/"+ze.getName());

            File parentfile= f.getParentFile();
            parentfile.mkdirs();

            if(!f.exists()){
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(currentPath+"/"+ze.getName());
            byte [] data = new byte[1024];
            int length=0;
            length=zis.read(data,0,1024);
            fos.write(data,0,length);
            fos.close();
            zis.closeEntry();
            ze=zis.getNextEntry();
        }
        zis.close();
        fis.close();
    }

    public void createZipFile(String name, final MainActivity activity){

        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                activity.postMessage(activity.getBaseContext().getString(R.string.creating_zip_file));
            }
        });

        try {
            ZipOutputStream zos= new ZipOutputStream(new FileOutputStream(currentPath+"/"+name+".zip"));
            for ( String file: selectedFiles){

                ZipEntry ze= new ZipEntry(file);
                FileInputStream fis= new FileInputStream(file);

                zos.putNextEntry(ze);
                byte []data = new byte[1024];
                int length;
                while((length = fis.read(data))>= 0){
                    zos.write(data,0,length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                activity.postMessage(activity.getBaseContext().getString(R.string.done_creating_zip_file));
                activity.deselectFiles();
                activity.refresh();
            }
        });


    }

    public void CreateZipFileClicked(MenuItem item) {
        AlertDialog.Builder ab= new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.create_zip_file_dialog, null);
        ab.setView(view);
        final MainActivity activity=this;
        final EditText et= (EditText)view.findViewById(R.id.input);
        ab.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Thread thread= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        createZipFile(et.getText().toString(),activity);
                    }
                });
                thread.start();
            }
        });
        ab.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refresh();

            }
        });
        ab.create().show();

    }


    public void license(MenuItem item) {
        InputStream is=  getResources().openRawResource(R.raw.license);
        String li = "";
        byte []data= new byte[1024];
        int length;
        try {
            length=is.read(data,0,1024);
            while(length>0){
                Log.i(BuildConfig.APPLICATION_ID, li);
                li+=(new String(data));
                length=is.read(data,0,1024);
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        AlertDialog.Builder ab= new AlertDialog.Builder(this);
        TextView tv= new TextView(this);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(li);
        ab.setView(tv);
        ab.create().show();
    }
}
