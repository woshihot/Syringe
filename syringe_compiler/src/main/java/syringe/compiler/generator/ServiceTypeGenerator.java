package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import syringe.compiler.Constants;
import syringe.compiler.entity.ServiceModel;

import static javax.lang.model.element.Modifier.PUBLIC;
/**
 * Created by Fred Zhao on 2017/3/1.
 */

public class ServiceTypeGenerator extends FileBox {


    public ServiceTypeGenerator(ServiceModel serviceModel) {

        super(serviceModel.getServicePackageName());
        setClassName(configTypeClassName(serviceModel.getServicePackageName(), serviceModel.getServiceClassName()));
    }

    public TypeSpec getTypeSpec() {

        TypeSpec.Builder result = TypeSpec.enumBuilder(getClassName())
                .addModifiers(PUBLIC);
        result.addEnumConstant("CLASS");
        return result.build();
    }

    public static ClassName configTypeClassName(String packageName, String className) {

        return ClassName.get(packageName, className.concat
                (Constants.SERVICE_TYPE_SUFFIX));
    }
}
