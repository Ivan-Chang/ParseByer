package com.blackirwin.datamodel;

import com.blackirwin.annotations.ParseByJson;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

/**
 * Created by blackirwin on 2017/6/23.
 */

public final class ParseByJsonFieldClassFactory {

    public static ParseByJsonFieldClass createFieldClass(Element e, Types typeUtil){

        VariableElement variableElement = (VariableElement) e;
        TypeKind kind = variableElement.asType().getKind();
        if (kind.isPrimitive()){
            return new ParseByJsonPrimitiveKindFieldClass(variableElement, typeUtil);
        }

        if (kind == TypeKind.ARRAY){
            return new ParseByJsonArrayFieldClass(variableElement, typeUtil);
        }

        if (kind == TypeKind.DECLARED){
            Element variableKindElement = typeUtil.asElement(variableElement.asType());
            String variableKindSimpleName = variableKindElement.getSimpleName().toString();
            if (variableKindSimpleName.equals(String.class.getSimpleName())){
                return new ParseByJsonStringFieldClass(variableElement, typeUtil);
            }

            if (variableKindSimpleName.equals(List.class.getSimpleName())){
                return new ParseByJsonListFieldClass(variableElement, typeUtil);
            }

            if(variableKindElement.getAnnotation(ParseByJson.class) != null){
                return new ParseByJsonRecursiveFieldClass(variableElement, typeUtil);
            }
        }

        return null;
    }

    private ParseByJsonFieldClassFactory(){}
}
