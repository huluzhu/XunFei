package com.example.dell.xunfei;

import java.util.ArrayList;

/**
 * Created by ${hujiqiang} on 2017/09/06.
 */

public class XFBean {
    public ArrayList<WS> ws;

    public class WS {
        public ArrayList<CW> cw;
    }

    public class CW {
        public String w;
    }
}
