package homework2.person.timer;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NumberPicker.OnValueChangeListener {

    public static final String UI_UPDATE_ACTION = "main.receiver.UPDATAUI";
    public static final String TIMER_FINISH_ACTION = "main.receiver.TIMERFINISH";

    private TextView textView_minute, textView_second;
    private Button btn_start_stop, btn_pause_resume;
    private LinearLayout layout_timeView;

    private SharedPreferences preferences;
    private static final String data_minute = "MINUTE";
    private static final String data_second = "SECOND";
    private static final String data_startBtn = "START_BTN";
    private static final String data_pauseBtn = "PAUSE_BTN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView_minute = (TextView) findViewById(R.id.textView);
        textView_second = (TextView) findViewById(R.id.textView3);
        btn_start_stop = (Button) findViewById(R.id.button2);
        btn_pause_resume = (Button) findViewById(R.id.button);
        layout_timeView = (LinearLayout) findViewById(R.id.timeview_layout);

        btn_start_stop.setOnClickListener(this);
        btn_pause_resume.setOnClickListener(this);
        layout_timeView.setOnClickListener(this);

        init_view();

        registerBroadcastReceiver();
    }

    public void registerBroadcastReceiver(){
        UIReceiver receiver = new UIReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UI_UPDATE_ACTION);
        filter.addAction(TIMER_FINISH_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.timeview_layout:
                if(btn_start_stop.getText().equals(getResources().getString(R.string.start)))
                    createNumPickerDialog();
                break;
            case R.id.button2:
                if(btn_start_stop.getText().equals(getResources().getString(R.string.start))){
                    startServiceTimer();

                    btn_start_stop.setText(getResources().getString(R.string.stop));
                    btn_pause_resume.setEnabled(true);
                }
                else if(btn_start_stop.getText().equals(getResources().getString(R.string.stop))){
                    stopServiceTimer();

                    init_view();
                }
                break;
            case R.id.button:
                if(btn_pause_resume.getText().equals(getResources().getString(R.string.pause))){
                    stopServiceTimer();

                    btn_pause_resume.setText(getResources().getString(R.string.resume));
                }
                else if(btn_pause_resume.getText().equals(getResources().getString(R.string.resume))){
                    startServiceTimer();

                    btn_pause_resume.setText(getResources().getString(R.string.pause));
                }
                break;
        }
    }

    public void startServiceTimer(){
        int minute = Integer.parseInt(textView_minute.getText().toString());
        int second = Integer.parseInt(textView_second.getText().toString());
        int countDownTime = minute*60 + second;

        Intent intent = new Intent(MainActivity.this, TimerService.class);
        intent.setAction(TimerService.TIMER_START);
        intent.putExtra("countDownTime", countDownTime);
        startService(intent);
    }

    public void stopServiceTimer(){
        Intent intent = new Intent(MainActivity.this, TimerService.class);
        intent.setAction(TimerService.TIMER_STOP);
        startService(intent);
        stopService(intent);
    }

    public void init_view(){
        textView_minute.setText(getResources().getString(R.string.timer_init));
        textView_second.setText(getResources().getString(R.string.timer_init));
        btn_start_stop.setText(getResources().getString(R.string.start));
        btn_pause_resume.setText(getResources().getString(R.string.pause));
        btn_pause_resume.setEnabled(false);
    }

    public void createNumPickerDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("請選擇倒數時間");
        dialog.setContentView(R.layout.numpicker_dialog);
        NumberPicker picker_minute = (NumberPicker) dialog.findViewById(R.id.numberPicker);
        NumberPicker picker_second = (NumberPicker) dialog.findViewById(R.id.numberPicker2);
        picker_minute.setMaxValue(59);
        picker_minute.setMinValue(0);
        picker_second.setMaxValue(59);
        picker_second.setMinValue(0);
        picker_minute.setClickable(false);

        String s1 = textView_minute.getText().toString();
        picker_minute.setValue(Integer.parseInt(s1));

        String s2 = textView_second.getText().toString();
        picker_second.setValue(Integer.parseInt(s2));

        picker_minute.setOnValueChangedListener(this);
        picker_second.setOnValueChangedListener(this);

        dialog.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch(picker.getId()){
            case R.id.numberPicker:
                if(picker.getValue()<10){
                    textView_minute.setText("0"+picker.getValue());
                }
                else textView_minute.setText(""+picker.getValue());
                break;
            case R.id.numberPicker2:
                if(picker.getValue()<10){
                    textView_second.setText("0"+picker.getValue());
                }
                else textView_second.setText(""+picker.getValue());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        readData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    public void saveData(){
        preferences = getPreferences(0);
        preferences.edit()
                .putString(data_minute, textView_minute.getText().toString())
                .putString(data_second, textView_second.getText().toString())
                .putString(data_startBtn, btn_start_stop.getText().toString())
                .putString(data_pauseBtn, btn_pause_resume.getText().toString())
                .commit();
    }

    public void readData(){
        preferences = getPreferences(0);
        textView_minute.setText(preferences.getString(data_minute,getResources().getString(R.string.timer_init)));
        textView_second.setText(preferences.getString(data_second,getResources().getString(R.string.timer_init)));
        btn_start_stop.setText(preferences.getString(data_startBtn,getResources().getString(R.string.start)));
        btn_pause_resume.setText(preferences.getString(data_pauseBtn,getResources().getString(R.string.stop)));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class UIReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(UI_UPDATE_ACTION)){
                Bundle bundle = intent.getExtras();
                int time = bundle.getInt("time");
                int minute = time/60;
                int second = time%60;

                if(minute<10){
                    textView_minute.setText("0"+minute);
                }
                else textView_minute.setText(""+minute);

                if(second <10){
                    textView_second.setText("0"+second);
                }
                else textView_second.setText(""+second);
            }
            else if(action.equals(TIMER_FINISH_ACTION)){
                init_view();
            }
        }
    }
}
