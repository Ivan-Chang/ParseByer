package com.blackirwin.datamodel;

import com.blackirwin.annotations.JsonSkip;
import com.blackirwin.annotations.ParseByJson;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.blackirwin.RelativeConst.PARSE_CLASS_METHOD_NAME;
import static com.blackirwin.RelativeConst.PARSE_CLASS_SUFFIX;
import static com.blackirwin.RelativeConst.PARSE_CLASS_TEST_DATA_METHOD_NAME;
import static com.blackirwin.RelativeConst.PARSE_CLASS_TO_JSON;

/**
 * Created by blackirwin on 2017/6/20.
 */

public class ParseByJsonAnnotatedClass {

    private TypeElement mAnnotatedClassElement;
    private String mQualifiedSuperClassName;
    private String mSimpleTypeName;
    private Types mTypeUtils;

    private List<MethodSpec.Builder> methods = new LinkedList<>();

    public ParseByJsonAnnotatedClass(TypeElement classElement, Types typeUtils)
            throws IllegalArgumentException{

        if (classElement == null){
            throw new IllegalArgumentException(
                    ParseByJsonAnnotatedClass.class.getSimpleName() + " cannot initialize with null"
            );
        }

        mTypeUtils = typeUtils;
        mAnnotatedClassElement = classElement;
        mQualifiedSuperClassName = classElement.getQualifiedName().toString();
        mSimpleTypeName = classElement.getSimpleName().toString();
    }

    /**
     * 获取被{@link ParseByJson}注释的类的合法全名
     *
     * @return qualified name
     */
    public String getQualifiedSuperClassName(){
        return mQualifiedSuperClassName;
    }

    /**
     * 获取被{@link ParseByJson}注释的类的简单名字
     *
     * @return simple name
     */
    public String getSimpleTypeName(){
        return mSimpleTypeName;
    }

    /**
     * 获取被{@link ParseByJson}注解的原始元素
     */
    public TypeElement getTypeElement() {
        return mAnnotatedClassElement;
    }

    private void generateParseByJsonCode(Elements elementUtils) {

        Set<ParseByJsonExceptionRecord> exceptionRecords = new HashSet<>();
        MethodSpec.Builder parseJsonMethodSpecBuilder = MethodSpec.methodBuilder(PARSE_CLASS_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(ClassName.get(mAnnotatedClassElement.asType()), "data")
                .addParameter(String.class, "jsonString");

        ClassName jsonClass = ClassName.get("org.json","JSONObject");
        exceptionRecords.add(new ParseByJsonExceptionRecord("org.json","JSONException"));
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.addStatement("\t$T jsonObject = new $T(jsonString)", jsonClass, jsonClass)
                .add("if (jsonObject != null) {\n");
        int usableNum = 0;
        for (Element e : elementUtils.getAllMembers(mAnnotatedClassElement)){
            if (e.getModifiers().contains(Modifier.FINAL)
                    || e.getModifiers().contains(Modifier.STATIC)
                    || e.getKind() != ElementKind.FIELD
                    || e.getAnnotation(JsonSkip.class) != null){
                continue;
            }

            ParseByJsonFieldClass fieldClass
                    = ParseByJsonFieldClassFactory.createFieldClass(e, mTypeUtils);

            if (fieldClass != null && fieldClass.addParseByJsonCode(
                    codeBuilder,
                    String.format("data.%s", fieldClass.getVariableName()),
                    "jsonObject"
            )){
                usableNum++;
            }

            if (fieldClass != null){
                exceptionRecords.addAll(fieldClass.getCatchExceptions());
            }
        }

        if (usableNum > 0) {
            codeBuilder.add("}\n");
            if (exceptionRecords.size() > 0) {
                parseJsonMethodSpecBuilder
                        .addCode("try{\n")
                        .addCode(codeBuilder.build())
                        .addCode("}");

                for (ParseByJsonExceptionRecord record : exceptionRecords) {
                    ClassName exceptionClass = ClassName.get(
                            record.packageName, record.simpleName);
                    parseJsonMethodSpecBuilder
                            .addCode("catch ($T e){\n", exceptionClass)
                            .addStatement("\te.printStackTrace()")
                            .addStatement("\treturn false")
                            .addCode("}\n");
                }
            }
            else {
                parseJsonMethodSpecBuilder.addCode(codeBuilder.build());
            }
            parseJsonMethodSpecBuilder.addStatement("return true");

            methods.add(parseJsonMethodSpecBuilder);
        }
    }

    private void generateTestDataCode(Elements elementUtils) {
        Set<ParseByJsonExceptionRecord> exceptionRecords = new HashSet<>();
        TypeName returnType = TypeName.get(getTypeElement().asType());
        MethodSpec.Builder testCreateMethodSpecBuilder = MethodSpec.methodBuilder(PARSE_CLASS_TEST_DATA_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnType);

        String className = getTypeElement().getSimpleName().toString();
        String classVaribleName = "result";
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        int usableNum = 0;
        for (Element e : elementUtils.getAllMembers(mAnnotatedClassElement)){
            if (e.getModifiers().contains(Modifier.FINAL)
                    || e.getModifiers().contains(Modifier.STATIC)
                    || e.getKind() != ElementKind.FIELD
                    || e.getAnnotation(JsonSkip.class) != null){
                continue;
            }

            ParseByJsonFieldClass fieldClass
                    = ParseByJsonFieldClassFactory.createFieldClass(e, mTypeUtils);

            if (fieldClass != null && fieldClass.addGenerateTestDataCode(
                    codeBlockBuilder,
                    classVaribleName+"."+fieldClass.getVariableName()
            )){
                usableNum++;
            }

            if (fieldClass != null){
                exceptionRecords.addAll(fieldClass.getCatchExceptions());
            }
        }

        if (usableNum > 0) {
            testCreateMethodSpecBuilder.addStatement("$L $L = new $L()", className, classVaribleName, className);
            if (exceptionRecords.size() > 0) {
                testCreateMethodSpecBuilder.addCode("try{\n")
                        .addCode(codeBlockBuilder.build())
                        .addCode("}\n");

                for (ParseByJsonExceptionRecord record : exceptionRecords) {
                    ClassName exceptionClass = ClassName.get(
                            record.packageName, record.simpleName);
                    testCreateMethodSpecBuilder
                            .addCode("catch ($T e){\n", exceptionClass)
                            .addStatement("\te.printStackTrace()")
                            .addStatement("\treturn $L", classVaribleName)
                            .addCode("}\n");
                }
            }
            else {
                testCreateMethodSpecBuilder.addCode(codeBlockBuilder.build());
            }
            testCreateMethodSpecBuilder.addStatement("return $L", classVaribleName);

            methods.add(testCreateMethodSpecBuilder);
        }
    }

    private void generateParseToCode(Elements elementUtils){
        Set<ParseByJsonExceptionRecord> exceptionRecords = new HashSet<>();

        ClassName stringClass = ClassName.get("java.lang", "String");
        ClassName jsonClass = ClassName.get("org.json","JSONObject");

        String methodParameter = "data";

        TypeName returnType = TypeName.get(elementUtils.getTypeElement(stringClass.toString()).asType());
        MethodSpec.Builder paseToMethodSpecBuilder = MethodSpec.methodBuilder(PARSE_CLASS_TO_JSON)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnType)
                .addParameter(ClassName.get(mAnnotatedClassElement.asType()), methodParameter);

        String classVaribleName = "result";
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        int usableNum = 0;
        for (Element e : elementUtils.getAllMembers(mAnnotatedClassElement)){
            if (e.getModifiers().contains(Modifier.FINAL)
                    || e.getModifiers().contains(Modifier.STATIC)
                    || e.getKind() != ElementKind.FIELD
                    || e.getAnnotation(JsonSkip.class) != null){
                continue;
            }

            ParseByJsonFieldClass fieldClass
                    = ParseByJsonFieldClassFactory.createFieldClass(e, mTypeUtils);

            if (fieldClass != null && fieldClass.addParseToCode(
                    codeBlockBuilder,
                    classVaribleName,
                    methodParameter
            )){
                usableNum++;
            }

            if (fieldClass != null){
                exceptionRecords.addAll(fieldClass.getCatchExceptions());
            }
        }

        if (usableNum > 0) {
            paseToMethodSpecBuilder.addStatement("$T $L = new $T()", jsonClass, classVaribleName, jsonClass);
            if (exceptionRecords.size() > 0) {
                paseToMethodSpecBuilder.addCode("try{\n")
                        .addCode(codeBlockBuilder.build())
                        .addCode("}\n");

                for (ParseByJsonExceptionRecord record : exceptionRecords) {
                    ClassName exceptionClass = ClassName.get(
                            record.packageName, record.simpleName);
                    paseToMethodSpecBuilder
                            .addCode("catch ($T e){\n", exceptionClass)
                            .addStatement("\te.printStackTrace()")
                            .addStatement("\treturn \"\"")
                            .addCode("}\n");
                }
            }
            else {
                paseToMethodSpecBuilder.addCode(codeBlockBuilder.build());
            }
            paseToMethodSpecBuilder.addStatement("return $L.toString()", classVaribleName);

            methods.add(paseToMethodSpecBuilder);
        }
    }

    public void generateClassFile(Elements elementUtils, Filer filer) throws IOException {
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(getSimpleTypeName() + PARSE_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        generateParseByJsonCode(elementUtils);
        generateTestDataCode(elementUtils);
        generateParseToCode(elementUtils);
        for (MethodSpec.Builder b : methods){
            typeSpecBuilder.addMethod(b.build());
        }
        if (methods.size() > 0) {
            methods.clear();
            JavaFile javaFile = JavaFile.builder(
                    elementUtils.getPackageOf(mAnnotatedClassElement).getQualifiedName().toString(),
                    typeSpecBuilder.build()
            ).build();
            javaFile.writeTo(filer);
        }
    }
}
