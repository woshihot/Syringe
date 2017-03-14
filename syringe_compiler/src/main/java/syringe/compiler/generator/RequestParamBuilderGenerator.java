package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.HashMap;
import java.util.Set;

import javax.lang.model.element.Modifier;

import syringe.compiler.Constants;
import syringe.compiler.entity.ConfigClassModel;
import syringe.compiler.entity.MethodModel;
import syringe.compiler.entity.ServiceModel;

import static com.squareup.javapoet.MethodSpec.methodBuilder;


/**
 * Created by Fred Zhao on 2017/3/5.
 */

public class RequestParamBuilderGenerator extends FileBox {

    private Set<ServiceModel> mServiceModels;

    private ClassName parent;

    private final String CascadeParamInterfaceClassPath = "com.zhj.syringe.core.request.CascadeParamInterface";

    public static final ClassName subscriberClass = ClassName.bestGuess("com.zhj.syringe.core.response" +
            ".BaseHttpSubscriber");

    public static final ClassName responseFormatClass = ClassName.bestGuess("com.zhj.syringe.core.response" +
            ".HttpResponseFormat");

    public static final ClassName requestFormatClass = ClassName.bestGuess("com.zhj.syringe.core.request" +
            ".HttpRequestFormat");

    private final String _CODE_METHOD_method = "method";

    private final String _CODE_METHOD_getMethodType = "getMethodType";

    private ConfigClassModel mConfigClassModel;

    public RequestParamBuilderGenerator(String packageName, Set<ServiceModel> serviceModels, ConfigClassModel
            configClassModel) {

        super(packageName);
        this.mServiceModels = serviceModels;
        this.mConfigClassModel = configClassModel;
        setClassName(configBuilderClassName(packageName));
        parent = ClassName.bestGuess(Constants.REQUEST_PARENT_PACKAGE.concat(".").concat(Constants
                .REQUEST_PARAM_BUILDER_PARENT_NAME));
    }

    @Override
    public TypeSpec getTypeSpec() {

        TypeSpec.Builder result = TypeSpec.classBuilder(getClassName()).addModifiers(Modifier.PUBLIC)
                .superclass(TypeVariableName.get(Constants.REQUEST_PARENT_PACKAGE.concat(".").concat(Constants
                        .REQUEST_PARAM_BUILDER_PARENT_NAME).concat("<").concat(getClassName().simpleName()).concat
                        (">")));

        result.addField(MethodEnumTypeGenerator.configMethodTypeClassName(getClassName().packageName()), "mMethodType");
        for (ServiceModel serviceModel : mServiceModels) {
            result.addMethod(addServiceMethod(getClassName().packageName(), serviceModel.getServiceClassName
                    (), serviceModel.getServicePackageName()));
        }
        result.addMethod(addMethodType());
        result.addMethod(addGetMethodType());
        result.addMethod(addBuildMethod());
        return result.build();
    }

    private MethodSpec addServiceMethod(String packageName, String className, String servicePackage) {

        ClassName paramClz = ServiceTypeGenerator.configTypeClassName(servicePackage, className);
        ClassName returnClz = CopyGenerator.configCopyClassName(packageName, className.concat(Constants.pkgSuffix
                (servicePackage)));
        return methodBuilder("service").addModifiers(Modifier.PUBLIC).addParameter(paramClz, "service")
                .addStatement("return new $T(this)", returnClz)
                .returns(returnClz).build();
    }

    private MethodSpec addMethodType() {

        return methodBuilder(_CODE_METHOD_method).addParameter(MethodEnumTypeGenerator
                .configMethodTypeClassName(getClassName().packageName()), "methodType").addStatement("this" +
                ".mMethodType = methodType").returns(getClassName()).addStatement("return this").build();
    }

    private MethodSpec addGetMethodType() {

        return methodBuilder(_CODE_METHOD_getMethodType).addStatement("return this.mMethodType").returns
                (MethodEnumTypeGenerator.configMethodTypeClassName(getClassName().packageName())).build();
    }

    private MethodSpec addBuildMethod() {

        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build").addModifiers(Modifier.PUBLIC).addAnnotation
                (Override.class).returns(ClassName.bestGuess(Constants.REQUEST_PARENT_PACKAGE.concat(".").concat
                (Constants.REQUEST_PARAM_PARENT_NAME)));
        buildMethod.addStatement("if (null == getParam()) param(new $T<String,Object>())", ClassName.get(HashMap
                .class));
        buildMethod.addStatement("if (null == getCascadeParamInterface()) cascade(new $T())", ClassName.bestGuess
                (CascadeParamInterfaceClassPath.concat(".DefaultCascadeParamImpl")));

        buildMethod.addStatement("if (null == getHttpRequestFormat()) requestFormat($T.getInstance().$L)",
                baseConfigClassName
                        (mConfigClassModel), Constants.BASE_FIELD_REQUEST_FORMAT);
        buildMethod.addStatement("if (null == getHttpResponseFormat()) responseFormat($T.getInstance().$L)",
                baseConfigClassName
                (mConfigClassModel), Constants.BASE_FIELD_RESPONSE_FORMAT);
        buildMethod.addStatement("if (null == getHttpSubscriber()) subscriber($T.getInstance().$L)", baseConfigClassName
                (mConfigClassModel), Constants.BASE_FIELD_SUBSCRIBER);
        buildMethod.addStatement("if (null == getMethodType()) method($T.$L)", MethodEnumTypeGenerator
                .configMethodTypeClassName(getClassName().packageName()), getDefaultService());
        buildMethod.addStatement("if (null == getPaths()) paths(new $T<String,Object>())", ClassName.get(HashMap
                .class));
        buildMethod.addStatement("if (null == getHeaders()) headers(new $T<String,String>())", ClassName.get(HashMap
                .class));
        buildMethod.addStatement("return new $T(this)", RequestParamGenerator.configRequestParamClassName(getClassName
                ().packageName()));
        return buildMethod.build();
    }

    public static ClassName configBuilderClassName(String packageName) {

        return ClassName.get(packageName, Constants.REQUEST_PARAM_BUILDER_NAME);
    }

    private ClassName baseConfigClassName(ConfigClassModel configClassModel) {

        return null == configClassModel ? ClassName.bestGuess(Constants.BASE_CONFIG_HOLDER_PATH) : ConfigGenerator
                .configConfigClassName(configClassModel);
    }

    private String getDefaultService() {

        String defaultMethod = null;
        for (ServiceModel serviceModel : mServiceModels) {
            for (MethodModel methodModel : serviceModel.getMethodModels()) {
                if (methodModel.isDefault())
                    defaultMethod = Constants.methodTypeConfig(serviceModel.getServicePackageName(), serviceModel
                            .getServiceClassName(), methodModel.getMethodName());
            }
        }
        if (null == defaultMethod) {
            ServiceModel serviceModel = mServiceModels.iterator().next();
            MethodModel methodModel = serviceModel.getMethodModels().iterator().next();
            defaultMethod = Constants.methodTypeConfig(serviceModel.getServicePackageName(), serviceModel
                    .getServiceClassName(), methodModel.getMethodName());
        }
        return defaultMethod;
    }
}
