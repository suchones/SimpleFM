package link.kjr.file_manager;

import android.app.ActionBar;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public void onc(View v){
        rootfile=rootfile.getParentFile();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    FileAdapter fa ;
    File rootfile=android.os.Environment.getRootDirectory();
    public void  setFile(File f){
        Log.i("file_app"," set file called");
        rootfile=f;
    }
    public File getRootfile(){
        return rootfile;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this,TabsActivity.class));


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==R.id.parent_dir_view){
            Log.i("blah","onOptionItemSelected called, name of parent" +rootfile.getParentFile().getName());
            rootfile=rootfile.getParentFile();

        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ond(MenuItem item) {
        Log.i("blah","ond called, name of parent" +rootfile.getParentFile().getName());
        fa.notifyDataSetChanged();
        rootfile=rootfile.getParentFile();


    }

    public void onp(MenuItem item) {
    }
}
