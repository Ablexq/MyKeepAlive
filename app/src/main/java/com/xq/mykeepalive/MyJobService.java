package com.xq.mykeepalive;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//API需要在21及以上
public class MyJobService extends JobService {

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            System.out.println("==================handleMessage=====================");
            JobParameters param = (JobParameters) msg.obj;
            //jobFinished:
            //params参数是从JobService的onStartJob(JobParameters params)的params传递过来的
            //needsRescheduled参数是让系统知道这个任务是否应该在最初的条件下被重复执行。
            jobFinished(param, true);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("==================onStartCommand=====================");
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        System.out.println("==================onStartJob=====================");
        Message m = Message.obtain();
        m.obj = params;
        handler.sendMessage(m);

        //唤醒极光推送服务或者其他
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        System.out.println("==================onStopJob=====================");
        handler.removeCallbacksAndMessages(null);
        return false;
    }
}