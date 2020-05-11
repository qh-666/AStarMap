package com.example.astarmap;
import android.graphics.Bitmap;
import android.widget.ImageView;

//we could transit more paremeters to asytask
public class TaskParameters {
    int[][] MapMatrix;
    int[] current;
    int[] destination;
    ImageView map;
    private Bitmap temp;
    private static Bitmap store_map;
    TaskParameters(int[][] a, int[] b, int[] c, ImageView d, Bitmap e){
        MapMatrix=a;
        current=b;
        destination=c;
        map=d;
        temp=e;
    }

    static Bitmap getStore_map() {
        return store_map;
    }

    static void setStore_map(Bitmap store_map) {
        System.out.println("Set");
        TaskParameters.store_map = store_map;
    }
}
