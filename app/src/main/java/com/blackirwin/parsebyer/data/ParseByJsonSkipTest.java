package com.blackirwin.parsebyer.data;

import com.blackirwin.annotations.JsonSkip;
import com.blackirwin.annotations.ParseByJson;

/**
 * Created by blackirwin on 2017/7/20.
 */
@ParseByJson
public class ParseByJsonSkipTest {
        int should_have;
        @JsonSkip
        long shouldnot_have;
}