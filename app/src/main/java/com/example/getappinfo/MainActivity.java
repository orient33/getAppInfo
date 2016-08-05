
package com.example.getappinfo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getappinfo.other.OtherActivity;
import com.example.palette.MyPalette;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int FLAG_DATA = 0;
    private static final int FLAG_SYSTEM = 1;
    private static final int FLAG_ALL = 2;
    ListView mListView;
    Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mListView = (ListView) findViewById(R.id.list_view);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        // new TestEventBus();
        LauncherApps la = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        List<LauncherActivityInfo> lai = la
                .getActivityList(null, android.os.Process.myUserHandle());
        final AppAdapter aa = new AppAdapter(this, lai);
        mListView.setAdapter(aa);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 2) {
                    doFlag(position);
                    return;
                }
                aa.setFilter(position);
                Toast.makeText(MainActivity.this, "onItemSelected=" + position, 0).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "onNothingSelected", 0).show();
            }
        });
        /*
        final Intent target =  new Intent(this,MainActivity.class);
        Intent un = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        un.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
        un.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Test");
        sendBroadcast(un);
        //
        Runnable run = new Runnable() {
            public void run(){
                Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Test");
                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,target);
                sendBroadcast(intent);
            }
        };
        this.getWindow().getDecorView().postDelayed(run, 1000);

        PackageManager pm = getPackageManager();
        ComponentName cn = new ComponentName(this, TActivity.class);
        int state = pm.getComponentEnabledSetting(cn);
        final int newState;
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            Toast.makeText(this, "从禁用更改为启用", Toast.LENGTH_SHORT).show();
        } else {
            newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            Toast.makeText(this, "从启用更改为禁用", Toast.LENGTH_SHORT).show();
        }
        pm.setComponentEnabledSetting(cn, newState, PackageManager.DONT_KILL_APP);
        /*
         * // for壁纸 parter PackageManager pm = getPackageManager(); String action =
         * "com.android.launcher3.action.PARTNER_CUSTOMIZATION"; final Intent intent = new
         * Intent(action); String result="--"; for (ResolveInfo info :
         * pm.queryBroadcastReceivers(intent, 0)) { if (info.activityInfo != null &&
         * (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) { final
         * String packageName = info.activityInfo.packageName; result = packageName +"========" +
         * info.activityInfo;//,Toast.LENGTH_LONG).show();// res); } } Toast.makeText(this, result,
         * Toast.LENGTH_LONG).show();
         */
        /*MTK 未读测试
        ComponentName cn = new ComponentName("com.android.email",
                "com.android.email.activity.Welcome");
        Intent intent = new Intent("com.mediatek.action.UNREAD_CHANGED");
        intent.putExtra("com.mediatek.intent.extra.UNREAD_NUMBER", 1);
        intent.putExtra("com.mediatek.intent.extra.UNREAD_COMPONENT", cn);
        sendBroadcast(intent);
        Toast.makeText(this, "send unread. " + cn, 0).show();
        */

        /*
        String p = getDownloadPathForPPTVPhone(this);
        Toast.makeText(this, "Default="+p, Toast.LENGTH_LONG).show(); */
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndTypeAndNormalize(Uri.parse("file://sdcard/a.mp4"), "video/*");
        List<ResolveInfo> ls = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo ri : ls) {
            android.util.Log.i("df", "" + ri.activityInfo.packageName +", "+ ri.activityInfo.name);
        }*/
        /*
        try {
            String s=String.format("?username=%s&from=%s&version=%s&format=%s",
                    URLEncoder.encode("_50934936%40qq", "UTF-8"), "aph", "aa", "xml");
            Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        } catch (UnsupportedEncodingException e) {
        }*/
        //

        PackageManager pm = getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo("/sdcard/pptv_plugins/app-debug.apk",
                PackageManager.GET_ACTIVITIES);
        if (pi != null && pi.activities != null && pi.activities.length > 0) {
            for (ActivityInfo ai : pi.activities) {
                ai.theme = ai.getThemeResource();
                Log.e("dd",ai.name+", theme :" +Integer.toHexString(ai.theme));
            }
        }
    }

    void doFlag(int position) {
        startActivity(new Intent(this, OtherActivity.class));
    }

    public static String getDownloadPathForPPTVPhone(Context context) {
        Uri uri = Uri.parse("content://com.android.settings.provider.fields/hide_fields");
        String selection = " hidefilds_name = 'default_storage_path' ";
        String path = "/storage/sdcard1/";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, selection, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("hidefilds_value");
                if (columnIndex != -1) {
                    path = cursor.getString(columnIndex);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("df", "" + e);
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return path;
    }

    class AppAdapter extends BaseAdapter implements View.OnClickListener {

        final Context context;
        final List<LauncherActivityInfo> data = new ArrayList<LauncherActivityInfo>();
        final List<LauncherActivityInfo> all;
        final int dm;

        public AppAdapter(Context ctx, List<LauncherActivityInfo> d) {
            context = ctx;
            dm = ctx.getResources().getDisplayMetrics().densityDpi;
            all = d;
            setFilter(FLAG_DATA);
        }

        void setFilter(int flag) {
            if (flag < 0 || flag > 2) throw new RuntimeException("Flags error. ");
            data.clear();
            if (flag == FLAG_ALL) {
                data.addAll(all);
            } else {
                for (LauncherActivityInfo item : all) {
                    int flags = item.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM;
                    if ((flags != 0 && flag == FLAG_SYSTEM) ||
                            (flags == 0 && flag == FLAG_DATA)) {
                        data.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public LauncherActivityInfo getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.app_info, null);
                convertView.setOnClickListener(this);
            }
            final LauncherActivityInfo info = getItem(position);
            Drawable drawable = info.getIcon(dm);
            String cnn = info.getComponentName().toShortString();
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageDrawable(drawable);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(++position + ")  " + info.getLabel());
            TextView cn = (TextView) convertView.findViewById(R.id.cn);
            cn.setText(cnn);
            // int flags = info.getApplicationInfo().flags;
            // cnn +=", system="+((flags&ApplicationInfo.FLAG_SYSTEM )!=0) // 系统应用
            // +",,update = "+((flags& ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) !=0);//经升级的系统应用
//            com.example.palette.MyPalette.findThemeColor(drawable, cn, cnn);
            convertView.setTag(info);
            convertView.setTag(R.drawable.gift_icon, position);
            return convertView;
        }

        @Override
        public void onClick(View v) {
//            String tag = v.getTag().toString();
//            Toast.makeText(v.getContext(), tag, Toast.LENGTH_SHORT).show();
            MyPalette.sLauncherActivityInfo = (LauncherActivityInfo) v.getTag();
            startActivity(new Intent(MainActivity.this, AppInfoActivity.class));
            // Log.i("dd", "click "+tag +", post MyEvent");
            // EventBus.getDefault().post(new TestEventBus.MyEvent(tag));
            File file = new File("/storage/sdcard0/");
            File sub[] = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".png");
                }
            });
            int pos = (Integer) v.getTag(R.drawable.gift_icon);
            int index = pos == 0 ? 0 : 1;
            Intent i = new Intent(MainActivity.this, AppInfoActivity.class);
            i.putExtra(AppInfoActivity.Key, sub[index].getAbsolutePath());
            startActivity(i);
        }
    }
}
