package com.blackirwin.datamodel;

import com.blackirwin.annotations.ParseByJson;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.lang.model.util.Types;

/**
 * Created by blackirwin on 2017/6/23.
 */

public class ParseByJsonRecursiveFieldClass extends ParseByJsonFieldClass {

    private boolean isRecursive;
    public ParseByJsonRecursiveFieldClass(Element variableElement, Types typeUtils) {
        super(variableElement, typeUtils);
        Element element = typeUtils.asElement(variableElement.asType());
        ParseByJson variableKindAnnotation = element.getAnnotation(ParseByJson.class);
        isRecursive = (variableKindAnnotation != null);
    }

    public boolean isRecursive(){
        return isRecursive;
    }

    @Override
    public boolean addParseByJsonCode(CodeBlock.Builder methodBuilder, String variableName, String jsonName) {
        if (isSkipParseBy() || !isRecursive) {
            return false;
        }
        ClassName parseByJsonCreatorClass = ClassName.get("com.blackirwin","ParseByer");
        ClassName recursiveClass = ClassName.get(
                getVariableKindOrBoxElement().getEnclosingElement().toString(),
                getVariableKindOrBoxName()
        );
        methodBuilder.addStatement(
                "\t$L = $T.parseBy($T.class, $L.optString($S))",
                variableName, parseByJsonCreatorClass, recursiveClass, jsonName, getParseByKey()
        );

        addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","InitCreatorClassException"));
        addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","MethodInvokeException"));
        return true;
    }

    @Override
    public boolean addGenerateTestDataCode(CodeBlock.Builder methodBuilder, String variableName) {
        if (isSkipTestCreate() || !isRecursive) {
            return false;
        }
        ClassName parseByJsonCreatorClass = ClassName.get("com.blackirwin","ParseByer");
        ClassName recursiveClass = ClassName.get(
                getVariableKindOrBoxElement().getEnclosingElement().toString(),
                getVariableKindOrBoxName()
        );
        methodBuilder.addStatement(
                "\t$L = $T.parseBy($T.class)",
                variableName, parseByJsonCreatorClass, recursiveClass
        );

        addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","InitCreatorClassException"));
        addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","MethodInvokeException"));

        return true;
    }

    @Override
    public boolean addParseToCode(CodeBlock.Builder methodBuilder, String jsonVariable, String variableName) {
        if (isSkipParseTo() || !isRecursive)
            return false;

        methodBuilder.addStatement(
                "$L.putOpt($S,ParseByer.parseTo($L.$L))", jsonVariable, getParseToKey(), variableName, getVariableName()
        );
        addException(new ParseByJsonExceptionRecord("org.json","JSONException"));
        addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","InitCreatorClassException"));
        addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","MethodInvokeException"));

        return true;
    }
}
