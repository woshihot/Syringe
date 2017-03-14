package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import java.util.Set;

import javax.lang.model.element.Modifier;

import syringe.compiler.Constants;
import syringe.compiler.entity.MethodModel;
import syringe.compiler.entity.ServiceModel;
import syringe.compiler.generator.FileBox;
/**
 * Created by Fred Zhao on 2017/3/2.
 */

public class MethodEnumTypeGenerator extends FileBox {

    private Set<ServiceModel> mServiceModels;

    public MethodEnumTypeGenerator(String packageName, Set<ServiceModel> serviceModels) {

        super(packageName);
        setClassName(configMethodTypeClassName(packageName));
        this.mServiceModels = serviceModels;
    }

    @Override
    public TypeSpec getTypeSpec() {

        TypeSpec.Builder builder = TypeSpec.enumBuilder(getClassName()).addModifiers(Modifier.PUBLIC);
        for (ServiceModel serviceModel : mServiceModels) {
            for (MethodModel methodModel : serviceModel.getMethodModels()) {
                builder.addEnumConstant(Constants.methodTypeConfig(serviceModel.getServicePackageName(), serviceModel
                        .getServiceClassName(), methodModel.getMethodName()));
            }
        }
        return builder.build();
    }

    public static ClassName configMethodTypeClassName(String packageName) {

        return ClassName.get(packageName, Constants.METHOD_ENUM_CLASS_NAME);
    }

}
