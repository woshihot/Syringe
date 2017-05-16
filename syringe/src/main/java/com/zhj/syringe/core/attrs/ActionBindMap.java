package com.zhj.syringe.core.attrs;
import com.zhj.syringe.core.BaseHttpHolder;
/**
 * Created by Fred Zhao on 2017/5/5.
 */

public class ActionBindMap {

    private static ActionMapParse sActionMapParse;

    private static final String MAP_PARSE_PATH = "com.zhj.syringe.AttrActionParse";

    public static RebindAttrAction getAction(int tag, BaseHttpHolder.BasePostBuilder builder) {

        createParse();
        if (null == sActionMapParse) return null;
        return sActionMapParse.parseAction(tag, builder);
    }

    private static void createParse() {

        if (null == sActionMapParse) {
            try {
                sActionMapParse = (ActionMapParse) Class.forName(MAP_PARSE_PATH).newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            }
        }
    }
}
