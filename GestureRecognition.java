package com.example.vidhi.reminderr;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class GestureRecognition extends AppCompatActivity {

    private DrawView dv;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x=event.getX(),y=event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                dv.path.moveTo(x,y);
                dv.preX=x;dv.preY=y;
                Paths.addPath();
                Paths.addPoint(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                dv.path.quadTo(dv.preX,dv.preY,x,y);
                dv.preX=x;dv.preY=y;
                Paths.addPoint(x,y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        dv.invalidate();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FrameLayout fl=new FrameLayout(this);
        setContentView(fl);
        dv=new DrawView(this);
        fl.addView(dv);
        RelativeLayout rl=(RelativeLayout)getLayoutInflater().inflate(R.layout.activity_gesture_recognition,null);
        fl.addView(rl);
        Button
                reset=(Button)rl.findViewById(R.id.reset),
                recog=(Button)rl.findViewById(R.id.recog),
                purge=(Button)rl.findViewById(R.id.purge),
                num0=(Button)rl.findViewById(R.id.num0),
                num1=(Button)rl.findViewById(R.id.num1);

        reset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dv.resetPath();
                Paths.reset();
                dv.invalidate();
            }
        });
        recog.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SharedPreferences sp=getSharedPreferences("data",Context.MODE_PRIVATE);
                int num=sp.getInt("num",0);
                Recognizer.init();
                for (int n=1;n<=num;n++)
                {
                    String s=sp.getString("d"+n,null);
                    if (s==null)
                        continue;
                    int number=s.charAt(s.length()-1)-'0';
                    boolean[][] input=new boolean[Const.MAXN][Const.MAXN];
                    for (int i=0;i<Const.MAXN;i++)
                        for (int j=0;j<Const.MAXN;j++)
                            if (s.charAt(i*Const.MAXN+j)=='0')
                                input[i][j]=false;
                            else
                                input[i][j]=true;
                    Recognizer.add(input,number);
                }
                Recognizer.learn();
                boolean[][] b=Paths.process();
                String output="";
                if (b==null)
                {
                    dv.resetPath();
                    Paths.reset();
                    dv.invalidate();
                    return;
                }
                double[] d=Recognizer.recognize(b);
                double sum=0;
                for (int i=0;i<=Const.MAXNUM;i++)
                    sum+=d[i];
                for (int i=0;i<=Const.MAXNUM;i++)
                    d[i]=d[i]*1.0/sum;
                double maxp=0;
                int maxn=0;
                for (int i=0;i<=Const.MAXNUM;i++)
                {
                    output=output+i+":"+d[i]+"\n";
                    if (d[i]>maxp)
                    {
                        maxp=d[i];
                        maxn=i;
                    }
                }


                output=GestureRecognition.this.getResources().getString(R.string.result)+maxn+"\n"+GestureRecognition.this.getResources().getString(R.string.po_list)+"\n"+output;

                new AlertDialog.Builder(GestureRecognition.this)
                        .setMessage(output)
                        .setNeutralButton(R.string.ok,null)
                        .show();
                dv.resetPath();
                Paths.reset();
                dv.invalidate();

                final int a = maxn;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(a == 0){
                            finish();
                        }
                        else if(a == 1){
                            finish();
                        }
                    }
                }, 5000);


            }
        });
        purge.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new AlertDialog.Builder(GestureRecognition.this)
                        .setMessage(R.string.confirm_purge)
                        .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int which)
                            {
                                SharedPreferences sp=getSharedPreferences("data",Context.MODE_PRIVATE);
                                SharedPreferences.Editor e=sp.edit();
                                e.clear();
                                e.commit();
                                dv.resetPath();
                                Paths.reset();
                                dv.invalidate();
                            }
                        })
                        .setNegativeButton(R.string.cancel,null)
                        .show();
            }
        });
        class Listener implements View.OnClickListener
        {
            private int n;
            public Listener(int x)
            {
                n=x;
            }
            public void onClick(View v) {
                SharedPreferences sp=getSharedPreferences("data",Context.MODE_PRIVATE);
                SharedPreferences.Editor e=sp.edit();
                int num=sp.getInt("num",0);
                num++;
                e.putInt("num",num);
                String s="";
                boolean[][] b=Paths.process();
                if (b==null)
                {
                    dv.resetPath();
                    Paths.reset();
                    dv.invalidate();
                    return;
                }
                for (int i=0;i<Const.MAXN;i++)
                {
                    for (int j=0;j<Const.MAXN;j++)
                        if (b[i][j]==true)
                        {
                            s=s+"1";
                        }
                        else
                        {
                            s=s+"0";
                        }
                }
                s=s+n;
                e.putString("d"+num,s);
                e.commit();
                new AlertDialog.Builder(GestureRecognition.this)
                        .setMessage(R.string.learn_sample)
                        .setNeutralButton(R.string.ok,null)
                        .show();
                dv.resetPath();
                Paths.reset();
                dv.invalidate();
            }
        }
        Button[] buttonArray={num0,num1};
        for (int i=0;i<=1;i++)
            buttonArray[i].setOnClickListener(new Listener(i));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gesture_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                new AlertDialog.Builder(GestureRecognition.this)
                        .setTitle(R.string.menu_about)
                        .setMessage(R.string.about)
                        .setNeutralButton(R.string.ok,null)
                        .create().show();
                return true;
            case R.id.menu_help:
                new AlertDialog.Builder(GestureRecognition.this)
                        .setTitle(R.string.menu_help)
                        .setMessage(R.string.help)
                        .setNeutralButton(R.string.ok,null)
                        .create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
