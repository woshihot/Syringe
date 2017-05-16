package syringe.compiler;
import java.util.regex.Pattern;
/**
 * Created by Fred Zhao on 2017/3/1.
 */

public class Constants {

    public static final String SERVICE_TYPE_SUFFIX = "Type";

    public static final String SERVICE_COPY_SUFFIX = "$$Copy";

    public static final String CONFIG_HOLDER_SUFFIX = "$$ConfigHolder";

    public static final String DEFAULTPOST_ANNOTATION = "DefaultPost";

    public static final String METHOD_ENUM_CLASS_NAME = "MethodType";

    public static final String SERVICE_MANAGER_CLASS_NAME = "ServiceManager";

    public static final String SYRINGE_PACKAGE = "com.zhj.syringe";

    public static final String BASE_CONFIG_PATH = SYRINGE_PACKAGE.concat(".core.BaseHttpHolderConfiguration");

    public static final String BASE_CONFIG_HOLDER_PATH = SYRINGE_PACKAGE.concat(".core.DefaultConfigHolder");

    public static final String BASE_POST_BUILDER_PATH = SYRINGE_PACKAGE.concat(".core.BaseHttpHolder.BasePostBuilder");

    public static String methodTypeConfig(String packageName, String className, String methodName) {

        return packageName.concat("_").concat(className).concat("_").concat(methodName).replace(".", "_")
                .toUpperCase();
    }

    public static String methodObservableConfig(String packageName, String className, String methodName) {

        return pkgSuffix(packageName).concat(className).concat(upFirst(methodName));
    }

    private static String upFirst(String str) {

        return str.substring(0, 1).toUpperCase().concat(str.substring(1, str.length()));
    }

    public static String pkgSuffix(String pkg) {

        StringBuilder sb = new StringBuilder();
        for (String str : pkg.split("\\.")) {
            sb.append(upCaseFirst(str));
        }
        return sb.toString();
    }

    private static String upCaseFirst(String str) {

        char[] strAry = str.toCharArray();
        if (Character.isLowerCase(strAry[ 0 ])) strAry[ 0 ] -= 32;
        return String.valueOf(strAry);
    }

    public static String serviceNameConfig(String packageName, String className) {

        return packageName.replace(".", "_").concat("_").concat(className);
    }

    public static String addBrackets(String content) {

        return "(".concat(content).concat(")");
    }

    public static String addAngleBracket(String content) {

        return "<".concat(content).concat(">");
    }


    public static final String SERVICE_MANAGER_PARENT_PACKAGE = SYRINGE_PACKAGE.concat(".core.service");

    public static final String SERVICE_MANAGER_PARENT_NAME = "BaseServiceManager";

    public static final String REQUEST_PARAM_NAME = "RequestParam";

    public static final String REQUEST_PARAM_BUILDER_NAME = "RequestParamBuilder";

    public static final String REQUEST_PARENT_PACKAGE = SYRINGE_PACKAGE.concat(".core.request");

    public static final String REQUEST_PARAM_PARENT_NAME = "BaseRequestParam";

    public static final String OBSERVABLE_CLASS_NAME = "rx.Observable";

    public static final String REQUEST_PARAM_BUILDER_PARENT_NAME = "BaseRequestParamBuilder";


    public static final String BASE_FIELD_REQUEST_FORMAT = "baseHttpRequestFormat";

    public static final String BASE_FIELD_RESPONSE_FORMAT = "baseHttpResponseFormat";

    public static final String BASE_FIELD_SUBSCRIBER = "baseHttpSubscriber";

    public static final String ATTR_ACTIONS_MAP_NAME = "AttrActionMap";

    public static final String ATTR_ACTION_PARSE_NAME="AttrActionParse";

    public static final String UNBIND_ATTR_ACTION_NAME="UnbindAction";

    public static final String ATTR_BIND_PARSE_NAME=SYRINGE_PACKAGE.concat(".core.attrs.ActionMapParse");

    public static final String REBIND_ACTION_NAME = SYRINGE_PACKAGE.concat(".core.attrs.RebindAttrAction");

    public static final String BA_NAME = "BA";

    public static final String BODY_ANNOTATION = "Body";

    public static final String URL_ANNOTATION = "Url";

    public static final String PART_ANNOTATION = "Part";

    public static final String PART_MAP_ANNOTATION = "PartMap";

    public static final String QUERY_ANNOTATION = "Query";

    public static final String QUERY_MAP_ANNOTATION = "QueryMap";

    public static final String FIELD_ANNOTATION = "Field";

    public static final String FIELD_MAP_ANNOTATION = "FieldMap";

    public static final String HEADER_ANNOTATION = "Header";

    public static final String HEADER_MAP_ANNOTATION = "HeaderMap";

    public static final String PATH_ANNOTATION = "Path";


    private static final String PATTERN_M = "m[A-Z]([A-Z_0-9a-z]+|)";

    public static String getTrulyFieldName(String fieldName) {

        if (isMField(fieldName)) {
            if (fieldName.length() > 1) {
                String f = fieldName.substring(1);
                return lowerFirstLetter(f);
            }
        }
        return fieldName;
    }

    public static String getTrulyMethodName(String methodName) {

        if (methodName.startsWith("set")) {
            if (methodName.length() > 3) {
                String m = methodName.substring(3);

                return lowerFirstLetter(m);
            }
        }
        return methodName;
    }

    public static boolean isMField(String fieldName) {

        return Pattern.compile(PATTERN_M).matcher(fieldName).matches();
    }

    public static String upFirstLetter(String str) {

        if (null != str && str.length() > 0) {
            String top = str.substring(0, 1).toUpperCase();
            return str.length() > 1 ? top.concat(str.substring(1)) : top;
        }
        return str;
    }


    public static String lowerFirstLetter(String str) {

        if (null != str && str.length() > 0) {
            String top = str.substring(0, 1).toLowerCase();
            return str.length() > 1 ? top.concat(str.substring(1)) : top;
        }
        return str;
    }
}