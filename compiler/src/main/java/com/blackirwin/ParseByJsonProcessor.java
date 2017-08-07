package com.blackirwin;

import com.blackirwin.annotations.JsonSkip;
import com.blackirwin.annotations.ParseByJson;
import com.blackirwin.datamodel.ParseByJsonAnnotatedClass;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by blackirwin on 2017/6/15.
 */
@AutoService(Processor.class)
public class ParseByJsonProcessor extends AbstractProcessor{

    private Elements mElementUtil;
    private Messager mMessager;
    private Filer mFiler;
    private Types mTypeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtil = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        mTypeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ParseByJson.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        // 获取所有注解了ParseByJson的Element
        for (Element element : roundEnv.getElementsAnnotatedWith(ParseByJson.class)) {

            if (element.getKind() != ElementKind.CLASS){
                error(element, "Only classes can be annotated with @%s",
                        ParseByJson.class.getSimpleName());
                return true;
            }

            ParseByJsonAnnotatedClass annotatedClass
                    = new ParseByJsonAnnotatedClass((TypeElement) element, mTypeUtils);
            if (!isValidClass(annotatedClass)){
                return true;
            }

            try {
                annotatedClass.generateClassFile(mElementUtil, mFiler);
            } catch (IOException e) {
                error(null, e.getMessage());
            }

        }
        return true;
    }

    private void error(Element e, String msg, Object... args){
        mMessager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e
        );
    }

    private boolean isValidClass(ParseByJsonAnnotatedClass item){
        TypeElement classElement = item.getTypeElement();

        //检查是否是一个抽象类
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)){
            error(classElement, "%s类是个抽象类，不能被%s注释解析",
                    classElement.getQualifiedName().toString(), ParseByJson.class.getSimpleName());
            return false;
        }

        //检查所有成员变量不是private
        List<? extends Element> members = mElementUtil.getAllMembers(classElement);
        for (Element e : members){
            if (e.getModifiers().contains(Modifier.PRIVATE)
                    && e.getKind() == ElementKind.FIELD
                    && e.getAnnotation(JsonSkip.class) == null){
                error(classElement, "%s类的成员变量是private，无法使用%s注释解析，或者增加%s的注释",
                        classElement.getQualifiedName().toString(),
                        ParseByJson.class.getSimpleName(),
                        JsonSkip.class.getSimpleName());
                return false;
            }
        }

        //检查所有构造函数，确定有一个默认构造函数
        for (Element enclosed : classElement.getEnclosedElements()){
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR){
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                if (constructorElement.getParameters().size() == 0
                        && constructorElement.getModifiers().contains(Modifier.PUBLIC)){
                    return true;
                }
            }
        }

        error(classElement, "the class %s must provide an public empty default constructor",
                classElement.getQualifiedName().toString());
        return false;
    }
}
