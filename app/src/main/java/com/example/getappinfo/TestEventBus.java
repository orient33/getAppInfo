
package com.example.getappinfo;

import android.util.Log;

import de.greenrobot.event.EventBus;

public class TestEventBus {
    public TestEventBus() {
        EventBus.getDefault().register(this);
    }

    public void onEvent(MyEvent e) {
        Log.i("dd", "onEvent : " + e);
    }

    public static class MyEvent {
        String content;
        long time;

        public MyEvent(String c) {
            content = c;
            time = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "MyEvent : " + content + ",  " + time;
        }
    }
}
