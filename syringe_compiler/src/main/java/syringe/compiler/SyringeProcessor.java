package syringe.compiler;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import syringe.BindAttr;
import syringe.HttpHolderConfig;
import syringe.Service;
import syringe.compiler.entity.AttrActionModel;
import syringe.compiler.entity.AttrModel;
import syringe.compiler.entity.ConfigClassModel;
import syringe.compiler.entity.MethodModel;
import syringe.compiler.entity.ParamModel;
import syringe.compiler.entity.ServiceModel;
import syringe.compiler.generator.AttrActionGenerator;
import syringe.compiler.generator.AttrIdGenerator;
import syringe.compiler.generator.AttrParseGenerator;
import syringe.compiler.generator.ConfigGenerator;
import syringe.compiler.generator.CopyGenerator;
import syringe.compiler.generator.FileBox;
import syringe.compiler.generator.MethodEnumTypeGenerator;
import syringe.compiler.generator.RequestParamBuilderGenerator;
import syringe.compiler.generator.RequestParamGenerator;
import syringe.compiler.generator.ServiceManagerGenerator;
import syringe.compiler.generator.ServiceTypeGenerator;

/**
 * Created by Fred Zhao on 2017/3/1.
 */
@AutoService(Processor.class)
public class SyringeProcessor extends AbstractProcessor {

    private Filer filer;

    private Class mapClz = Map.class;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {

        Set<String> types = new LinkedHashSet<>();
        types.add(Service.class.getCanonicalName());
        types.add(HttpHolderConfig.class.getCanonicalName());
        types.add(BindAttr.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {

        return SourceVersion.RELEASE_7;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (null != annotations && !annotations.isEmpty()) {
            parseEnvironment(roundEnv);
        }
        return true;
    }


    private void parseEnvironment(RoundEnvironment roundEnv) {

        Set<FileBox> fileMap = new LinkedHashSet<>();
        ConfigClassModel configClassModel = parseConfig(roundEnv, fileMap);
        parseService(roundEnv, fileMap, configClassModel);
        parseAttrs(roundEnv, fileMap);
        writeFile(fileMap);
    }


    private void parseAttrs(RoundEnvironment roundEnv, Set<FileBox> fileMap) {

        Set<? extends Element> attrEle = roundEnv.getElementsAnnotatedWith(BindAttr.class);
        Set<AttrModel> attrModels = new HashSet<>();
        for (Element element : attrEle) {
            attrModels.add(new AttrModel(element));
        }
        clearAttrs(attrModels);
        if (attrModels.isEmpty()) return;
        Set<AttrActionModel> attrActionModels = AttrActionModel.getModels(attrModels);
        if (attrActionModels.isEmpty()) return;

        fileMap.add(new AttrIdGenerator(attrActionModels));
        fileMap.add(new AttrActionGenerator(attrActionModels));
        fileMap.add(new AttrParseGenerator(attrActionModels));

    }

    private void clearAttrs(Set<AttrModel> attrs) {

        Set<AttrModel> delAttrs = new LinkedHashSet<>();
        Set<String> methodNames = new LinkedHashSet<>();
        for (AttrModel attrModel : attrs) {
            if (!attrModel.isNotNull()) delAttrs.add(attrModel);
            if (!methodNames.add(attrModel.getMethodName())) delAttrs.add(attrModel);
        }
        attrs.removeAll(delAttrs);
    }


    private ConfigClassModel parseConfig(RoundEnvironment roundEnv, Set<FileBox> fileMap) {

        Set<? extends Element> configElements = roundEnv.getElementsAnnotatedWith(HttpHolderConfig.class);
        checkConfig(configElements);
        if (configElements.size() == 1) {
            ConfigClassModel configClassModel = new ConfigClassModel(configElements.iterator().next());
            if (configClassModel.isConfigElementAdopt()) {
                fileMap.add(new ConfigGenerator(configClassModel));
                return configClassModel;
            }
        }
        return null;
    }

    private void parseService(RoundEnvironment roundEnv, Set<FileBox> fileMap, ConfigClassModel configClassModel) {

        Set<ServiceModel> serviceModels = new LinkedHashSet<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(Service.class)) {
            serviceModels.add(new ServiceModel(element));
        }
        clearService(serviceModels);
        if (!serviceModels.isEmpty()) {
            String packageName = serviceModels.iterator().next().getServicePackageName();
            fileMap.add(new ServiceManagerGenerator(packageName, serviceModels));
            fileMap.add(new MethodEnumTypeGenerator(packageName, serviceModels));

            for (ServiceModel serviceModel : serviceModels) {
                fileMap.add(new ServiceTypeGenerator(serviceModel));
                fileMap.add(new CopyGenerator(packageName, serviceModel));
            }
            fileMap.add(new RequestParamBuilderGenerator(packageName, serviceModels, configClassModel));
            fileMap.add(new RequestParamGenerator(packageName, serviceModels));
            /* ps:
            * RequestParam依赖RequestParamBuilder
            * RequestParamBuilder 依赖 MethodType、ServiceType、Copy
            * */
        }
    }

    private void checkConfig(Set<? extends Element> configElements) {

        if (null != configElements) {
            if (configElements.isEmpty()) printCheck(false, null, false, "you can set a default HolderConfig");
            else printCheck(false, null, configElements.size() == 1, "you can set only one default HolderConfig");
            for (Element element : configElements) {
                printCheck(true, element, Preconditions.checkClass(element), "%s must be a class", element
                        .getSimpleName().toString());
                printCheck(true, element, Preconditions.checkConfig((TypeElement) element), "%s must extends " +
                        "BaseHttpHolderConfiguration", element.getSimpleName().toString());
            }
        }
    }

    private void printCheck(boolean isMandatory, Element element, boolean checkResult, String message, Object...
            args) {

        if (!checkResult) {
            if (isMandatory) error(element, message, args);
            else warning(element, message, args);
        }
    }

    private void checkService(ServiceModel serviceModel) {

        printCheck(false, serviceModel.getElement(), Preconditions.checkInterface(serviceModel.getElement()), "the " +
                "service need to be an interface");
        printCheck(false, serviceModel.getElement(), !serviceModel.getMethodModels().isEmpty(), "the service need at " +
                "least one method return Observable");
    }

    private void clearService(Set<ServiceModel> serviceModels) {

        Set<ServiceModel> delModel = new LinkedHashSet<>();
        for (ServiceModel serviceModel : serviceModels) {
            clearMethod(serviceModel.getMethodModels());
            checkService(serviceModel);
            if (null == serviceModel.getServicePackageName() || null == serviceModel.getServiceClassName()
                    || serviceModel.getMethodModels().isEmpty()) {
                delModel.add(serviceModel);
            }
        }
        serviceModels.removeAll(delModel);
    }

    private void clearMethod(Set<MethodModel> methodModels) {

        Set<MethodModel> delModel = new LinkedHashSet<>();
        for (MethodModel methodModel : methodModels) {
            if (!methodModel.isMethodAdopt()) delModel.add(methodModel);
            else checkMethod(methodModel);
        }
        methodModels.removeAll(delModel);
    }

    private void checkParams(ParamModel paramModel) {

        checkAnnotation(paramModel);
    }

    private void checkAnnotation(final ParamModel paramModel) {
        // TODO: 2017/3/14 check map param
        /*try {
            Class clz= Class.forName(paramModel.getParamType().toString());
            clz.isAssignableFrom(Map.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/

        /*
        * Class<?> rawParameterType = Utils.getRawType(type);
        * if (!Map.class.isAssignableFrom(rawParameterType)) {
        *   throw parameterError(p, "@QueryMap parameter type must be Map.");
        * }
        * Type mapType = Utils.getSupertype(type, rawParameterType, Map.class);
        * if (!(mapType instanceof ParameterizedType)) {
        *   throw parameterError(p, "Map must include generic types (e.g., Map<String, String>)");
        * }
        * ParameterizedType parameterizedType = (ParameterizedType) mapType;
        * Type keyType = Utils.getParameterUpperBound(0, parameterizedType);
        * if (String.class != keyType) {
        *   throw parameterError(p, "@QueryMap keys must be of type String: " + keyType);
        * }
        * */
        printCheck(true, paramModel.getElement(), paramModel.getAnnotations().size() == 1, "you should add only one " +
                "annotation on this parameter");
    }

    private void checkMethod(MethodModel methodModel) {

        printCheck(true, methodModel.getElement(), methodModel.isMethodAdopt(), "the api method %s must return an " +
                "Observable type", methodModel.getElement().getSimpleName().toString());
        for (ParamModel paramModel : methodModel.getParamModels()) {
            checkParams(paramModel);
        }
        checkMethodParamAnnotation(methodModel);
    }

    private void checkMethodParamAnnotation(MethodModel methodModel) {

        List<ParamModel> paramModels = new ArrayList<>(methodModel.getParamModels());
        int size = paramModels.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                AnnotationType one = paramModels.get(i).getAnnotationType();
                AnnotationType two = paramModels.get(j).getAnnotationType();
                if (AnnotationType.isMutex(one, two)) {
                    Element errorEle = paramModels.get(j).getElement();
                    error(errorEle, String.format(AnnotationType.mutexMessage(one, two), errorEle.getSimpleName()
                            .toString()));
                }
                if (one == AnnotationType.PART && two == AnnotationType.PART && (null == paramModels.get(i)
                        .getAnnotationValue() || null == paramModels.get(j).getAnnotationValue())) {
                    error(paramModels.get(j).getElement(), "your Part param is not similar,please add annotation " +
                            "value");
                }
            }
        }
    }

    private void writeFile(Set<FileBox> fileBoxes) {

        for (FileBox box : fileBoxes) {
            try {
                box.preJavaFile().writeTo(filer);
            } catch (IOException e) {
                error(null, e.getMessage());
            }
        }
    }

    private void error(Element element, String message, Object... args) {

        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {

        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void warning(Element element, String message, Object... args) {

        printMessage(Diagnostic.Kind.WARNING, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {

        if (args.length > 0) {
            message = String.format(message, args);
        }
        if (null == element) {
            processingEnv.getMessager().printMessage(kind, message);
        } else {
            processingEnv.getMessager().printMessage(kind, message, element);
        }
    }
}
