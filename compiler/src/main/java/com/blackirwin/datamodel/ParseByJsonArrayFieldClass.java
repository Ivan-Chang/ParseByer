package com.blackirwin.datamodel;

import com.blackirwin.annotations.ParseByTestArray;
import com.blackirwin.annotations.ParseByTestDouble;
import com.blackirwin.annotations.ParseByTestInt;
import com.blackirwin.annotations.ParseByTestLong;
import com.blackirwin.annotations.ParseByTestString;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

import static com.blackirwin.RelativeConst.PARSE_CLASS_TEST_DATA_METHOD_NAME;
import static com.blackirwin.RelativeConst.PARSE_TEST_ARRAY_MAX_LENGTH;
import static com.blackirwin.RelativeConst.PARSE_TEST_DOUBLE_MAX_VALUE;
import static com.blackirwin.RelativeConst.PARSE_TEST_INT_MAX_VALUE;
import static com.blackirwin.RelativeConst.PARSE_TEST_LONG_MAX_VALUE;
import static com.blackirwin.RelativeConst.PARSE_TEST_STRING_MAX_LENGTH;

/**
 * Created by blackirwin on 2017/6/23.
 */

public class ParseByJsonArrayFieldClass extends ParseByJsonFieldClass {

    private Types mTypeUtils;
    public ParseByJsonArrayFieldClass(Element variableElement, Types typeUtils) {
        super(variableElement, typeUtils);
        mTypeUtils = typeUtils;
    }

    @Override
    public boolean addParseByJsonCode(CodeBlock.Builder methodBuilder, String variableName, String jsonName) {
        if (isSkipParseBy()) {
            return false;
        }

        ArrayType arrayType = (ArrayType) getVariableElement().asType();
        Element dataElement = mTypeUtils.asElement(arrayType.getComponentType());

        ClassName listClass = ClassName.get("java.util","LinkedList");
        ClassName jsonArrayClass = ClassName.get(
                "org.json", "JSONArray"
        );
        ClassName jsonObjectClass = ClassName.get(
                "org.json", "JSONObject"
        );
        String tmpListName = String.format("%sTmpList", getParseByKey());
        String jsonArrayName = String.format("%sJsonArray", getParseByKey());
        TypeKind kind = arrayType.getComponentType().getKind();
        if (kind.isPrimitive()){
            String operation = "";
            String classString = "";
            switch (kind){
                case INT:
                    operation = "$L[$L] = $L.optInt($L, 0)";
                    classString = "int";
                    break;

                case BOOLEAN:
                    operation = "$L[$L] = $L.optBoolean($L, false)";
                    classString = "boolean";
                    break;

                case DOUBLE:
                    operation = "$L[$L] = $L.optDouble($L, 0.0f)";
                    classString = "double";
                    break;

                case LONG:
                    operation = "$L[$L] = $L.optLong($L, 0)";
                    classString = "long";
                    break;
                default:
                    return false;
            }
            methodBuilder.addStatement("$T $L = $L.optJSONArray($S)", jsonArrayClass, jsonArrayName, jsonName, getParseByKey())
                    .add("if ($L != null){\n", jsonArrayName)
                    .addStatement("$L = new $L[$L.length()]",
                            variableName, classString, jsonArrayName)
                    .beginControlFlow("for (int i = 0; i < $L.length(); i++)", jsonArrayName)
                    .addStatement(operation, variableName, "i", jsonArrayName, "i")
                    .endControlFlow()
                    .add("}\n");
            return true;
        }

        ParseByJsonRecursiveFieldClass recursiveFieldClass = new ParseByJsonRecursiveFieldClass(
                dataElement, mTypeUtils
        );
        if (recursiveFieldClass.isRecursive()){
            ClassName parseByJsonCreatorClass = ClassName.get("com.blackirwin","ParseByer");
            String dataClassName = dataElement.getSimpleName().toString();
            ClassName recursiveClass = ClassName.get(
                    dataElement.getEnclosingElement().toString(), dataClassName
            );
            methodBuilder.addStatement("$T<$L> $L = new $T<>()",
                    listClass, dataClassName, tmpListName, listClass)
                    .addStatement("$T $L = $L.optJSONArray($S)", jsonArrayClass, jsonArrayName, jsonName, getParseByKey())
                    .add("if ($L != null){\n", jsonArrayName)
                    .beginControlFlow("for (int i = 0; i < $L.length(); i++)", jsonArrayName)
                    .addStatement("$T itemJson = $L.optJSONObject(i)", jsonObjectClass, jsonArrayName)
                    .addStatement("$L item= $T.parseBy($T.class, itemJson.toString())",
                            dataClassName, parseByJsonCreatorClass, recursiveClass)
                    .addStatement("$L.add(item)",tmpListName)
                    .endControlFlow()
                    .add("}\n")
                    .addStatement("$L = ($L[])$L.toArray()", variableName, dataClassName, tmpListName);

            addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","InitCreatorClassException"));
            addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","MethodInvokeException"));
            return true;
        }
        else if(dataElement.getSimpleName().toString().equals(String.class.getSimpleName())){
            String className = "String";
            methodBuilder.addStatement("$T<$L> $L = new $T<>()",
                    listClass, className, tmpListName, listClass)
                    .addStatement("$T $L = $L.optJSONArray($S)", jsonArrayClass, jsonArrayName, jsonName, getParseByKey())
                    .add("if ($L != null){\n", jsonArrayName)
                    .beginControlFlow("for (int i = 0; i < $L.length(); i++)", jsonArrayName)
                    .addStatement("$L item = $L.optString(i, \"\")", className, jsonArrayName)
                    .addStatement("$L.add(item)",tmpListName)
                    .endControlFlow()
                    .add("}\n")
                    .addStatement("$L = ($L[])$L.toArray()", variableName, className, tmpListName);
            return true;
        }
        return false;
    }

    @Override
    public boolean addGenerateTestDataCode(CodeBlock.Builder methodBuilder, String variableName) {
        if (isSkipTestCreate()){
            return false;
        }
        ArrayType arrayType = (ArrayType) getVariableElement().asType();
        ClassName listClass = ClassName.get("java.util","LinkedList");
        ParseByTestArray arrayParser = getVariableElement().getAnnotation(ParseByTestArray.class);
        String arrayLength = String.valueOf(arrayParser == null ? PARSE_TEST_ARRAY_MAX_LENGTH
                : arrayParser.maxLength());
        String tmpListName = String.format("%sTmpList", getParseByKey());
        TypeKind kind = arrayType.getComponentType().getKind();
        ClassName testDataCreatorClass = ClassName.get("com.blackirwin.utils","RandomDataUtils");
        if (kind.isPrimitive()){
            String operation = "";
            String classString = "";
            String value = "";
            switch (kind){
                case INT:
                    operation = ".createInt";
                    classString = "int";
                    ParseByTestInt intParser = getVariableElement().getAnnotation(ParseByTestInt.class);
                    value = String.valueOf(intParser == null ? PARSE_TEST_INT_MAX_VALUE : intParser.maxValue());
                    break;

                case BOOLEAN:
                    operation = ".createBoolean";
                    classString = "boolean";
                    break;

                case DOUBLE:
                    operation = ".createDouble";
                    ParseByTestDouble doubleParser = getVariableElement().getAnnotation(ParseByTestDouble.class);
                    value = String.valueOf(doubleParser == null ? PARSE_TEST_DOUBLE_MAX_VALUE : doubleParser.maxValue());
                    classString = "double";
                    break;

                case LONG:
                    operation = ".createLong";
                    ParseByTestLong longParser = getVariableElement().getAnnotation(ParseByTestLong.class);
                    value = String.valueOf(longParser == null ? PARSE_TEST_LONG_MAX_VALUE : longParser.maxValue());
                    classString = "long";
                    break;

                default:
                    return false;
            }
            methodBuilder.addStatement("$L = new $L[$L]",
                    variableName, classString, arrayLength)
                    .beginControlFlow("for (int i = 0; i < $L.length; i++)", variableName)
                    .addStatement("$L[$L] = $T$L($L)", variableName, "i", testDataCreatorClass, operation, value)
                    .endControlFlow();
            return true;
        }

        Element dataElement = mTypeUtils.asElement(arrayType.getComponentType());
        ParseByJsonRecursiveFieldClass recursiveFieldClass = new ParseByJsonRecursiveFieldClass(
                dataElement, mTypeUtils
        );
        if (recursiveFieldClass.isRecursive()){
            ClassName parseByJsonCreatorClass = ClassName.get("com.blackirwin","ParseByer");
            String dataClassName = dataElement.getSimpleName().toString();
            ClassName recursiveClass = ClassName.get(
                    dataElement.getEnclosingElement().toString(), dataClassName
            );
            methodBuilder.addStatement("$L = new $L[$T.createInt($L)]",
                    variableName, recursiveClass, parseByJsonCreatorClass, arrayLength)
                    .beginControlFlow("for (int i = 0; i < $L; i++)", arrayLength)
                    .addStatement("$L[i] = $T.%L(%T.class)", variableName, parseByJsonCreatorClass,
                            PARSE_CLASS_TEST_DATA_METHOD_NAME, recursiveClass)
                    .endControlFlow();

            addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","InitCreatorClassException"));
            addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions","MethodInvokeException"));
            return true;
        }
        else if(dataElement.getSimpleName().toString().equals(String.class.getSimpleName())){
            String className = "String";
            ParseByTestString testStringAnnotation = getVariableElement().getAnnotation(ParseByTestString.class);
            methodBuilder.addStatement("$L = new $L[$L]",
                    variableName, className, arrayLength)
                    .beginControlFlow("for (int i = 0; i < $L; i++)", arrayLength)
                    .addStatement("$L[i] =  $T.$L($L)", variableName, testDataCreatorClass, "createString",
                            String.valueOf(testStringAnnotation == null ?
                                    PARSE_TEST_STRING_MAX_LENGTH : testStringAnnotation.maxLength()))
                    .endControlFlow();
            return true;
        }
        return false;
    }

    @Override
    public boolean addParseToCode(CodeBlock.Builder methodBuilder, String jsonVariable, String variableName) {
        if (isSkipParseTo())
            return false;

        ClassName jsonArrayClass = ClassName.get("org.json", "JSONArray");
        String jsonArrayName = getParseToKey()+"JsonArray";
        methodBuilder
                .addStatement(
                        "$T $L = new $T()", jsonArrayClass, jsonArrayName, jsonArrayClass
                )
                .beginControlFlow("for (int i = 0; i < $L.$L.length; i++)", variableName, getVariableName())
                .addStatement(
                        "$L.put($L.$L[i])", jsonArrayName, variableName, getVariableName()
                )
                .endControlFlow()
                .addStatement(
                        "$L.putOpt($S,$L.toString())", jsonVariable, getParseToKey(), jsonArrayName
                );
        addException(new ParseByJsonExceptionRecord("org.json","JSONException"));
        return true;
    }
}
