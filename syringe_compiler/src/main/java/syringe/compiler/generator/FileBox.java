package syringe.compiler.generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
/**
 * Created by Fred Zhao on 2017/3/1.
 */

public abstract class FileBox {


    private String packageName;

    private ClassName mClassName;

    public FileBox(String packageName) {

        this.packageName = packageName;
    }

    public JavaFile preJavaFile() {

        return JavaFile.builder(this.packageName, getTypeSpec())
                .addFileComment("Generated code Do not modify!!!")
                .build();
    }

    public ClassName getClassName() {

        return mClassName;
    }

    public void setClassName(ClassName className) {
        mClassName = className;
    }

    public abstract TypeSpec getTypeSpec();

}
