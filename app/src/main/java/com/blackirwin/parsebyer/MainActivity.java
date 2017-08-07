package com.blackirwin.parsebyer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.blackirwin.ParseByer;
import com.blackirwin.exceptions.InitCreatorClassException;
import com.blackirwin.exceptions.MethodInvokeException;
import com.blackirwin.parsebyer.data.ParseByJsonDoubleArrayTest;
import com.blackirwin.parsebyer.data.ParseByJsonIntArrayTest;
import com.blackirwin.parsebyer.data.ParseByJsonLongArrayTest;
import com.blackirwin.parsebyer.data.ParseByJsonPrimitiveTest;
import com.blackirwin.parsebyer.data.ParseByJsonRecursiveTest;
import com.blackirwin.parsebyer.data.ParseByJsonSkipTest;
import com.blackirwin.parsebyer.data.ParseByJsonStringArrayTest;
import com.blackirwin.parsebyer.data.ParseByJsonStringListTest;

public class MainActivity extends Activity {

    private static final String TestTag = "TestTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Log.d(TestTag, "test begins");
            ParseByJsonDoubleArrayTest dat = ParseByer.parseBy(ParseByJsonDoubleArrayTest.class);
            String datString = ParseByer.parseTo(dat);
            Log.d(TestTag, "ParseByJsonDoubleArrayTest : " + datString);

            ParseByJsonIntArrayTest iat = ParseByer.parseBy(ParseByJsonIntArrayTest.class);
            String iatString = ParseByer.parseTo(iat);
            Log.d(TestTag, "ParseByJsonIntArrayTest : " + iatString);

            ParseByJsonLongArrayTest lat = ParseByer.parseBy(ParseByJsonLongArrayTest.class);
            String latString = ParseByer.parseTo(lat);
            Log.d(TestTag, "ParseByJsonLongArrayTest : " + latString);

            ParseByJsonPrimitiveTest pt = ParseByer.parseBy(ParseByJsonPrimitiveTest.class);
            String ptString = ParseByer.parseTo(pt);
            Log.d(TestTag, "ParseByJsonPrimitiveTest : " + ptString);

            ParseByJsonRecursiveTest rt = ParseByer.parseBy(ParseByJsonRecursiveTest.class);
            String rtString = ParseByer.parseTo(rt);
            Log.d(TestTag, "ParseByJsonRecursiveTest : " + rtString);

            ParseByJsonSkipTest st = ParseByer.parseBy(ParseByJsonSkipTest.class);
            String stString = ParseByer.parseTo(st);
            Log.d(TestTag, "ParseByJsonSkipTest : " + stString);

            ParseByJsonStringArrayTest sat = ParseByer.parseBy(ParseByJsonStringArrayTest.class);
            String satString = ParseByer.parseTo(sat);
            Log.d(TestTag, "ParseByJsonStringArrayTest : " + satString);

            ParseByJsonStringListTest slt = ParseByer.parseBy(ParseByJsonStringListTest.class);
            String sltString = ParseByer.parseTo(slt);
            Log.d(TestTag, "ParseByJsonStringListTest : " + sltString);
            Log.d(TestTag, "test ends");
        } catch (InitCreatorClassException | MethodInvokeException e) {
            e.printStackTrace();
            Log.d(TestTag, "test runs wrong");
        }
    }
}
