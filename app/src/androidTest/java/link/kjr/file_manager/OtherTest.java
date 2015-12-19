package link.kjr.file_manager;

import android.test.InstrumentationTestCase;

/**
 * Created by kr on 12/11/15.
 */
public class OtherTest extends InstrumentationTestCase{
 public void TestCase(String name){
     try {

         test();

     } catch (Exception e){
         e.printStackTrace();;
     }
     return;
 }
   public OtherTest(){
        try {
            test();


        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void test() throws Exception{
        ThumbnailIdProvider ip= new ThumbnailIdProvider(getInstrumentation().getContext());
        ip.getDrawableIdforSuffix("jpg");
    }
}
