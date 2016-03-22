package homework2.person.timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by WEI-ZHE on 2016/3/21.
 */
public class TimerService extends Service {

    public final static String TIMER_START = "service.timer.START";
    public final static String TIMER_STOP = "service.timer.STOP";

    private CountDownTimer timer;
    private Intent timerIntent;
    private Bundle bundle;
    private int coutDownTime;

    @Override
    public void onCreate() {
        super.onCreate();

        timerIntent = new Intent();
        bundle = new Bundle();
        coutDownTime = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch(action){
            case TIMER_START:
                Bundle bundle = intent.getExtras();
                coutDownTime = bundle.getInt("countDownTime");
                startTimer();
                break;
            case TIMER_STOP:
                stopTimer();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startTimer(){
        timer = new CountDownTimer(coutDownTime*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bundle.putInt("time", (int) (millisUntilFinished / 1000));
                timerIntent.putExtras(bundle);
                timerIntent.setAction(MainActivity.UI_UPDATE_ACTION);
                sendBroadcast(timerIntent);
            }

            @Override
            public void onFinish() {
                timerIntent.setAction(MainActivity.TIMER_FINISH_ACTION);
                sendBroadcast(timerIntent);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                long[] vibrate_effect = {1000, 500, 1000, 400, 1000, 300, 1000, 200, 1000, 100};
                builder.setSmallIcon(R.drawable.clock)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(getResources().getString(R.string.notify_title))
                        .setContentText(getResources().getString(R.string.notify_content))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setVibrate(vibrate_effect)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(0, builder.build());

                stopSelf();
            }
        }.start();
    }

    public void stopTimer(){
        if(timer!=null)
            timer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
