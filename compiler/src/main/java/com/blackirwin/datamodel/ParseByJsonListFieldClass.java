package com.blackirwin.datamodel;

import com.blackirwin.annotations.ParseByTestArray;
import com.blackirwin.annotations.ParseByTestString;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static com.blackirwin.RelativeConst.PARSE_CLASS_TEST_DATA_METHOD_NAME;
import static com.blackirwin.RelativeConst.PARSE_TEST_ARRAY_MAX_LENGTH;
import static com.blackirwin.RelativeConst.PARSE_TEST_STRING_MAX_LENGTH;

/**
 * Created by blackirwin on 2017/6/26.
 */

public class ParseByJsonListFieldClass extends ParseByJsonFieldClass {

    private Types mTypeUtils;
    public ParseByJsonListFieldClass(Element variableElement, Types typeUtils) {
        super(variableElement, typeUtils);
        mTypeUtils = typeUtils;
    }

    @Override
    public boolean addParseByJsonCode(CodeBlock.Builder methodBuilder, String variableName, String jsonName) {
        if (isSkipParseBy()) {
            return false;
        }

        DeclaredType typeMirror = (DeclaredType) getVariableElement().asType();
        List<? extends TypeMirror> typeArguments = typeMirror.getTypeArguments();
        if (typeArguments != null && typeArguments.size() > 0) {
            Element dataElement = mTypeUtils.asElement(typeArguments.get(0));
            ParseByJsonRecursiveFieldClass recursiveFieldClass = new ParseByJsonRecursiveFieldClass(
                    dataElement, mTypeUtils
            );

            ClassName listClass = ClassName.get("java.util", "LinkedList");
            ClassName jsonArrayClass = ClassName.get(
                    "org.json", "JSONArray"
            );
            ClassName jsonObjectClass = ClassName.get(
                    "org.json", "JSONObject"
            );
            String tmpListName = String.format("%sTmpList", getParseByKey());
            String jsonArrayName = String.format("%sJsonArray", getParseByKey());
            if (recursiveFieldClass.isRecursive()) {
                ClassName parseByJsonCreatorClass = ClassName.get("com.blackirwin", "ParseByer");
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
                        .addStatement("$L = $L", variableName, tmpListName);

                addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions", "InitCreatorClassException"));
                addException(new ParseByJsonExceptionRecord("com.blackirwin.exceptions", "MethodInvokeException"));
                return true;
            }
            else if (dataElement.getSimpleName().toString().equals(String.class.getSimpleName())){
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
                        .addStatement("$L = $L", variableName, tmpListName);
                return true;
            }
            else {
                return false;
            }

        }
        return false;
    }

    @Override
    public boolean addGenerateTestDataCode(CodeBlock.Builder methodBuilder, String variableName) {
        if (isSkipTestCreate()) {
            return false;
        }

        DeclaredType typeMirror = (DeclaredType) getVariableElement().asType();
        List<? extends TypeMirror> typeArguments = typeMirror.getTypeArguments();
        if (typeArguments != null && typeArguments.size() > 0) {
            Element dataElement = mTypeUtils.asElement(typeArguments.get(0));
            ParseByJsonRecursiveFieldClass recursiveFieldClass = new ParseByJsonRecursiveFieldClass(
                    dataElement, mTypeUtils
            );

            ClassName parseByJsonCreatorClass = ClassName.get("com.blackirwin", "ParseByer");
            ClassName listClass = ClassName.get("java.util", "LinkedList");
            String tmpListName = String.format("%sTmpList", getParseByKey());
            ParseByTestArray arrayParser = getVariableElement().getAnnotation(ParseByTestArray.class);
            String arrayLength = String.valueOf(arrayParser == null ? PARSE_TEST_ARRAY_MAX_LENGTH
                    : arrayParser.maxLength());
            ClassName testDataCreatorClass = ClassName.get("com.blackirwin.utils","RandomDataUtils");
            if (recursiveFieldClass.isRecursive()) {
                String dataClassName = dataElement.getSimpleName().toString();
                ClassName recursiveClass = ClassName.get(
                        dataElement.getEnclosingElement().toString(), dataClassName
                );
                methodBuilder.addStatement("$T<$L> $L = new $T<>()",
                        listClass, dataClassName, tmpListName, listClass)
                        .addStatement("int length = $T.createInt($L)", testDataCreatorClass, arrayLength)
                        .beginControlFlow("for (int i = 0; i < length; i++)")
                        .addStatement("$L.add($T.$L($T.class))", tmpListName, parseByJsonCreatorClass,
                                PARSE_CLASS_TEST_DATA_METHOD_NAME, recursiveClass)
                        .endControlFlow()
                        .addStatement("$L = $L", variableName, tmpListName);
                return true;
            }
            else if (dataElement.getSimpleName().toString().equals(String.class.getSimpleName())){
                String className = "String";
                ParseByTestString stringParser = getVariableElement().getAnnotation(ParseByTestString.class);
                String stringLength = String.valueOf(
                        stringParser == null ? PARSE_TEST_STRING_MAX_LENGTH : stringParser.maxLength()
                );
                methodBuilder.addStatement("$T<$L> $L = new $T<>()",
                        listClass, className, tmpListName, listClass)
                        .addStatement("int length = $T.createInt($L)", testDataCreatorClass, arrayLength)
                        .beginControlFlow("for (int i = 0; i < length; i++)")
                        .addStatement("$L.add($T.createString($T.createInt($L)))", tmpListName, testDataCreatorClass,
                                testDataCreatorClass, stringLength)
                        .endControlFlow()
                        .addStatement("$L = $L", variableName, tmpListName);
                return true;
            }
            else {
                return false;
            }

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
                .beginControlFlow("for (int i = 0; i < $L.$L.size(); i++)", variableName, getVariableName())
                .addStatement(
                        "$L.put($L.$L.get(i))", jsonArrayName, variableName, getVariableName()
                )
                .endControlFlow()
                .addStatement(
                    "$L.putOpt($S,$L.toString())", jsonVariable, getParseToKey(), jsonArrayName
                );
        addException(new ParseByJsonExceptionRecord("org.json","JSONException"));
        return true;
    }
}
