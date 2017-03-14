package syringe.compiler;


import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import syringe.compiler.entity.MethodModel;
import syringe.compiler.entity.ServiceModel;
/**
 * Created by jess on 26/09/2016 13:59
 * Contact with jess.yan.effort@gmail.com
 */

public final class Preconditions {

    public static boolean checkInterface(Element element) {

        return element.getKind().isInterface();
    }

    public static boolean checkMethod(Element element) {

        return element instanceof ExecutableElement;
    }

    public static boolean checkObservable(ExecutableElement element) {

        return element.getReturnType().toString().startsWith(Constants.OBSERVABLE_CLASS_NAME);
    }

    public static boolean checkParam(Element element) {

        return element instanceof VariableElement;
    }

    public static boolean checkClass(Element element) {

        return element.getKind().isClass();
    }

    public static boolean checkConfig(TypeElement element) {

        return element.getSuperclass().toString().equals(Constants.BASE_CONFIG_PATH) && !element.getModifiers()
                .contains(Modifier.ABSTRACT);
    }

    public static boolean hasDefaultAndOnly(Set<ServiceModel> models) {

        Set<MethodModel> defaultMethods = new LinkedHashSet<>();
        for (ServiceModel serviceModel : models) {
            MethodModel defaultM = serviceModel.getDefault();
            if (null != defaultM) {
                defaultMethods.add(defaultM);
            }
        }
        return !defaultMethods.isEmpty() && defaultMethods.size() == 1;
    }

    public static final String[] retrofitAnnotations = {
            Constants.URL_ANNOTATION,
            Constants.BODY_ANNOTATION,
            Constants.PART_ANNOTATION,
            Constants.PART_MAP_ANNOTATION,
            Constants.QUERY_ANNOTATION,
            Constants.QUERY_MAP_ANNOTATION,
            Constants.FIELD_ANNOTATION,
            Constants.FIELD_MAP_ANNOTATION,
            Constants.HEADER_ANNOTATION,
            Constants.HEADER_MAP_ANNOTATION,
            Constants.PATH_ANNOTATION };

    public static boolean isAnnotationBelongRetrofit(String annotationName) {

        return Arrays.asList(retrofitAnnotations).contains(annotationName);
    }



}
