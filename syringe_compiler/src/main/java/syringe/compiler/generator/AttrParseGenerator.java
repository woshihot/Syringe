package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.Set;

import javax.lang.model.element.Modifier;

import syringe.compiler.Constants;
import syringe.compiler.entity.AttrActionModel;

import static syringe.compiler.Constants.ATTR_ACTIONS_MAP_NAME;
import static syringe.compiler.Constants.ATTR_BIND_PARSE_NAME;
import static syringe.compiler.Constants.BASE_POST_BUILDER_PATH;
import static syringe.compiler.Constants.BA_NAME;
import static syringe.compiler.Constants.SYRINGE_PACKAGE;
import static syringe.compiler.Constants.UNBIND_ATTR_ACTION_NAME;
/**
 * Created by Fred Zhao on 2017/5/8.
 */

public class AttrParseGenerator extends FileBox {

    private Set<AttrActionModel> mAttrActionModels;

    private static final String _CODE_PARAM_tag = "tag";

    private static final String _CODE_PARAM_builder = "builder";

    public AttrParseGenerator(Set<AttrActionModel> attrActionModels) {

        super(Constants.SYRINGE_PACKAGE);
        setClassName(ClassName.get(Constants.SYRINGE_PACKAGE, Constants.ATTR_ACTION_PARSE_NAME));
        mAttrActionModels = attrActionModels;
    }

    @Override
    public TypeSpec getTypeSpec() {

        TypeSpec.Builder result = TypeSpec.classBuilder(getClassName()).addModifiers(Modifier.PUBLIC);
        result.addSuperinterface(TypeVariableName.get(ATTR_BIND_PARSE_NAME));
        result.addType(TypeSpec.classBuilder(UNBIND_ATTR_ACTION_NAME)
                .superclass(TypeVariableName.get(Constants.REBIND_ACTION_NAME))
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(TypeVariableName.get(BASE_POST_BUILDER_PATH), _CODE_PARAM_builder)
                        .addStatement("super($N)", _CODE_PARAM_builder)
                        .build())
                .addMethod(MethodSpec.methodBuilder("call")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.OBJECT, "o")
                        .returns(TypeName.VOID)
                        .addAnnotation(Override.class)
                        .build())
                .build());
        result.addMethod(MethodSpec.methodBuilder("parseAction")
                .returns(TypeVariableName.get(Constants.REBIND_ACTION_NAME))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.INT, _CODE_PARAM_tag)
                .addParameter(TypeVariableName.get(BASE_POST_BUILDER_PATH), _CODE_PARAM_builder)
                .addCode(parseCode().build())
                .build());

        return result.build();
    }

    private CodeBlock.Builder parseCode() {

        CodeBlock.Builder code = CodeBlock.builder()
                .beginControlFlow("switch($N)", _CODE_PARAM_tag);
        for (AttrActionModel attrActionModel : mAttrActionModels) {
            code.add("case ".concat("$T.$N:\n"), ClassName.bestGuess(Constants.SYRINGE_PACKAGE.concat(".").concat
                    (BA_NAME)), attrActionModel.getAttrModel().getTagName());
            code.addStatement("return new ".concat("$T(($T)$N)"), ClassName.bestGuess(SYRINGE_PACKAGE.concat(".").concat
                            (ATTR_ACTIONS_MAP_NAME).concat(".").concat(attrActionModel.getActionName())),
                    ClassName.bestGuess(attrActionModel.getAttrModel().getBuilderName()),
                    _CODE_PARAM_builder);
        }
        code.add("default :\n");
        code.addStatement("return new $N($N)", UNBIND_ATTR_ACTION_NAME, _CODE_PARAM_builder);
        return code.endControlFlow();
    }
}
