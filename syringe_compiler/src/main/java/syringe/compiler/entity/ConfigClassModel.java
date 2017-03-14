package syringe.compiler.entity;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import syringe.compiler.Preconditions;
/**
 * Created by Fred Zhao on 2017/3/6.
 */

public class ConfigClassModel {

    private String configClassName = "";

    private String configPackageName = "";

    private Element element;

    public ConfigClassModel(Element element) {

        this.element = element;
        if (isConfigElementAdopt()) {
            String packageName = ((TypeElement) element).getQualifiedName().toString();
            configPackageName = packageName.substring(0, packageName.lastIndexOf("."));
            configClassName = element.getSimpleName().toString();
        }
    }

    public String getConfigClassName() {

        return configClassName;
    }

    public String getConfigPackageName() {

        return configPackageName;
    }

    public boolean isConfigElementAdopt() {

        return Preconditions.checkClass(this.element) && Preconditions.checkConfig((TypeElement) element);
    }
}
