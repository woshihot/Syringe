package syringe.compiler.entity;
import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import syringe.compiler.AnnotationType;
import syringe.compiler.Preconditions;
/**
 * Created by Fred Zhao on 2017/3/2.
 */

public class ParamModel {

    private TypeName paramType;

    private Map<String, Map<String, String>> annotations;

    private String paramName;

    private Element mElement;

    private AnnotationType mAnnotationType;

    private String annotationValue;

    public ParamModel(VariableElement element) {

        mElement = element;
        if (Preconditions.checkParam(element)) {
            this.paramType = TypeName.get(element.asType());
            annotations = new LinkedHashMap<>();
            this.paramName = element.getSimpleName().toString();
            for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
                String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
                if (Preconditions.isAnnotationBelongRetrofit(annotationName)) {
                    Map<String, String> valueMap = new HashMap<>();
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror
                            .getElementValues().entrySet()) {
                        valueMap.put(entry.getKey().toString(), entry.getValue().toString());
                    }
                    annotations.put(annotationName, valueMap);
                }
            }
            if (!annotations.isEmpty()) {
                mAnnotationType = AnnotationType.getTypeByName(annotations.entrySet()
                        .iterator().next().getKey());
                Map<String, String> annotationValueMap = annotations.entrySet().iterator().next().getValue();
                if (!annotationValueMap.isEmpty())
                    annotationValue = annotationValueMap.entrySet().iterator().next().getValue();
            }
        }

    }


    public TypeName getParamType() {

        return paramType;
    }

    public Map<String, Map<String, String>> getAnnotations() {

        return annotations;
    }

    public Element getElement() {

        return mElement;
    }

    public AnnotationType getAnnotationType() {

        return mAnnotationType;
    }

    public String getParamName() {

        return paramName;
    }

    public String getAnnotationValue() {

        return annotationValue;
    }
}
