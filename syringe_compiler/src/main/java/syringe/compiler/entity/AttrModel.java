package syringe.compiler.entity;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import syringe.compiler.Constants;
import syringe.compiler.Preconditions;
/**
 * Created by Fred Zhao on 2017/5/5.
 */

public class AttrModel {

    private Element mElement;

    private String tagName;

    private String methodName;

    private String builderName;

    private String fieldTypeName;


    public AttrModel(Element element) {

        mElement = element;
        if (isAttrAdopt()) {
            VariableElement fieldEle = (VariableElement) mElement;
            fieldTypeName = TypeName.get(fieldEle.asType()).toString();
            tagName = fieldEle.getSimpleName().toString();
            TypeElement builderEle = (TypeElement) fieldEle.getEnclosingElement();
            builderName = builderEle.getQualifiedName().toString();
            ExecutableElement methodEle = getMethodEle(builderEle);
            if (null != methodEle) methodName = methodEle.getSimpleName().toString();
        }
    }

    private ExecutableElement getMethodEle(TypeElement builderEle) {

        List<ExecutableElement> methods = new ArrayList<>();
        for (Element broEle : builderEle.getEnclosedElements()) {
            if (broEle instanceof ExecutableElement) {
                if (Preconditions.isAttrMethod((ExecutableElement) broEle, this.builderName, this.fieldTypeName))
                    methods.add((ExecutableElement) broEle);
            }
        }
        ExecutableElement methodEle = null;
        if (methods.size() == 1) methodEle = methods.get(0);
        else {
            for (ExecutableElement method : methods) {
                if (Constants.getTrulyMethodName(method.getSimpleName().toString()).equals(Constants
                        .getTrulyFieldName(this.tagName)))
                    methodEle = method;
            }
        }
        return methodEle;

    }


    public String getTagName() {

        return tagName;
    }

    public String getMethodName() {

        return methodName;
    }

    public String getBuilderName() {

        return builderName;
    }

    public String getFieldTypeName() {

        return fieldTypeName;
    }

    public boolean isAttrAdopt() {

        return Preconditions.isAttrField(mElement);
    }

    public boolean isNotNull() {

        return null != tagName
                && null != methodName
                && null != builderName
                && null != fieldTypeName;
    }

}
