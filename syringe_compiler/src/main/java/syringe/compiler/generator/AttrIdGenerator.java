package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Set;

import javax.lang.model.element.Modifier;

import syringe.compiler.Constants;
import syringe.compiler.entity.AttrActionModel;

import static syringe.compiler.Constants.BA_NAME;
/**
 * Created by Fred Zhao on 2017/5/5.
 */

public class AttrIdGenerator extends FileBox {

    private Set<AttrActionModel> mAttrModels;

    public AttrIdGenerator(Set<AttrActionModel> attrModels) {

        super(Constants.SYRINGE_PACKAGE);
        this.mAttrModels = attrModels;
        setClassName(ClassName.get(Constants.SYRINGE_PACKAGE, BA_NAME));
    }

    @Override
    public TypeSpec getTypeSpec() {

        TypeSpec.Builder result = TypeSpec.classBuilder(getClassName()).addModifiers(Modifier.PUBLIC);
        for (AttrActionModel attrActionModel : mAttrModels)
            result.addField(FieldSpec.builder(TypeName.INT, attrActionModel.getAttrModel().getTagName(), Modifier
                    .PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer(CodeBlock.of(String.valueOf(attrActionModel
                    .getTag()))).build());


        return result.build();
    }


}
