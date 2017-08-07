package com.blackirwin.datamodel;

import com.blackirwin.annotations.ParseByTestDouble;
import com.blackirwin.annotations.ParseByTestInt;
import com.blackirwin.annotations.ParseByTestLong;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.lang.model.util.Types;

import static com.blackirwin.RelativeConst.PARSE_TEST_DOUBLE_MAX_VALUE;
import static com.blackirwin.RelativeConst.PARSE_TEST_INT_MAX_VALUE;
import static com.blackirwin.RelativeConst.PARSE_TEST_LONG_MAX_VALUE;

/**
 * Created by blackirwin on 2017/6/23.
 */

public class ParseByJsonPrimitiveKindFieldClass extends ParseByJsonFieldClass {

    public ParseByJsonPrimitiveKindFieldClass(Element variableElement, Types typeUtils) {
        super(variableElement, typeUtils);
    }

    @Override
    public boolean addParseByJsonCode(CodeBlock.Builder methodBuilder, String variableName, String jsonName) {
        if (isSkipParseBy()){
            return false;
        }
        String operation = "";
        switch (getVariableElement().asType().getKind()){
            case INT:
                operation = ".optInt";
                break;

            case BOOLEAN:
                operation = ".optBoolean";
                break;

            case DOUBLE:
                operation = ".optDouble";
                break;

            case LONG:
                operation = ".optLong";
                break;

            default:
                return false;
        }
        methodBuilder.addStatement(
                "\t$L = $L$L($S)", variableName, jsonName, operation, getParseByKey()
        );
        return true;
    }

    @Override
    public boolean addGenerateTestDataCode(CodeBlock.Builder methodBuilder, String variableName) {
        if (isSkipTestCreate()){
            return false;
        }
        String operation = "";
        String value = "";
        ClassName testDataCreatorClass = ClassName.get("com.blackirwin.utils","RandomDataUtils");
        switch (getVariableElement().asType().getKind()){
            case INT:
                operation = ".createInt";
                ParseByTestInt intParser = getVariableElement().getAnnotation(ParseByTestInt.class);
                value = String.valueOf(intParser == null ? PARSE_TEST_INT_MAX_VALUE : intParser.maxValue());
                break;

            case BOOLEAN:
                operation = ".createBoolean";
                break;

            case DOUBLE:
                operation = ".createDouble";
                ParseByTestDouble doubleParser = getVariableElement().getAnnotation(ParseByTestDouble.class);
                value = String.valueOf(doubleParser == null ? PARSE_TEST_DOUBLE_MAX_VALUE : doubleParser.maxValue());
                break;

            case LONG:
                operation = ".createLong";
                ParseByTestLong longParser = getVariableElement().getAnnotation(ParseByTestLong.class);
                value = String.valueOf(longParser == null ? PARSE_TEST_LONG_MAX_VALUE : longParser.maxValue());
                break;

            default:
                return false;
        }
        methodBuilder.addStatement(
                "\t$L = $T$L($L)", variableName, testDataCreatorClass, operation, value
        );
        return true;
    }

    @Override
    public boolean addParseToCode(CodeBlock.Builder methodBuilder, String jsonVariable, String variableName) {
        if (isSkipParseTo())
            return false;

        methodBuilder.addStatement(
                "$L.put($S,$L.$L)", jsonVariable, getParseToKey(), variableName, getVariableName()
        );
        addException(new ParseByJsonExceptionRecord("org.json","JSONException"));
        return true;
    }
}
