
package com.example.getappinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palette.MyPalette;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class AppInfoActivity extends Activity implements View.OnClickListener {
    public static final String Key="file";
    LauncherActivityInfo mInfo;
    TextView mResult;
    Drawable mIcon;
    Bitmap mBitmap;
    LinearLayout mPaletteColorContainer;
    String sign[] = new String[]{"1", "2"};

    protected void onCreate(android.os.Bundle savedInstanceState) {
        MyPalette.init();
        String file = getIntent().getStringExtra(Key);
        if(TextUtils.isEmpty(file)) {
            mIcon = mInfo.getBadgedIcon(getResources().getDisplayMetrics().densityDpi);
            mBitmap = ((BitmapDrawable) mIcon).getBitmap();
            mInfo = MyPalette.sLauncherActivityInfo;
        } else {
            mInfo = null;
            mBitmap = BitmapFactory.decodeFile(file);
            mIcon = new BitmapDrawable(mBitmap);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info_activity);
        mResult = (TextView) findViewById(R.id.result);
        TextView appInfo = (TextView) findViewById(R.id.app_name);
        appInfo.setVisibility(View.GONE);

        //
        if(mInfo != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                PackageInfo pi = getPackageManager().getPackageInfo(mInfo.getApplicationInfo().packageName,
                        PackageManager.GET_SIGNATURES);
                for (int i = 0; i < pi.signatures.length; ++i) {
                    Signature s = pi.signatures[i];
                    md.update(s.toByteArray());
                    sign[i] = "\n" + Base64.encodeToString(md.digest(), Base64.DEFAULT);
                }
            } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
                sign[0] = e.toString();
            }
            Toast.makeText(this, "" + sign[0], Toast.LENGTH_LONG).show();
        } else {
            new SavePngPx(this).execute(file);
        }
        //
        setTitle(mInfo != null ? mInfo.getLabel() : file);
        mPaletteColorContainer = (LinearLayout) findViewById(R.id.palette_container);
        // appInfo.setText(mInfo.getLabel());
        // appInfo.setCompoundDrawables(mIcon, null, null, null);
        LinearLayout swatchContainer = (LinearLayout) findViewById(R.id.swatch_container);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        for (Palette.Swatch s : MyPalette.COLORES) {
            if (s == null) continue;
            ImageView iv = new ImageView(this);
            iv.setBackgroundColor(s.getRgb());
            swatchContainer.addView(iv, params);
        }
        MyPalette.sLauncherActivityInfo = null;
    }

    ;

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Palette.Swatch> list = new ArrayList<>(6);
        int finalColor = MyPalette.getColor(mBitmap, list);
//        write2SD(mBitmap,Integer.toHexString(finalColor)+"-GetApp.png");
        mResult.setBackgroundColor(finalColor);
        mResult.setText(mResult.getText() + " :  0x" + Integer.toHexString(finalColor));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, 50);
        int i=0;
        for (Palette.Swatch ss : list) {
            TextView tv = new TextView(this);
            tv.setBackgroundColor(ss.getRgb());
            tv.setTextColor(ss.getBodyTextColor());
            String signa = i < sign.length ? ("\n" + sign[i++]) : "";
            tv.setText("Color = 0x" + Integer.toHexString(ss.getRgb()) + ",  Population = "
                    + ss.getPopulation() + signa);
            tv.setOnClickListener(this);
            mPaletteColorContainer.addView(tv, params);
        }
    }

    boolean mSended;

    @Override
    public void onClick(View v) {
        if (mSended) {
            Toast.makeText(this, "已发送, 不要重复发了", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent("com.pptv.theme.changed");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, mBitmap);
        sendBroadcast(intent);
        Toast.makeText(this, "已发送theme改变, ", Toast.LENGTH_LONG).show();
        mSended = true;
    }

    public static void write2SD(Bitmap icon, String name) {
        int w = icon.getWidth(), h = icon.getHeight();
        int data[] = new int[w * h];
        icon.getPixels(data, 0, w, 0, 0, w, h);
        File file = new File("/storage/sdcard0/", name);
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            icon.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            android.util.Log.e("ww",""+e);
//        } finally {
//            IoUtils.closeSilently(out);
        }
    }
}
