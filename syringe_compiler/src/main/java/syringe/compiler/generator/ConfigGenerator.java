package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import syringe.compiler.Constants;
import syringe.compiler.entity.ConfigClassModel;
/**
 * Created by Fred Zhao on 2017/3/6.
 */

public class ConfigGenerator extends FileBox {


    private ConfigClassModel configClassModel;

    private final String _CODE_Method_responseFormat = "configDefaultHttpResponse";

    private final String _CODE_Method_requestFormat = "configDefaultHttpRequest";

    private final String _CODE_Method_subscriber = "configDefaultSubscriber";


    public ConfigGenerator(ConfigClassModel configClassModel) {

        super(configClassModel.getConfigPackageName());
        this.configClassModel = configClassModel;
        setClassName(configConfigClassName(configClassModel));
    }

    @Override
    public TypeSpec getTypeSpec() {

        TypeSpec.Builder result = TypeSpec.classBuilder(getClassName()).addModifiers(Modifier.PUBLIC).superclass
                (ClassName.bestGuess(configClassModel.getConfigPackageName().concat(".").concat(configClassModel
                        .getConfigClassName())));
        result.addField(RequestParamBuilderGenerator.responseFormatClass, Constants.BASE_FIELD_RESPONSE_FORMAT, Modifier
                .PUBLIC);
        result.addField(RequestParamBuilderGenerator.requestFormatClass, Constants.BASE_FIELD_REQUEST_FORMAT,
                Modifier.PUBLIC);
        result.addField(RequestParamBuilderGenerator.subscriberClass, Constants.BASE_FIELD_SUBSCRIBER, Modifier.PUBLIC);
        result.addField(getClassName(), "instance", Modifier.PRIVATE, Modifier.STATIC);
        MethodSpec.Builder instanceMethod = MethodSpec.methodBuilder("getInstance").addModifiers(Modifier.PUBLIC,
                Modifier.STATIC, Modifier.SYNCHRONIZED).returns(getClassName());
        instanceMethod.addStatement("if (null == instance) instance = new $T()", getClassName()).addStatement
                ("return instance");
        result.addMethod(instanceMethod.build());
        MethodSpec.Builder constructMethod = MethodSpec.constructorBuilder()
                .addStatement("this.$N=$N()", Constants.BASE_FIELD_RESPONSE_FORMAT, _CODE_Method_responseFormat)
                .addStatement("this.$N=$N()", Constants.BASE_FIELD_REQUEST_FORMAT, _CODE_Method_requestFormat)
                .addStatement("this.$N=$N()", Constants.BASE_FIELD_SUBSCRIBER, _CODE_Method_subscriber);
        result.addMethod(constructMethod.build());
        return result.build();
    }

    public static ClassName configConfigClassName(ConfigClassModel configClassModel) {

        return ClassName.get(configClassModel.getConfigPackageName(), configClassModel.getConfigClassName()
                .concat(Constants.CONFIG_HOLDER_SUFFIX));
    }
}
