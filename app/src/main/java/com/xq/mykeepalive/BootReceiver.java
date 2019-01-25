package com.xq.mykeepalive;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;


/*接收到开机广播后延时5秒执行任务*/
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "boot complete", Toast.LENGTH_SHORT).show();
        doService(context);
    }

    private void doService(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(context, MyJobService.class));  //指定哪个JobService执行操作
        builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(5000)); //执行的最小延迟时间
        builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(5000));  //执行的最长延时时间
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);  //任何网络状态
        builder.setRequiresCharging(false); // 未充电状态
        jobScheduler.schedule(builder.build());
    }
}