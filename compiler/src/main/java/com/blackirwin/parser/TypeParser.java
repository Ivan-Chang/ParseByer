package com.blackirwin.parser;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by blackirwin on 2017/7/14.
 */

public class TypeParser {

    public interface SelectFilter{
        boolean intercept(Element element);
    }

    private Elements elementUtils;
    private Types typeUtils;

    private TypeElement typeElement;
    private List<? extends Element> childElements;
    public TypeParser(TypeElement typeElement, Elements elementUtils, Types typeUtils){
        if (typeElement != null) {
            this.typeElement = typeElement;
            if (elementUtils != null) {
                this.elementUtils = elementUtils;
                childElements = this.elementUtils.getAllMembers(this.typeElement);
            }
        }
        this.typeUtils = typeUtils;
    }

    public Collection<? extends Element> getVariableElements(final SelectFilter filter){
        if (childElements != null) {
            return Collections2.filter(childElements, new Predicate<Element>() {
                @Override
                public boolean apply(@Nullable Element input) {
                    return input != null && input.getKind() == ElementKind.FIELD && !filter.intercept(input);
                }
            });
        }
        else {
            return Collections.emptyList();
        }
    }


}
