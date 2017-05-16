package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.Set;

import javax.lang.model.element.Modifier;

import syringe.compiler.Constants;
import syringe.compiler.entity.AttrActionModel;

import static javax.lang.model.element.Modifier.PUBLIC;
import static syringe.compiler.Constants.REBIND_ACTION_NAME;
/**
 * Created by Fred Zhao on 2017/5/8.
 */

public class AttrActionGenerator extends FileBox {


    private Set<AttrActionModel> mAttrModels;

    private static String _CODE_Param_builder = "builder";

    private static String _CODE_Param_o = "o";

    public AttrActionGenerator(Set<AttrActionModel> attrModels) {

        super(Constants.SYRINGE_PACKAGE);
        mAttrModels = attrModels;
        setClassName(ClassName.get(Constants.SYRINGE_PACKAGE, Constants.ATTR_ACTIONS_MAP_NAME));
    }

    @Override
    public TypeSpec getTypeSpec() {

        TypeSpec.Builder result = TypeSpec.classBuilder(getClassName()).addModifiers(Modifier.PUBLIC);
        for (AttrActionModel attrActionModel : mAttrModels) {
            result.addType(TypeSpec.classBuilder(attrActionModel.getActionName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .superclass(TypeVariableName.get(REBIND_ACTION_NAME.concat(Constants.addAngleBracket
                            (attrActionModel.getAttrModel().getBuilderName()))))
                    .addMethod(MethodSpec.constructorBuilder().addModifiers(PUBLIC)
                            .addParameter(ParameterSpec.builder(TypeVariableName.get(attrActionModel.getAttrModel()
                                    .getBuilderName()), _CODE_Param_builder).build())
                            .addStatement("super($N)", _CODE_Param_builder)
                            .build())
                    .addMethod(MethodSpec.methodBuilder("call")
                            .addModifiers(PUBLIC)
                            .addParameter(TypeName.OBJECT, _CODE_Param_o)
                            .addAnnotation(Override.class)
                            .returns(TypeName.VOID)
                            .addStatement("mBuilder.$N(($T) $N)", attrActionModel.getAttrModel().getMethodName(),
                                    ClassName.bestGuess(attrActionModel.getAttrModel().getFieldTypeName()),
                                    _CODE_Param_o)
                            .build())
                    .build());
        }
        return result.build();
    }
}
