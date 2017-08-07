package com.blackirwin.datamodel;

import com.blackirwin.annotations.JsonKey;
import com.blackirwin.annotations.JsonSkip;
import com.squareup.javapoet.CodeBlock;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

/**
 * Created by blackirwin on 2017/6/23.
 */

abstract public class ParseByJsonFieldClass {

    private Element variableElement;
    private Element variableKindElement;
    private String variableName;
    private String variableKindName;
    private boolean skipParseBy;
    private boolean skipParseTo;
    private boolean skipTestCreate;
    private String parseByKey;
    private String parseToKey;
    private List<ParseByJsonExceptionRecord> catchExceptions = new LinkedList<>();

    public ParseByJsonFieldClass(Element variableElement, Types typeUtils){
        this.variableElement = variableElement;
        TypeKind kind = variableElement.asType().getKind();
        if (kind.isPrimitive()){
            this.variableKindElement = typeUtils.boxedClass((PrimitiveType) variableElement.asType());
        }
        else {
            this.variableKindElement = typeUtils.asElement(variableElement.asType());
        }
        this.variableName = variableElement.getSimpleName().toString();
        if (this.variableKindElement != null) {
            this.variableKindName = this.variableKindElement.getSimpleName().toString();
        }
        else {
            this.variableKindName = this.variableElement.asType().toString();
        }
        JsonSkip skipAnnotation = variableElement.getAnnotation(JsonSkip.class);
        this.skipParseBy = skipAnnotation != null && skipAnnotation.skipParseBy();
        this.skipParseTo = skipAnnotation != null && skipAnnotation.skipParseTo();
        this.skipTestCreate = skipAnnotation != null && skipAnnotation.skipTestCreate();
        JsonKey jsonKeyAnnotation = variableElement.getAnnotation(JsonKey.class);
        this.parseByKey = jsonKeyAnnotation == null ? this.variableName : jsonKeyAnnotation.parseByKey();
        this.parseToKey = jsonKeyAnnotation == null ? this.variableName : jsonKeyAnnotation.parseToKey();
    }

    abstract public boolean addParseByJsonCode(CodeBlock.Builder methodBuilder, String variableName, String jsonName);
    abstract public boolean addGenerateTestDataCode(CodeBlock.Builder methodBuilder, String variableName);
    abstract public boolean addParseToCode(CodeBlock.Builder methodBuilder, String jsonVariable, String variableName);

    public Element getVariableElement() {
        return variableElement;
    }

    public Element getVariableKindOrBoxElement() {
        return variableKindElement;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getVariableKindOrBoxName() {
        return variableKindName;
    }

    public boolean isSkipParseBy() {
        return skipParseBy;
    }

    public boolean isSkipParseTo() {
        return skipParseTo;
    }

    public boolean isSkipTestCreate() {
        return skipTestCreate;
    }

    public String getParseByKey() {
        return parseByKey;
    }
    public String getParseToKey() {
        return parseToKey;
    }

    public List<ParseByJsonExceptionRecord> getCatchExceptions(){
        return catchExceptions;
    }

    protected void addException(ParseByJsonExceptionRecord exception){
        catchExceptions.add(exception);
    }
}
