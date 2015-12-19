package link.kjr.file_manager;

import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by kr on 12/17/15.
 */
public class ImageCache {

    Queue<Pair<String,Drawable>> queue;
    HashMap<String,Drawable> hashMap;
    int size;
    ImageCache(int size){
        queue= new ArrayDeque<>(size);
        hashMap= new HashMap<>(size);
        this.size=size;
    }
    public synchronized void put(String key, Drawable drawable){
        if(queue.size()==this.size){
            Log.i(BuildConfig.APPLICATION_ID, "ImageCache is full");
            Pair<String,Drawable> item = queue.poll();
            hashMap.remove(item.first);
        }
        queue.add(new Pair<>(key,drawable));
        hashMap.put(key, drawable);
    }

    public boolean contains(String key){
        return hashMap.containsKey(key);
    }
    public Drawable get(String key){
        if(hashMap.containsKey(key)){
            return hashMap.get(key);
        }
        return null;
    }
}
