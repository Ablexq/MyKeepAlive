package com.xq.mykeepalive;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.btn);
        button.setText(getClass().getSimpleName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduler();
            }
        });
    }

    private void scheduler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), MyJobService.class.getName()));
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);//只有在满足指定的网络条件时才会被执行
            builder.setPersisted(true);//告诉系统当你的设备重启之后你的任务是否还要继续执行,默认false
//            builder.setRequiresCharging(true);//只有当设备在充电时这个任务才会被执行，默认false
//            builder.setRequiresDeviceIdle(true);//只有当用户没有在使用该设备且有一段时间没有使用时才会启动该任务，默认false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//每隔多长时间执行一次（毫秒）
                //android N之后时间必须在15分钟以上
                builder.setPeriodic(15 * 60 * 1000);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setPeriodic(60 * 100);
            }
//            builder.setMinimumLatency(2000);//延迟执行时间（毫秒）
//            builder.setOverrideDeadline(2000);//多长时间后一定执行（毫秒）

            JobInfo jobInfo = builder.build();

            int schedule = jobScheduler.schedule(jobInfo);
            switch (schedule) {
                case JobScheduler.RESULT_SUCCESS:
                    System.out.println("=================RESULT_SUCCESS====================");
                    break;
                case JobScheduler.RESULT_FAILURE:
                    System.out.println("=================RESULT_FAILURE====================");
                    break;
            }
        }
    }
}
