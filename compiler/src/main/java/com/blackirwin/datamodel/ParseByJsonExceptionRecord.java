package com.blackirwin.datamodel;

/**
 * Created by blackirwin on 2017/6/23.
 */

public class ParseByJsonExceptionRecord {
    public String packageName;
    public String simpleName;

    public ParseByJsonExceptionRecord(String packageName, String simpleName){
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ParseByJsonExceptionRecord)){
            return false;
        }
        ParseByJsonExceptionRecord record = (ParseByJsonExceptionRecord) obj;
        return this.packageName.equals(record.packageName)
                && this.simpleName.equals(record.simpleName);
    }

    @Override
    public int hashCode() {
        return this.packageName.hashCode() + this.simpleName.hashCode();
    }
}
