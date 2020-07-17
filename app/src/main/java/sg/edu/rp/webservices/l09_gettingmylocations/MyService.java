package sg.edu.rp.webservices.l09_gettingmylocations;

import android.app.Service;
import android.content.Intent;
import android.net.MailTo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    boolean started;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service","Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false){
            started = true;
            Toast.makeText(MyService.this,"Service started",Toast.LENGTH_SHORT).show();
            Log.d("Service","Service is started");
        }else{
            Toast.makeText(MyService.this,"Service is running",Toast.LENGTH_SHORT).show();
            Log.d("Service","Service is running");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(MyService.this,"Service is stopped",Toast.LENGTH_SHORT).show();
        Log.d("Service","Service exited");
        super.onDestroy();
    }
}
