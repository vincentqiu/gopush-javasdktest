package com.t.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import com.ks.gopush.cli.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @StringRes
    String hello_world;

    @ViewById
    TextView helloTextView;

    @AfterViews
       void afterViews() {
        Date now = new Date();
        String helloMessage = String.format(hello_world, now.toString());
        System.out.print(helloMessage);
        helloTextView.setText(helloMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        hello_world = "hello world!";

         gopushInit();
         testSync();


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Before
    public void gopushInit() {

        local.set(new GoPushCli("192.168.40.122", 8090, "QXF", 30, 0, 0,
                new Listener() {
                    @Override
                    public void onOpen() {
                        System.err.println("dang dang dang dang~");
                    }

                    @Override
                    public void onOnlineMessage(PushMessage message) {
                        hello_world += "\nonline message : " + message.getMsg();

                        runOnUiThread(  new Runnable(){
                            @Override
                            public  void run(){
                                helloTextView.setText(hello_world);
                            }

                        });
                        //helloTextView.setText(hello_world);
                        System.err.println("online message: "
                                + message.getMsg());
                    }

                    @Override
                    public void onOfflineMessage(ArrayList<PushMessage> messages) {

                        if (messages != null)
                            for (PushMessage message : messages) {
                                hello_world += "\noffline message : " + message.getMsg();

                                runOnUiThread(  new Runnable(){
                                    @Override
                                    public  void run(){
                                        helloTextView.setText(hello_world);
                                    }

                                });

                                System.err.println("offline message: "
                                        + message.getMsg());
                            }
                    }

                    @Override
                    public void onError(Throwable e, String message) {
                        Assert.fail(message);
                    }

                    @Override
                    public void onClose() {
                        System.err.println("pu pu pu pu~");
                    }
                }));
    }

    // @Test
    public void testNoSync() {
        GoPushCli cli = local.get();
        cli.start(false);

        Assert.assertTrue("获取节点失败", cli.isGetNode());
        Assert.assertTrue("握手失败", cli.isHandshake());
        cli.destory();
    }

    @Test
    public void testSync()  {

        final GoPushCli cli = local.get();
        new Thread() {
            public void run() {
                cli.start(true);
            }
        }.start();

        //try {
        //    TimeUnit.SECONDS.sleep(10000000);
        //} catch (InterruptedException e) {
        //}
        //Assert.assertTrue("获取节点失败", cli.isGetNode());
        //Assert.assertTrue("握手失败", cli.isHandshake());
        //cli.destory();
    }

    private ThreadLocal<GoPushCli> local = new ThreadLocal<GoPushCli>();
}
