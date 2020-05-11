package com.example.astarmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayDeque;
import java.util.Deque;

public class NavigationTask extends AsyncTask<TaskParameters, Node,String>{
    @SuppressLint("StaticFieldLeak")
    private static ImageView map;
    private Bitmap temp;
    private Paint paint;
    private Canvas canvas;
    @SuppressLint("ResourceAsColor")
    @Override
    protected String doInBackground(TaskParameters... taskParameters) {
        Log.d("NavigationTask","doInbackground");
        //pathfinding
        int[][] mapMatrix = taskParameters[0].MapMatrix;
        map=taskParameters[0].map;
        int[] current = taskParameters[0].current;
        int[] destination = taskParameters[0].destination;
        temp=TaskParameters.getStore_map();
        //需要bitmap置为0，不知道原因。
        for(int i=0;i<temp.getWidth();i++){
            for(int j=0;j<temp.getHeight();j++){
                temp.setPixel(i,j,0);
            }
        }
        AStar aStar=new AStar(mapMatrix, current, destination);
        Boolean result=aStar.search();
        System.out.println(result);
        Deque<Node> path = new ArrayDeque<>();
        if(result) path =aStar.findpath();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //画图
        paint = new Paint(Paint.ANTI_ALIAS_FLAG); // 画笔抗锯齿
        int color = Color.parseColor("#008B00");
        paint.setColor(color);
        if(canvas==null){
            Log.d("canvas","newCanavs");
            canvas = new Canvas(temp);
        }
        //起始
        canvas.drawCircle(current[0], current[1], 2.1f, paint);
        //路径
        while(!path.isEmpty()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Node curretloc= path.pollLast();
            publishProgress(curretloc);
        }
        //终点
        canvas.drawCircle(destination[0], destination[1], 2.1f, paint);
        return null;
    }

    @Override
    protected void onProgressUpdate(Node... nodes) {
        Log.d("NavigationTask","ProgressUpdate");
        //bitmap should be mutable
        //location画圆
        canvas.drawCircle(nodes[0].getRow(),nodes[0].getColumn(),1.3f,paint);
        map.setImageBitmap(temp);
        //super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("NavigationTask","PostExecute");
        map.setImageBitmap(temp);
        //You cannot recycle the Bitmap while using it on in the UI, the Bitmap has to be kept in memory.
        // Android will in most cases handle recycling just fine,
        // but if you need to recycle yourself you need to make sure to not use the Bitmap instance afterwards
        // (as in this case where the Bitmap instance will be rendered later on).
        //imageview持有bitmap引用
//        if (!temp.isRecycled()) {
//            temp.recycle();
//        }
        super.onPostExecute(s);
    }

    @Override
    protected void onCancelled() {
        Log.d("NavigationTask","onCancelled");
        super.onCancelled();
    }
}
