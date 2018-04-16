package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;

import syringe.compiler.AnnotationType;
import syringe.compiler.Constants;
import syringe.compiler.Preconditions;
import syringe.compiler.entity.MethodModel;
import syringe.compiler.entity.ParamModel;
import syringe.compiler.entity.ServiceModel;


/**
 * Created by Fred Zhao on 2017/3/2.
 */

public class RequestParamGenerator extends FileBox {

    private Set<ServiceModel> mServiceModels;

    private ClassName parent;

    private final String REQUEST_PARAM_OVERRIDE_NAME = "getObservable";

    private final String _CODE_FIELD_NAME_methodType = "mMethodType";

//    private final String _CODE_METHOD_NAME_newBuilder = "newBuilder";
//
//    private final String _CODE_PARAM_NAME_builder = "builder";

    private final String _CODE_PARAM_NAME_serviceManager = "serviceManager";

    private final String _CODE_METHOD_getParam = "getHttpRequestFormat().formatApiParam(getParam())";

    private final String _CODE_METHOD_getUrl = "getUrl()";

    private final String _CODE_METHOD_getHeaders = "getHeaders()";

    private final String _CODE_METHOD_getPaths = "getPaths()";

    private final String _CODE_PARAM_VAR = "params";


    public RequestParamGenerator(String packageName, Set<ServiceModel> serviceModels) {

        super(packageName);
        setClassName(configRequestParamClassName(packageName));
        parent = ClassName.bestGuess(Constants.REQUEST_PARENT_PACKAGE.concat(".").concat(Constants
                .REQUEST_PARAM_PARENT_NAME));
        this.mServiceModels = serviceModels;
    }

    @Override
    public TypeSpec getTypeSpec() {

        TypeSpec.Builder result = TypeSpec.classBuilder(getClassName()).addModifiers(Modifier.PUBLIC).superclass
                (parent);
        MethodSpec.Builder constructorMethod = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder builderMethod = MethodSpec.methodBuilder(Constants.NEW_BUILDER_NAME).addModifiers
                (Modifier.PUBLIC).addModifiers(Modifier.STATIC).returns(ClassName.bestGuess(getClassName()
                .packageName().concat(".").concat(Constants.REQUEST_PARAM_BUILDER_NAME)));
        MethodSpec.Builder overrideMethod = MethodSpec.methodBuilder(REQUEST_PARAM_OVERRIDE_NAME)
                .addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
                .returns(ClassName.bestGuess(Constants.OBSERVABLE_CLASS_NAME))
                .addParameter(ClassName.get(Constants.SERVICE_MANAGER_PARENT_PACKAGE, Constants
                        .SERVICE_MANAGER_PARENT_NAME), _CODE_PARAM_NAME_serviceManager);

        result.addField(ClassName.bestGuess(getClassName().packageName().concat(".").concat(Constants
                .METHOD_ENUM_CLASS_NAME)), _CODE_FIELD_NAME_methodType, Modifier.PRIVATE, Modifier.FINAL);

        constructorMethod.addParameter(ClassName.bestGuess(getClassName().packageName().concat(".").concat(Constants
                .REQUEST_PARAM_BUILDER_NAME)), Constants.BUILDER_NAME)
                .addStatement("super($N)", Constants.BUILDER_NAME)
                .addStatement("this.$N =  $N.getMethodType()", _CODE_FIELD_NAME_methodType, Constants.BUILDER_NAME);

        builderMethod.addStatement("return new $N()", Constants.REQUEST_PARAM_BUILDER_NAME);

        overrideMethod.beginControlFlow("try");
        overrideMethod.beginControlFlow("switch(this.$N)", _CODE_FIELD_NAME_methodType);

        CodeBlock defaultCode = null;
        for (ServiceModel serviceModel : mServiceModels) {
            for (MethodModel methodModel : serviceModel.getMethodModels()) {
                if (methodModel.isDefault()) {
                    defaultCode = getReturnCode(serviceModel, methodModel);
                }
                overrideMethod.addCode("$N ".concat(Constants.methodTypeConfig(serviceModel
                        .getServicePackageName(), serviceModel.getServiceClassName(), methodModel.getMethodName())
                        .concat(":").concat("\n")), "case");
                overrideMethod.addCode(getReturnCode(serviceModel, methodModel));
                result.addMethod(getObservableMethodGenerator(serviceModel, methodModel));
            }
        }
        overrideMethod.addCode("default:\n").addCode(getDefaultOb(defaultCode));
        overrideMethod.endControlFlow();
        overrideMethod.addCode("}catch(Exception e){\n");
        overrideMethod.addCode(returnEmpty());
        overrideMethod.endControlFlow();
        result.addMethod(constructorMethod.build());
        result.addMethod(builderMethod.build());
        result.addMethod(overrideMethod.build());

        return result.build();
    }

    private CodeBlock getDefaultOb(CodeBlock defaultCb) {

        CodeBlock.Builder defaultCbb = CodeBlock.builder();
        if (Preconditions.hasDefaultAndOnly(mServiceModels) && defaultCb != null) {
            defaultCbb.add(defaultCb);
        } else {
            Iterator<ServiceModel> serviceIterator = mServiceModels.iterator();
            boolean getMethod = false;
            while (serviceIterator.hasNext() && !getMethod) {
                ServiceModel serviceModel = serviceIterator.next();
                MethodModel methodModel = serviceModel.getMethodModels().iterator().next();
                if (null != methodModel) {
                    defaultCbb.add(getReturnCode(serviceModel, methodModel));
                    getMethod = true;
                }
            }
            if (!getMethod) defaultCbb.add(returnEmpty());
        }
        return defaultCbb.build();
    }

    private CodeBlock returnEmpty() {

        return CodeBlock.builder().addStatement("return $T.empty()", ClassName.bestGuess(Constants
                .OBSERVABLE_CLASS_NAME)).build();
    }

    private CodeBlock getReturnCode(ServiceModel serviceModel, MethodModel methodModel) {

        CodeBlock.Builder returnCbb = CodeBlock.builder();
        returnCbb.addStatement("return ".concat(getObservableMethodName(serviceModel, methodModel)).concat(Constants
                .addBrackets(_CODE_PARAM_NAME_serviceManager)));
//        returnCbb.addStatement("return ".concat(_CODE_PARAM_NAME_serviceManager).concat(".").concat
//                (ServiceManagerGenerator._CODE_METHOD_getService).concat("($T.class)").concat(".").concat
//                (methodModel.getMethodName()).concat("($N)"), ClassName.get(serviceModel.getServicePackageName(),
//                serviceModel.getServiceClassName()), getMethodParamGenerator(methodModel));
        return returnCbb.build();
    }

    private String getObservableMethodName(ServiceModel serviceModel, MethodModel methodModel) {

        return "get".concat(Constants.methodObservableConfig(serviceModel
                .getServicePackageName(), serviceModel.getServiceClassName(), methodModel.getMethodName()));

    }

    private MethodSpec getObservableMethodGenerator(ServiceModel serviceModel, MethodModel methodModel) {

        MethodSpec.Builder methodB = MethodSpec.methodBuilder(getObservableMethodName(serviceModel, methodModel))
                .returns(ClassName.bestGuess(Constants.OBSERVABLE_CLASS_NAME)).addParameter(ClassName.get(Constants
                                .SERVICE_MANAGER_PARENT_PACKAGE, Constants.SERVICE_MANAGER_PARENT_NAME),
                        _CODE_PARAM_NAME_serviceManager).addModifiers(Modifier.PRIVATE);
        boolean isParamOnly = isParamOnly(methodModel.getParamModels());
        if (!isParamOnly) {
            methodB.addCode(paramSplit(methodModel));
        }
        methodB.addStatement("return ".concat(_CODE_PARAM_NAME_serviceManager).concat(".").concat
                (ServiceManagerGenerator._CODE_METHOD_getService).concat("($T.class)").concat(".").concat
                (methodModel.getMethodName()).concat("($N)"), ClassName.get(serviceModel.getServicePackageName(),
                serviceModel.getServiceClassName()), getMethodParamGenerator(methodModel, isParamOnly));
        return methodB.build();
    }

    private CodeBlock paramSplit(MethodModel methodModel) {

        CodeBlock.Builder paramSplitB = CodeBlock.builder();
        paramSplitB.addStatement("$T ".concat(_CODE_PARAM_VAR).concat(" = ").concat(Constants.addBrackets
                ("Map<String,Object>")).concat(_CODE_METHOD_getParam), ClassName.get(Map.class));
        for (ParamModel paramModel : methodModel.getParamModels()) {
            if (paramModel.getAnnotationType().isFromParam()) {
                if (null != paramModel.getAnnotationValue()) {
                    paramSplitB.addStatement("$T ".concat(paramModel.getParamName()).concat(" = ").concat
                            (_CODE_PARAM_VAR).concat(".get").concat(Constants.addBrackets(paramModel
                            .getAnnotationValue())), ClassName.get(Object.class));
                    paramSplitB.addStatement(_CODE_PARAM_VAR.concat(".remove").concat(Constants.addBrackets
                            (paramModel.getAnnotationValue())));
                }

            }
        }
        return paramSplitB.build();
    }

//    private String addQuotationMarks(String str) {
//
//        return "\"".concat(str).concat("\"");
//    }

    private String getMethodParamGenerator(MethodModel methodModel, boolean isParamOnly) {

        StringBuilder sb = new StringBuilder();
        for (ParamModel paramModel : methodModel.getParamModels()) {
            sb.append(paramCode(paramModel, isParamOnly));
            sb.append(",");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    private String paramCode(ParamModel paramModel, boolean isParamOnly) {

        String parameter = null;
        switch (paramModel.getAnnotationType()) {
            case BODY:
                parameter = _CODE_METHOD_getParam;
                break;
            case URL:
                parameter = _CODE_METHOD_getUrl;
                break;
            case PART:
            case QUERY:
            case FIELD:
                parameter = isParamOnly ? _CODE_METHOD_getParam : paramModel.getParamName();
                break;
            case PART_MAP:
            case QUERY_MAP:
            case FIELD_MAP:
                parameter = isParamOnly ? _CODE_METHOD_getParam : _CODE_PARAM_VAR;
                break;
            case HEADER:
                parameter = _CODE_METHOD_getHeaders.concat(".").concat("get").concat(Constants.addBrackets(paramModel
                        .getAnnotationValue()));
                break;
            case HEADER_MAP:
                parameter = _CODE_METHOD_getHeaders;
                break;
            case PATH:
                parameter = _CODE_METHOD_getPaths.concat(".").concat("get").concat(Constants.addBrackets(paramModel
                        .getAnnotationValue()));
                break;
        }
        return null != parameter ? Constants.addBrackets(paramModel.getParamType().toString()).concat(parameter) :
                "null";
    }

    public static ClassName configRequestParamClassName(String packageName) {
        return ClassName.get(packageName, Constants.REQUEST_PARAM_NAME);
    }

    private boolean isParamOnly(Set<ParamModel> paramModels) {

        boolean hasOneParam = false;
        boolean hasSecondParam = false;
        boolean hasBody = false;
        for (ParamModel paramModel : paramModels) {
            if (paramModel.getAnnotationType().isFromParam()) {
                if (!hasOneParam) hasOneParam = true;
                else hasSecondParam = true;
            }
            if (paramModel.getAnnotationType() == AnnotationType.BODY) hasBody = true;
        }
        return paramModels.isEmpty() || hasBody || !hasSecondParam;
    }

}
