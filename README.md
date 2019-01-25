

# 双进程保活aidl版 （android5.0以下）

# 双进程保活jni版 （android5.0以下）

# 保活 JobService版 （android5.0++）

参考：

[在Android 5.0中使用JobScheduler](https://blog.csdn.net/bboyfeiyu/article/details/44809395)

通常在5.0之前，我们可以使用广播或者闹钟等方式让我们的进程防杀自启，

而5.0以后的Android系统，我们就可以使用JobService，JobService它是Android5.0以后新增的一个服务

原理: JobService是官方推荐的方式，即使app完成被杀死的状态下也能调用起来，本质是向系统注册一个任务。

通过getSystemService拿到系统的JobScheduler。然后通过JobInfo.Buidler进行构造。

需要注意的是一定要指定被触发的条件。比如:设备充电中、空闲状态、连接wifi... 非常类似以前的广播保护原理。

但是实现不一样。这次是我们反向注册给系统，而不是接收系统的广播。

注意：jobScheduler无法兼容Android 5.0以下的设备。

### JobScheduler 

JobScheduler 是Job的调度类，负责执行，取消任务等逻辑


```
public abstract class JobScheduler {
   
    public static final int RESULT_FAILURE = 0;
  
    public static final int RESULT_SUCCESS = 1;

	/*
	*参数：JobInfo采用Builder的设计模式，对需要执行的Job任务信息进行的封装。
	*返回值：RESULT_SUCCESS=1   RESULT_FAILURE=0 表示执行成功或失败
	*/
    public abstract int schedule(JobInfo job);

    @SystemApi
    public abstract int scheduleAsPackage(JobInfo job, String packageName, int userId, String tag);

	/**通过指定的jobId取消Job任务*/
    public abstract void cancel(int jobId);
	
	/**取消所有的Job任务*/
    public abstract void cancelAll();

	/**获取所有的未执行的Job任务*/
    public abstract @NonNull List<JobInfo> getAllPendingJobs();

	/**获取指定的Job未执行的任务*/
    public abstract @Nullable JobInfo getPendingJob(int jobId);
}

```

### JobService

JobService中的代码实现不多,内部使用AIDL + Handler的方式来传递消息

```
public abstract class JobService extends Service {

    /**
     * Job services must be protected with this permission
     */
    public static final String PERMISSION_BIND =
            "android.permission.BIND_JOB_SERVICE";
			
	//.........		

	//开始jobScheduler的方法
    public abstract boolean onStartJob(JobParameters params);

	//停止jobScheduler的方法
    public abstract boolean onStopJob(JobParameters params);

	//完成jobScheduler的方法
    public final void jobFinished(JobParameters params, boolean needsReschedule) {
        ensureHandler();
        Message m = Message.obtain(mHandler, MSG_JOB_FINISHED, params);
        m.arg2 = needsReschedule ? 1  0;
        m.sendToTarget();
    }
}
```

### JobInfo


```
JobScheduler jobScheduler = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE)


private JobInfo initJob(int jobId) {
    jobId++;
    ComponentName componentName = new ComponentName(getPackageName(), MyJobService.class.getName());
    return new JobInfo.Builder(jobId, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)//设置需要的网络条件，默认NETWORK_TYPE_NONE
            //
            .setPeriodic((3000))//设置间隔时间
            //
            .setMinimumLatency(3000)// 设置任务运行最少延迟时间
            .setOverrideDeadline(50 * 1000)// 设置deadline，若到期还没有达到规定的条件则会开始执行
            .setRequiresCharging(false)// 设置是否充电的条件,默认false
            .setRequiresDeviceIdle(false)// 设置手机是否空闲的条件,默认false
            /*  设备重启 <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />  */
            .setPersisted(true)//设备重启之后你的任务是否还要继续执行
            .build();
}

JobInfo jobInfo = initJob(startId);
if (jobScheduler.schedule(jobInfo) <= 0) {
    Log.e(TAG, "=========MyJobService  工作失败");
} else {
    Log.e(TAG, "=========MyJobService  工作成功");
}
```


setOverrideDeadline / setMinimumLatency 与 setPeriodic 不可同时设置，否则会报以下错误：
```
java.lang.IllegalArgumentException Can't call setMinimumLatency() on a periodic job

java.lang.IllegalArgumentException Can't call setOverrideDeadline() on a periodic job.
```


```

/** 默认条件，不管是否有网络这个作业都会被执行 */
public static final int NETWORK_TYPE_NONE = 0;
/** 任意一种网络这个作业都会被执行 */
public static final int NETWORK_TYPE_ANY = 1;
/** 不是蜂窝网络( 比如在WIFI连接时 )时作业才会被执行 */
public static final int NETWORK_TYPE_UNMETERED = 2;
/** 不在漫游时作业才会被执行 */
public static final int NETWORK_TYPE_NOT_ROAMING = 3;

```



### 示例

```


```


在大多数Android5.0以上手机杀死APP进程后，仍然可以唤醒，手机重启在部分手机可是可以唤醒APP的。










