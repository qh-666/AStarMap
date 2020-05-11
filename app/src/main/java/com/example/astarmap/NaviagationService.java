package com.example.astarmap;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import static android.graphics.Bitmap.Config.RGBA_F16;

public class NaviagationService extends Service {
    int[][] MapMatrix;
    Bitmap bitmap,temp;
    TaskParameters taskParameters;

    public NavigationTask navigationTask;

    private pathfindBinder mBinder=new pathfindBinder();
    public NaviagationService() {
        super();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("NavigationService","Start");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("NavigationService","onbind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d("NavigationService","Destroy");
        if(navigationTask!=null) navigationTask.cancel(true);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("NavigationService","unbind");
        return super.onUnbind(intent);
    }

    class pathfindBinder extends Binder{
        pathfindBinder(){
            super();
        }
        public void imagetoArray(ImageView map){
            //把图片转换成bitmap,转换成int[],同时把图片变为transparent
            bitmap = ((BitmapDrawable) map.getDrawable()).getBitmap();
            temp=bitmap.copy(RGBA_F16,true);
            int height=bitmap.getHeight();
            int width=bitmap.getWidth();
            MapMatrix=new int[width][height];
            int count=0;
            for(int i=0;i<width;i++){
                for(int j=0;j<height;j++){
                    int pixel = bitmap.getPixel(i,j);
                    count++;
                    if(pixel==-1) {//白色 路径
                        MapMatrix[i][j]=0;
                    }else MapMatrix[i][j]=1;
                    temp.setPixel(i,j,0);
                }
            }
            map.setImageBitmap(temp);
            TaskParameters.setStore_map(temp);
        }

        public void findpath(ImageView map,int[] current,int[] destination){
            //设置之后可以异步显示路径
            navigationTask=new NavigationTask();
            taskParameters=new TaskParameters(MapMatrix,current,destination,map,TaskParameters.getStore_map());
            navigationTask.execute(taskParameters);
        }
    }
}
