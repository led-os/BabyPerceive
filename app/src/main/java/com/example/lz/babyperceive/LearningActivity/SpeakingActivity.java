package com.example.lz.babyperceive.LearningActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lz.babyperceive.Activity.BaseActivity;
import com.example.lz.babyperceive.Activity.YuleActivity;
import com.example.lz.babyperceive.Application.MyApplication;
import com.example.lz.babyperceive.Dialog.NoticeDialog;
import com.example.lz.babyperceive.R;
import com.example.lz.babyperceive.Utils.SharedPreferencesHelper;
import com.example.lz.babyperceive.Utils.Speek;
import com.example.lz.babyperceive.Utils.Utils;
import com.example.lz.babyperceive.View.TitleView;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SpeakingActivity extends BaseActivity implements View.OnClickListener {
    private List<String> objectList = new ArrayList<>();
    private Random random;
    private TextView chinese_tv, chineseSpell_tv;
    private int number = 1;
    private String chinses;  //单个汉字
    private String previous_number = "";//上一个汉字
    private Speek speek;  //文字转语音类
    private Button previous_bt, next_bt, play_bt;
    private String[] spell = new String[0];
    private String chineseSpell = "";
    private TitleView titleView;
    private int length;
    private boolean isShowNotice = false;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private Utils utils;
    private MyApplication myApplication;

    @Override
    public void widgetClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApplication = (MyApplication) getApplication();
        if (!myApplication.status) {
            myApplication.sendEmptyMessage();
        }
        utils = new Utils(this);
        String[] arr = utils.getAsstesTxt("chinese.txt").split(",");
        objectList = java.util.Arrays.asList(arr);
        length = objectList.size();
        sharedPreferencesHelper = new SharedPreferencesHelper(this, "chinese.txt");
        number = (int) sharedPreferencesHelper.getSharedPreference("number", 1);
        Log.i("test", "number:" + number);
        initView();
        initData(number);
        speek = new Speek(this);
        //   List

    }

    private String getChineseSpell(String chinese) {
        String[] pyStrs = PinyinHelper.toHanyuPinyinStringArray('重');

        for (String s : pyStrs) {
            System.out.println(s);
        }
//-------------------指定格式转换----------------------------
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

// UPPERCASE：大写  (ZHONG)
// LOWERCASE：小写  (zhong)
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);//输出大写

// WITHOUT_TONE：无音标  (zhong)
// WITH_TONE_NUMBER：1-4数字表示音标  (zhong4)
// WITH_TONE_MARK：直接用音标符（必须WITH_U_UNICODE否则异常）  (zhòng)
        format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);

// WITH_V：用v表示ü  (nv)
// WITH_U_AND_COLON：用"u:"表示ü  (nu:)
// WITH_U_UNICODE：直接用ü (nü)
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        try {
            char c = chinese.charAt(0);
            spell = PinyinHelper.toHanyuPinyinStringArray(c, format);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        String s = Arrays.toString(spell); //
        return s;
    }


    /**
     * \
     * 指定位置的汉字
     */
    private void initData(int position) {
        number = position;
        chinses = utils.getChinese(number);      //指定位置的汉字
        chineseSpell = utils.getChineseSpell(chinses);                          //获取拼音
        chinese_tv.setText(chinses);
        chineseSpell_tv.setText(chineseSpell);
    }

    /**
     * 汉字赋值并显示
     */
    private void initData(String name) {
        chinses = name;
        chinese_tv.setText(chinses);
        chineseSpell_tv.setText(chineseSpell);
    }

    private void initView() {
        chinese_tv = (TextView) findViewById(R.id.chinese_tv);
        chinese_tv.setOnClickListener(this);
        chineseSpell_tv = (TextView) findViewById(R.id.chineseSpell_tv);
        previous_bt = (Button) findViewById(R.id.previous_bt);
        previous_bt.setOnClickListener(this);
        next_bt = (Button) findViewById(R.id.next_bt);
        next_bt.setOnClickListener(this);
        play_bt = (Button) findViewById(R.id.play_bt);
        play_bt.setOnClickListener(this);
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitleView(1);   //1代表标题栏 显示 X
        titleView.setCustomOnClickListener(new TitleView.ClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.back_bt:
                        finish();
                        break;
                }
            }
        });
    }

    private void getStatus() {
        if (myApplication.isStatus()) {
            isShowNotice = true;
            new NoticeDialog(this, R.style.dialog, "111", new NoticeDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {

                }
            }).setTitle("学习完成啦").show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chinese_tv:
                speek.Speeking(chinses);
                break;
            case R.id.next_bt:
                if (myApplication.isStatus() &&!isShowNotice) {
                    getStatus();
                } else if (number < length - 2) {
                    previous_number = chinses;
                    initData(number + 1);
                    speek.Speeking(chinses);
                }
                break;
            case R.id.previous_bt:
                if (myApplication.isStatus() &&!isShowNotice) {
                    getStatus();
                } else if (number > 1) {
                    initData(number - 1);
                    speek.Speeking(chinses);
                }
                break;
            case R.id.play_bt:
                speek.Speeking(chinses);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    //改变状态栏字体颜色
    private void changeStatusBarTextColor(boolean isBlack) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (isBlack) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏黑色字体
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//恢复状态栏白色字体
            }
        }
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_speaking;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void setListener() {

    }

    @Override
    protected void onStop() {
        if (number > (int) sharedPreferencesHelper.getSharedPreference("chinese", 1)) {
            sharedPreferencesHelper.put("number", number);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (number > (int) sharedPreferencesHelper.getSharedPreference("chinese", 1)) {
            sharedPreferencesHelper.put("number", number);
        }
        myApplication.removeEmptyMessage();
        speek.Destory();
        super.onDestroy();
    }
}
