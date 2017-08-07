package com.blackirwin.datamodel;

import com.blackirwin.annotations.ParseByTestString;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.lang.model.util.Types;

import static com.blackirwin.RelativeConst.PARSE_TEST_STRING_MAX_LENGTH;

/**
 * Created by blackirwin on 2017/6/23.
 */

public class ParseByJsonStringFieldClass extends ParseByJsonFieldClass {

    public ParseByJsonStringFieldClass(Element variableElement, Types typeUtils) {
        super(variableElement, typeUtils);
    }

    @Override
    public boolean addParseByJsonCode(CodeBlock.Builder methodBuilder, String variableName, String jsonName) {
        if (isSkipParseBy()){
            return false;
        }
        methodBuilder.addStatement(
                "\t$L = $L.optString($S)",
                variableName, jsonName, getParseByKey()
        );
        return true;
    }

    @Override
    public boolean addGenerateTestDataCode(CodeBlock.Builder methodBuilder, String variableName) {
        if (isSkipTestCreate()){
            return false;
        }

        ParseByTestString testStringAnnotation = getVariableKindOrBoxElement().getAnnotation(ParseByTestString.class);
        ClassName testDataCreatorClass = ClassName.get("com.blackirwin.utils","RandomDataUtils");
        methodBuilder.addStatement(
                "\t$L = $T.$L($L)", variableName,
                testDataCreatorClass, "createString",
                String.valueOf(testStringAnnotation == null ?
                        PARSE_TEST_STRING_MAX_LENGTH : testStringAnnotation.maxLength())
        );

        return true;
    }

    @Override
    public boolean addParseToCode(CodeBlock.Builder methodBuilder, String jsonVariable, String variableName) {
        if (isSkipParseTo())
            return false;

        methodBuilder.addStatement(
                "$L.putOpt($S,$L.$L)", jsonVariable, getParseToKey(), variableName, getVariableName()
        );
        addException(new ParseByJsonExceptionRecord("org.json","JSONException"));

        return true;
    }
}
