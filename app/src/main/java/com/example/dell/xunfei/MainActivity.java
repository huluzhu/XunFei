package com.example.dell.xunfei;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private StringBuilder mStringBuilder;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.edit_query);
    }

    public void discern(View view) {
        //1.创建RecognizerDialog对象,第二个参数就是一个初始化的监听器,我们用不上就设置为null
        RecognizerDialog mDialog = new RecognizerDialog(this, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//设置为中文模式
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//设置普通话模式
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //mDialog.setParameter("asr_sch", "1");
        //mDialog.setParameter("nlp_version", "2.0");
        //创建一个装每次解析数据的容器
        mStringBuilder = new StringBuilder();
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override//识别成功执行,参数recognizerResult 识别的结果,Json格式的字符串
            //第二参数 b:等于true时会话结束.方法才不会继续回调
            //一般情况下通过onResult接口多次返回结果,完整识别内容是多次结果累加的
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                //拿到讯飞识别的结果
                String resultString = recognizerResult.getResultString();
                System.out.println("讯飞识别的结果 " + resultString);
                System.out.println("b参数是什么 " + b);
                //自定义解析bean数据的方法,得到解析数据
                String content = parseData(resultString);
                System.out.println("解析后的数据 " + content);
                mStringBuilder.append(content);
                //对参数2b进行判断,如果为true,代表这个方法不会对调,那么我们容器的数据转为字符串,拿来使用即可
                if (b) {
                    String result = mStringBuilder.toString();
                    System.out.println(result);
                    //回答对象,在没有匹配到用户说的话,默认输出语句
                    String anwser = "不好意思,你说的我没有听清楚！！！";
                    if (result.contains("你好")) {
                        anwser = "你好,我是你的智能语音助手,很高兴为你服务";
                    } else if (result.contains("小明")) {
                        anwser = "和小明一块玩";
                    } else if (result.contains("美女")) {
                        //定义一个String数组,智能语音根据美女这个数据,可能说的话
                        String[] answerList = new String[]{"500元,妹子陪你打一晚上游戏", "你是坏人不和你玩了", "小助手很纯洁,不要带坏我了"};
                        //小助手随机回答,所以使用生成随机数的类.random(因为此数是小数,乘以集合长度,再做int类型强转,得到的数是0到集合长度-1)
                        int random = (int) (Math.random() * answerList.length);
                        anwser = answerList[random];
                    }
                    show(anwser);
                }
            }

            @Override//识别失败执行的方法,speechError错误码
            public void onError(SpeechError speechError) {
                System.out.println("错误码 " + speechError);
            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    public void compound(View view) {
        String str1 = editText.getText().toString();
        show(str1);
    }

    public void show(String result) {
        //1.创建 SpeechSynthesizer 对象, 第二个参数：本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.合成参数设置，详见《MSC Reference Manual》SpeechSynthesizer 类
        // 设置发音人（更多在线发音人，用户可参见 附录13.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端,这些功能用到了讯飞服务器,所以要有网络
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        //仅支持保存为 pcm 和 wav 格式，如果不需要保存合成音频，注释该行代码
        // mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        // 3.开始合成,第一个参数就是转换成声音的文字,自定义,第二个参数就是合成监听器对象,我们不需要对声音有什么特殊处理,就传null
        mTts.startSpeaking(result, null);
    }

    private String parseData(String resultString) {
        //创建gson对象.记得要关联一下gson.jar包,方可以使用
        Gson gson = new Gson();
        //参数1 String类型的json数据   参数2.存放json数据对应的bean类
        XFBean xfBean = gson.fromJson(resultString, XFBean.class);
        //创建集合,用来存放bean类里的对象
        ArrayList<XFBean.WS> ws = xfBean.ws;
        //创建一个容器,用来存放从每个集合里拿到的数据,使用StringBUndle效率高
        StringBuilder stringBuilder = new StringBuilder();
        for (XFBean.WS w : ws) {
            String text = w.cw.get(0).w;
            stringBuilder.append(text);
        }
        //把容器内的数据转换为字符串返回出去
        return stringBuilder.toString();
    }
}
