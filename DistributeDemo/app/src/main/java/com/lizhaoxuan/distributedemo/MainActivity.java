package com.lizhaoxuan.distributedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhaoxuan.distribute.DistributePool;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DistributePool distributePool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);

        distributePool = DistributePool.getInstance();
        distributePool.addObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:btn1Click();
                break;
            case R.id.btn2:btn2Click();
                break;
        }
    }

    /**
     * 分发User数据包
     */
    private void btn1Click(){
        distributePool.post(new UserEvent(1,"I am User"));
    }

    /**
     * 分发Student数据包
     */
    private void btn2Click(){
        distributePool.post(new StudentEvent(1,"I am Student"));
    }

    public void disInboxUserThread(UserEvent data){
        Toast.makeText(this,"处理User数据包：User:"+data.getName(),Toast.LENGTH_SHORT).show();
    }

    public void disInboxStudentThread(StudentEvent data){
        Toast.makeText(this,"处理Student数据包：Student:"+data.getClassName(),Toast.LENGTH_SHORT).show();
    }
}
