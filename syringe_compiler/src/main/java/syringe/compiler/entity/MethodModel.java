package syringe.compiler.entity;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import syringe.compiler.Preconditions;

import static syringe.compiler.Constants.DEFAULTPOST_ANNOTATION;
/**
 * Created by Fred Zhao on 2017/3/2.
 */

public class MethodModel {

    private String methodName;

    private Set<ParamModel> mParamModels = new LinkedHashSet<>();

    private boolean isDefault;

    private Element mElement;

    public MethodModel(Element methodElement) {

        mElement = methodElement;

        if (isMethodAdopt()) {
            this.methodName = methodElement.getSimpleName().toString();
            mParamModels = new LinkedHashSet<>();
            for (VariableElement element : ((ExecutableElement) methodElement).getParameters()) {
                mParamModels.add(new ParamModel(element));
            }
            for (AnnotationMirror mirror : methodElement.getAnnotationMirrors()) {
                if (DEFAULTPOST_ANNOTATION.equals(mirror.getAnnotationType().asElement().getSimpleName().toString
                        ()))
                    isDefault = true;
            }
        }

    }

    public String getMethodName() {

        return methodName;
    }


    public Set<ParamModel> getParamModels() {

        return mParamModels;
    }

    public boolean isDefault() {

        return isDefault;
    }

    public Element getElement() {

        return mElement;
    }

    public boolean isMethodAdopt() {

        return Preconditions.checkMethod(this.mElement) && Preconditions.checkObservable((ExecutableElement) this
                .mElement);
    }

}
