package com.example.getappinfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SavePngPx extends AsyncTask<String, Void, String> {
    final Context mContext;

    SavePngPx(Context act) {
        mContext = act.getApplicationContext();
    }

    @Override
    protected String doInBackground(String... params) {
        String file = params[0] + ".txt";
        File out = new File(file);
//        if (out.exists()) out.delete();
        Bitmap bitmap = BitmapFactory.decodeFile(params[0]);
        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();
        final int[] pixels = new int[bitmapWidth * bitmapHeight];
        bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
        FileWriter writer = null;
        try {
            writer = new FileWriter(out);
            for (int line = 0; line < bitmapHeight; ++line) {
                for (int col = 0; col < bitmapWidth; ++col) {
                    writer.write(" 0x" + Integer.toHexString(pixels[line * bitmapWidth + col]));
                }
                writer.write("\n\r");
            }
            writer.flush();
            writer.close();
            return "ok. w="+bitmapWidth+",h="+bitmapHeight;

        } catch (IOException e) {
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return "fail";
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(mContext, s , Toast.LENGTH_SHORT).show();
    }
}