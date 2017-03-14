package syringe.compiler.entity;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import syringe.compiler.Preconditions;
/**
 * Created by Fred Zhao on 2017/3/2.
 */

public class ServiceModel {

    private String serviceClassName;

    private String servicePackageName;

    private Set<MethodModel> mMethodModels = new LinkedHashSet<>();

    private Element mElement;


    public ServiceModel(Element typeElement) {

        mElement = typeElement;
        if (Preconditions.checkInterface(typeElement)) {
            String packageName = ((TypeElement) typeElement).getQualifiedName().toString();
            packageName = packageName.substring(0, packageName.lastIndexOf("."));
            mMethodModels = new LinkedHashSet<>();
            for (Element element : typeElement.getEnclosedElements()) {
                mMethodModels.add(new MethodModel(element));
            }
            if (!mMethodModels.isEmpty()) {
                this.servicePackageName = packageName;
                this.serviceClassName = typeElement.getSimpleName().toString();
            }
        }
    }

    public String getServiceClassName() {

        return serviceClassName;
    }


    public String getServicePackageName() {

        return servicePackageName;
    }

    public Set<MethodModel> getMethodModels() {

        return mMethodModels;
    }

    public MethodModel getDefault() {

        MethodModel methodModel = null;
        for (MethodModel methodModelIn : mMethodModels) {
            if (methodModelIn.isDefault()) methodModel = methodModelIn;
        }
        return methodModel;
    }

    public Element getElement() {

        return mElement;
    }
}
