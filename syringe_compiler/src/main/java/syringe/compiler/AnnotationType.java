package syringe.compiler;
import static syringe.compiler.Constants.BODY_ANNOTATION;
import static syringe.compiler.Constants.FIELD_ANNOTATION;
import static syringe.compiler.Constants.FIELD_MAP_ANNOTATION;
import static syringe.compiler.Constants.HEADER_ANNOTATION;
import static syringe.compiler.Constants.HEADER_MAP_ANNOTATION;
import static syringe.compiler.Constants.PART_ANNOTATION;
import static syringe.compiler.Constants.PART_MAP_ANNOTATION;
import static syringe.compiler.Constants.PATH_ANNOTATION;
import static syringe.compiler.Constants.QUERY_ANNOTATION;
import static syringe.compiler.Constants.QUERY_MAP_ANNOTATION;
import static syringe.compiler.Constants.URL_ANNOTATION;
/**
 * Created by Fred Zhao on 2017/3/10.
 */

public enum AnnotationType {
    BODY(BODY_ANNOTATION, true),
    URL(URL_ANNOTATION, false),
    PART(PART_ANNOTATION, true),
    PART_MAP(PART_MAP_ANNOTATION, true),
    QUERY(QUERY_ANNOTATION, true),
    QUERY_MAP(QUERY_MAP_ANNOTATION, true),
    FIELD(FIELD_ANNOTATION, true),
    FIELD_MAP(FIELD_MAP_ANNOTATION, true),
    HEADER(HEADER_ANNOTATION, false),
    HEADER_MAP(HEADER_MAP_ANNOTATION, false),
    PATH(PATH_ANNOTATION, false);

    private String annotationName;

    private boolean isFromParam;

    AnnotationType(String annotationName, boolean isFromParam) {

        this.annotationName = annotationName;
        this.isFromParam = isFromParam;
    }

    public static AnnotationType getTypeByName(String annotationName) {

        switch (annotationName) {
            case Constants.BODY_ANNOTATION:
                return BODY;
            case Constants.URL_ANNOTATION:
                return URL;
            case Constants.PART_ANNOTATION:
                return PART;
            case Constants.PART_MAP_ANNOTATION:
                return PART_MAP;
            case Constants.QUERY_ANNOTATION:
                return QUERY;
            case Constants.QUERY_MAP_ANNOTATION:
                return QUERY_MAP;
            case Constants.FIELD_ANNOTATION:
                return FIELD;
            case Constants.FIELD_MAP_ANNOTATION:
                return FIELD_MAP;
            case Constants.HEADER_ANNOTATION:
                return HEADER;
            case Constants.HEADER_MAP_ANNOTATION:
                return HEADER_MAP;
            case Constants.PATH_ANNOTATION:
                return PATH;
            default:
                return null;
        }
    }

    public boolean isFromParam() {

        return isFromParam;
    }

    public static boolean isMutex(AnnotationType one, AnnotationType two) {

        switch (one) {
            case BODY:
                return two == FIELD || two == FIELD_MAP || two == QUERY || two == QUERY_MAP || two == PART || two ==
                        PART_MAP || two == BODY;
            case PART:
                return two == PART_MAP || two == BODY;
            case PART_MAP:
                return two == PART || two == BODY || two == QUERY_MAP || two == FIELD_MAP || two == PART_MAP;
            case QUERY:
                return two == QUERY_MAP || two == BODY;
            case QUERY_MAP:
                return two == QUERY || two == BODY || two == FIELD_MAP || two == PART_MAP || two == QUERY_MAP;
            case FIELD:
                return two == FIELD_MAP || two == BODY;
            case FIELD_MAP:
                return two == FIELD || two == BODY || two == FIELD_MAP || two == PART_MAP || two == QUERY_MAP;
            case HEADER:
                return two == HEADER_MAP;
            case HEADER_MAP:
                return two == HEADER_MAP || two == HEADER;
            case URL:
                return two == URL;
            case PATH:
            default:
                return false;
        }
    }

    public static String mutexMessage(AnnotationType one, AnnotationType two) {

        switch (one) {
            case BODY:
                return bodyMutex(two);
            case PART:
                return partMutex(two);
            case PART_MAP:
                return partMapMutex(two);
            case QUERY:
                return queryMutex(two);
            case QUERY_MAP:
                return queryMapMutex(two);
            case FIELD:
                return fieldMutex(two);
            case FIELD_MAP:
                return fieldMapMutex(two);
            case HEADER:
                return headerMutex(two);
            case HEADER_MAP:
                return headerMapMutex(two);
            case URL:
                return urlMutex(two);
            case PATH:
            default:
                return "";
        }
    }

    private static String bodyMutex(AnnotationType two) {

        switch (two) {
            case BODY:
                return "you should at most one Body param,this param %s can be merged";
            case PART:
                return BODY_ONLY;
            case PART_MAP:
                return BODY_ONLY;
            case QUERY:
                return BODY_ONLY;
            case QUERY_MAP:
                return BODY_ONLY;
            case FIELD:
                return BODY_ONLY;
            case FIELD_MAP:
                return BODY_ONLY;
            default:
                return "";
        }
    }

    private static String partMutex(AnnotationType two) {

        switch (two) {
            case BODY:
                return PART_MERGE;
            case PART_MAP:
                return PART_MERGE;
            default:
                return "";
        }
    }

    private static String partMapMutex(AnnotationType two) {

        switch (two) {
            case BODY:
                return PART_MAP_MERGE;
            case PART:
                return "you have a param annotation with PartMap,this param %s can be merged in";
            case PART_MAP:
                return MAP_ONLY;
            case QUERY_MAP:
                return MAP_ONLY;
            case FIELD_MAP:
                return MAP_ONLY;
            default:
                return "";
        }
    }

    private static String queryMapMutex(AnnotationType two) {

        switch (two) {
            case BODY:
                return QUERY_MAP_MERGE;
            case QUERY:
                return "you have a param annotation with QueryMap,this param %s can be merged in";
            case PART_MAP:
                return MAP_ONLY;
            case QUERY_MAP:
                return MAP_ONLY;
            case FIELD_MAP:
                return MAP_ONLY;
            default:
                return "";
        }
    }

    private static String queryMutex(AnnotationType two) {

        switch (two) {
            case BODY:
                return QUERY_MERGE;
            case QUERY_MAP:
                return QUERY_MERGE;
            default:
                return "";
        }
    }

    private static String fieldMutex(AnnotationType two) {

        switch (two) {
            case BODY:
                return FIELD_MERGE;
            case PART_MAP:
                return FIELD_MERGE;
            default:
                return "";
        }
    }

    private static String fieldMapMutex(AnnotationType two) {

        switch (two) {
            case BODY:
                return FIELD_MAP_MERGE;
            case FIELD:
                return "you have a param annotation with FieldMap,this param %s can be merged in";
            case PART_MAP:
                return MAP_ONLY;
            case QUERY_MAP:
                return MAP_ONLY;
            case FIELD_MAP:
                return MAP_ONLY;
            default:
                return "";
        }
    }

    private static String headerMutex(AnnotationType two) {

        switch (two) {
            case HEADER_MAP:
                return "you have a param annotate with Header you can merge it in this param %s";
            default:
                return "";
        }
    }

    private static String headerMapMutex(AnnotationType two) {

        switch (two) {
            case HEADER_MAP:
                return HEADER_MAP_ONLY;
            case HEADER:
                return "you have a param annotate with HeaderMap ,this param %s can be merged";
            default:
                return "";
        }
    }

    private static String urlMutex(AnnotationType two) {

        switch (two) {
            case URL:
                return "you should at most one Url param,this param %s is repeated";
            default:
                return "";
        }
    }

    private static final String BODY_ONLY = "you have a param annotate with Body,the param %s can not annotate with " +
            "the similar kind annotation.";

    private static final String PART_MERGE = "you have a param annotation with Part,you can merge it in this param %s";

    private static final String QUERY_MERGE = "you have a param annotation with Query,you can merge it in this param " +
            "%s";

    private static final String FIELD_MERGE = "you have a param annotation with Field,you can merge it in this param " +
            "%s";


    private static final String PART_MAP_MERGE = "you have a param annotation with PartMap,you can merge it in this " +
            "param %s";

    private static final String QUERY_MAP_MERGE = "you have a param annotation with QueryMap,you can merge it in this" +
            " " +
            "param %s";

    private static final String FIELD_MAP_MERGE = "you have a param annotation with FieldMap,you can merge it in this" +
            " " +
            "param %s";

    private static final String MAP_ONLY = "you should at most one Map(parameter type) param,this param %s can be " +
            "split";

    private static final String HEADER_MAP_ONLY = "you should at most one HeaderMap param,this param %s can be merged";


}
