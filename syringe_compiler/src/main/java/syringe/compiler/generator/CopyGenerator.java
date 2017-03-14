package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import syringe.compiler.Constants;
import syringe.compiler.entity.MethodModel;
import syringe.compiler.entity.ServiceModel;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
/**
 * Created by Fred Zhao on 2017/3/1.
 */

public class CopyGenerator extends FileBox {


    private ServiceModel mServiceModel;

    private final String _CODE_FIELD_mBuilder = "mBuilder";

    private final String _CODE_PARAM_builder = "builder";

    public CopyGenerator(String builderPkg, ServiceModel serviceModel) {

        super(builderPkg);
        this.mServiceModel = serviceModel;
        setClassName(configCopyClassName(builderPkg, serviceModel.getServiceClassName().concat(Constants.pkgSuffix
                (serviceModel.getServicePackageName()))));
    }


    @Override
    public TypeSpec getTypeSpec() {

        TypeSpec.Builder result = TypeSpec.classBuilder(getClassName())
                .addModifiers(PUBLIC);
        result.addField(ClassName.bestGuess(getClassName().packageName().concat(".").concat(Constants
                        .REQUEST_PARAM_BUILDER_NAME)),
                _CODE_FIELD_mBuilder, PRIVATE);
        result.addMethod(addConstructorMethod());
        for (MethodModel method : mServiceModel.getMethodModels()) {
            result.addMethod(addMethod(mServiceModel, method));
        }
        return result.build();
    }

    private MethodSpec addMethod(ServiceModel serviceModel, MethodModel method) {

        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(method.getMethodName()).addModifiers(Modifier.PUBLIC)
                .addStatement("this.$N.method($T.$N)", _CODE_FIELD_mBuilder, MethodEnumTypeGenerator
                        .configMethodTypeClassName(getClassName().packageName()), Constants.methodTypeConfig
                        (serviceModel
                                .getServicePackageName(), serviceModel.getServiceClassName(), method.getMethodName()))
                .addStatement("return $N", _CODE_FIELD_mBuilder).returns
                        (ClassName.bestGuess(getClassName().packageName().concat(".").concat(Constants
                                .REQUEST_PARAM_BUILDER_NAME)));
        return methodSpec.build();
    }

    public MethodSpec addConstructorMethod() {

        MethodSpec.Builder constructorMethodSpec = MethodSpec.constructorBuilder().addModifiers(PUBLIC).addParameter
                (RequestParamBuilderGenerator.configBuilderClassName(getClassName().packageName()),
                        _CODE_PARAM_builder).addStatement
                ("this.$N=$N", _CODE_FIELD_mBuilder, _CODE_PARAM_builder);
        return constructorMethodSpec.build();
    }

    public static ClassName configCopyClassName(String packageName, String className) {

        return ClassName.get(packageName, className.concat
                (Constants.SERVICE_COPY_SUFFIX));
    }
}
