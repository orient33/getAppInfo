package com.example.getappinfo.other;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.choices.divider.DividerItemDecoration;
import com.example.getappinfo.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OtherActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    MyAdapter mAdapter;
    String[] mTitle;
    private static final int ID_BASE = R.id.addMonth;
    private static final int SPAN = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        mTitle = getResources().getStringArray(R.array.title_12w);
        mRecyclerView = (RecyclerView) findViewById(R.id.view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN));
        DividerItemDecoration itemDecoration = new DividerItemDecoration();
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter = new MyAdapter(this, mTitle));
        addMy();
    }

    void addMy() {
        float v[] = new float[]{16000f, 3000f, 1370f, 11627f};
        mAdapter.addMonth("1月", v);
        mAdapter.addMonth("2月", v);
        mAdapter.addMonth("3月", v);
        v = new float[]{10114.94f, 3000f, 256.49f, 6855.45f};
        mAdapter.addMonth("4月", v);

        v = new float[]{19000 * 0.81f + 510, 3083f, 1321.88f, 11485.62f};
        mAdapter.addMonth("5月", v);

        //加班费 奖金 交通、餐费补贴 都计税
        v = new float[]{19000 + 873.56f + 630, 3083f, 2475.14f, 14945.42f};
        mAdapter.addMonth("6月", v);

        v = new float[]{19000 + 1000 + 873.56f + 690, 4183f, 2465.14f, 14915.42f};
        mAdapter.addMonth("7月", v);

        v = new float[]{19000 + 1000 + 630, 4183f, 2231.75f, 14215.25f};
        mAdapter.addMonth("8月", v);

        //中秋福利未计税
        v = new float[]{19000 + 873.56f + 630 + 66.67f, 4183f, 2216.81f, 14170.42f + 50};
        mAdapter.addMonth("9月", v);

        v = new float[]{19000 + 540, 4183f, 1959.25f, 13397.75f};
        mAdapter.addMonth("10月", v);

        v = new float[]{19000 + 873.56f + 630, 4183f, 2200.14f, 14120.42f};
        mAdapter.addMonth("11月", v);

        v = new float[]{19000 + 690, 4183f, 1996.75f, 13510.25f};
        mAdapter.addMonth("12月", v);

        v = new float[]{44815.68f, 0f, 4376.57f, 40439.11f};
        mAdapter.addMonth("年终奖", v);
        mAdapter.cacluteAll();
    }

    boolean showCheckDialog(View v, Data title, float value[]) {
        float leak = value[0] - value[1] - value[2] - value[3];
        String leakStr = mFormat.format(leak);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < SPAN; ++i) {
            sb.append(mTitle[i] + " : " + value[i - 1] + "\n");
        }
        float geShui = geShui(value[0], value[1]);
        boolean geShuiOk = title.ok;//isGeShuiOk(value);
        sb.append("应交" + mTitle[SPAN - 2] + "  = " + geShui + ",,right?" + geShuiOk);
        builder.setTitle(title.description + ", 总数验证 " + leakStr)
                .setMessage(sb)
                .setPositiveButton(android.R.string.ok, null);
        builder.create().show();
        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            tv.append(" : " + leakStr);
        }
        return geShuiOk;
    }

    static boolean isGeShuiOk(float value[]) {
        float right = geShui(value[0], value[1]);
        return (0.1f > Math.abs(right - value[2]));
    }

    static float geShui(float all, float sheBao) {
        float p = all - sheBao - 3500f;
        if (p < 0) return 0f;
        else if (p < 1500) return p * 0.03f;
        else if (p < 4500) return p * 0.1f - 105;
        else if (p < 9000) return p * 0.2f - 555;
        else if (p < 35000) return p * 0.25f - 1005;
        else if (p < 55000) return p * 0.3f - 2755;
        else if (p < 80000) return p * 0.35f - 5505;
        else return p * 0.45f - 13505;
    }

    final View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float[] value = new float[SPAN - 1];
            for (int key = 1; key < SPAN; ++key)
                value[key - 1] = ((Data) v.getTag(ID_BASE + key)).value;
            boolean right = showCheckDialog(v, (Data) v.getTag(ID_BASE), value);
        }
    };

    static class VH extends RecyclerView.ViewHolder {
        final TextView tv;
        final Drawable bg;

        VH(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.item_rv);
            bg = tv.getBackground();
        }
    }

    static class Data {
        boolean ok;
        String description;
        float value;
    }

    final DecimalFormat mFormat = new DecimalFormat();

    {
        mFormat.applyPattern("0.00");
    }

    class MyAdapter extends RecyclerView.Adapter<VH> {

        final int TYPE_DES = 0;
        final int TYPE_VALUE = 1;
        /**
         * 数据
         * 月份  工资总额 三险一金 个税 净收入
         */
        final Context mmContext;
        final ArrayList<Data> mmData = new ArrayList<>(70);

        MyAdapter(Context context, String[] title) {
            mmContext = context;
            if (title.length != SPAN) throw new RuntimeException("标题和title数量不匹配");
            for (String t : title) {
                Data item = new Data();
                item.description = t;
                mmData.add(item);
            }
        }

        void addMonth(String month, float[] v) {
            for (int i = 0; i < SPAN; ++i) {
                Data item = new Data();
                if (i == 0) {
                    item.description = month;
                    item.ok = isGeShuiOk(v);
                } else {
                    item.value = (i - 1 < v.length) ? v[i - 1] : 0f;
                }
                mmData.add(item);
            }
            notifyDataSetChanged();
        }

        //合计
        void cacluteAll() {
            int itemCount = getItemCount();
            int lineCount = itemCount / SPAN;
            float v[] = new float[SPAN - 1];
            for (int i = 0; i < SPAN - 1; ++i) {
                for (int j = 0; j < lineCount; ++j) {
                    v[i] += mmData.get(j * SPAN + i + 1).value;
                }
            }
            addMonth("合计 : ", v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            int type = getItemViewType(position);
            final Data data = mmData.get(position);
            String content = type == TYPE_DES ? data.description :
                    mFormat.format(data.value);
            holder.tv.setText(content);
            boolean clickable = position % SPAN == 0;
            holder.tv.setOnClickListener(clickable ? itemClick : null);
            if (clickable) {
                int month = position / SPAN;
                if (month > 0 && month < 13)
                    holder.tv.setBackgroundColor(data.ok ? Color.GREEN : Color.RED);
                else holder.tv.setBackground(holder.bg);
                for (int key = 0; key < SPAN; ++key)
                    holder.tv.setTag(ID_BASE + key, mmData.get(month * SPAN + key));
            } else {
                holder.tv.setBackground(holder.bg);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View tv = View.inflate(mmContext, R.layout.recycler_item, null);
            return new VH(tv);
        }

        @Override
        public int getItemCount() {
            return mmData.size();
        }

        /**
         * 第一行、第一列 为string类型  0
         * 其他为float                1
         */
        @Override
        public int getItemViewType(int position) {
            return (position < SPAN || position % SPAN == 0) ? TYPE_DES : TYPE_VALUE;
        }
    }
}
