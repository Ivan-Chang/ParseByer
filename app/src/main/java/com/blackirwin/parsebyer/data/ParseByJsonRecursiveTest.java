package com.blackirwin.parsebyer.data;

import com.blackirwin.annotations.ParseByJson;

/**
 * Created by blackirwin on 2017/7/20.
 */
@ParseByJson
public class ParseByJsonRecursiveTest {
    int int_value;
    String string_value;
    ParseByJsonPrimitiveTest parseByTestInt_value;
    ParseByJsonStringListTest parseByTestArray_value;
}
