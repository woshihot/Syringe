package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

import syringe.compiler.Constants;
import syringe.compiler.KeyWord;
import syringe.compiler.entity.ServiceModel;

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
        TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(Constants.SERVICE_MANAGER_BUILDER_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        MethodSpec.Builder newBuilderMethod = MethodSpec.methodBuilder(Constants.NEW_BUILDER_NAME).addModifiers
                (Modifier.PUBLIC, Modifier.STATIC).returns(TypeVariableName.get(Constants.SERVICE_MANAGER_BUILDER_NAME))
                .addStatement("return new $N()", Constants.SERVICE_MANAGER_BUILDER_NAME);
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC).returns(getClassName());
        StringBuilder serviceManagerSb = new StringBuilder();
        serviceManagerSb.append("return new " + Constants.SERVICE_MANAGER_CLASS_NAME + "(");
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
            nestedBuilder.addField(serviceClz, serviceName, Modifier.PRIVATE);
            nestedBuilder.addMethod(MethodSpec.methodBuilder(getBestName(className))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeVariableName.get(Constants.SERVICE_MANAGER_BUILDER_NAME))
                    .addParameter(serviceClz, serviceName)
                    .addStatement("this.$N = $N", serviceName, serviceName)
                    .addStatement("return this")
                    .build());
            serviceManagerSb.append(serviceName + ",");
        }
        serviceManagerSb.delete(serviceManagerSb.length() - 1, serviceManagerSb.length());
        serviceManagerSb.append(")");
        overrideMethod.addStatement("return null");
        builder.addMethod(constructorMethod.build());
        builder.addMethod(overrideMethod.build());
        if (isServiceSimple()) {
            nestedBuilder.addMethod(buildMethod.addStatement(serviceManagerSb.toString()).build());
            builder.addType(nestedBuilder.build());
            builder.addMethod(newBuilderMethod.build());
        }
        return builder.build();
    }

    private boolean isServiceSimple() {

        Set<String> serviceNames = new HashSet<>();
        for (ServiceModel serviceModel : mServiceModels) {
            String serviceName = serviceModel.getServiceClassName();
            serviceNames.add(serviceName);
        }
        return serviceNames.size() == mServiceModels.size();
    }

    private String getBestName(String serviceName) {

        char[] chars = serviceName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLowerCase(chars[ i ])) {
                break;
            }
            if ("Service".equals(serviceName.substring(i))) {
                break;
            }
            chars[ i ] = Character.toLowerCase(chars[ i ]);
        }
        String result = String.valueOf(chars);
        if (result.equals(KeyWord.NEW) ||
                result.equals(KeyWord.DEFAULT) ||
                result.equals(KeyWord.NATIVE) ||
                result.equals(KeyWord.PUBLIC) ||
                result.equals(KeyWord.SUPER) ||
                result.equals(KeyWord.THIS)) {
            result += "Api";
        }
        return result;
    }

    /*
    if (IService.class == service) {
        return (T) mIService;
    }*/

}
