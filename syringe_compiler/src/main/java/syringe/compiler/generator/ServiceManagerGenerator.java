package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.Set;

import javax.lang.model.element.Modifier;

import syringe.compiler.Constants;
import syringe.compiler.entity.ServiceModel;
import syringe.compiler.generator.FileBox;

import static syringe.compiler.Constants.SERVICE_MANAGER_PARENT_NAME;
import static syringe.compiler.Constants.SERVICE_MANAGER_PARENT_PACKAGE;
import static syringe.compiler.Constants.serviceNameConfig;
/**
 * Created by Fred Zhao on 2017/3/2.
 */

public class ServiceManagerGenerator extends FileBox {

    private Set<ServiceModel> mServiceModels;

    private ClassName parent;

    public static final String _CODE_METHOD_getService = "getService";


    private final String _CODE_PARAM_NAME_service = "service";

    public ServiceManagerGenerator(String packageName, Set<ServiceModel> serviceModels) {

        super(packageName);
        this.mServiceModels = serviceModels;
        setClassName(ClassName.get(packageName, Constants.SERVICE_MANAGER_CLASS_NAME));
        parent = ClassName.bestGuess(SERVICE_MANAGER_PARENT_PACKAGE.concat(".").concat(SERVICE_MANAGER_PARENT_NAME));
    }

    @Override
    public TypeSpec getTypeSpec() {

        TypeSpec.Builder builder = TypeSpec.classBuilder(getClassName()).addModifiers(Modifier.PUBLIC).superclass
                (parent);
        MethodSpec.Builder constructorMethod = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder overrideMethod = MethodSpec.methodBuilder(_CODE_METHOD_getService).addAnnotation
                (Override.class).addTypeVariable(TypeVariableName.get("T")).addParameter(TypeVariableName.get
                ("Class<T>"), _CODE_PARAM_NAME_service)
                .addModifiers(Modifier.PUBLIC).returns(TypeVariableName.get("T"));
        for (ServiceModel serviceModel : mServiceModels) {
            String packageName = serviceModel.getServicePackageName();
            String className = serviceModel.getServiceClassName();
            ClassName serviceClz = ClassName.get(packageName, className);
            String serviceName = serviceNameConfig(packageName, className);
            builder.addField(serviceClz, serviceName,
                    Modifier.PRIVATE, Modifier.FINAL);
            constructorMethod.addParameter(serviceClz, serviceName)
                    .addStatement("this.$N = $N", serviceName, serviceName);
            overrideMethod.addCode(CodeBlock.builder()
                    .addStatement("if ($T.class == $N) return (T) $L", serviceClz, _CODE_PARAM_NAME_service,
                            serviceName).build());
        }
        overrideMethod.addStatement("return null");
        builder.addMethod(constructorMethod.build());
        builder.addMethod(overrideMethod.build());
        return builder.build();
    }
    /*
    if (IService.class == service) {
        return (T) mIService;
    }*/

}
