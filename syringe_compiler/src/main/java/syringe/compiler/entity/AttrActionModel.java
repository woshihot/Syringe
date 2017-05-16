package syringe.compiler.entity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import syringe.compiler.Constants;

import static syringe.compiler.Constants.upFirstLetter;
/**
 * Created by Fred Zhao on 2017/5/8.
 */

public class AttrActionModel {

    private AttrModel mAttrModel;

    private int tag;

    private String actionName;

    public AttrActionModel(AttrModel attrModel, int tag, String actionName) {

        mAttrModel = attrModel;
        this.tag = tag;
        this.actionName = actionName;
    }

    public static Set<AttrActionModel> getModels(Set<AttrModel> attrModels) {

        Set<AttrActionModel> attrActionModels = new LinkedHashSet<>();
        List<AttrModel> attrModelList = new ArrayList<>(attrModels);
        HashSet<String> actionName = new LinkedHashSet<>();
        for (AttrModel attrModel : attrModels) {
            String field = attrModel.getTagName();
            String method = attrModel.getMethodName();
            if (actionName.add(Constants.getTrulyFieldName(field))) ;
            else if (actionName.add(field)) ;
            else if (actionName.add(method)) ;
            else if (field.startsWith("set") && field.length() > 3) {
                actionName.add(attrModel.getTagName().substring(3));
            } else actionName.add("Set".concat(field));
        }
        List<String> actionNames = new ArrayList<>(actionName);
        for (int i = 0; i < attrModelList.size(); i++) {
            if (actionNames.size() > i)
                attrActionModels.add(new AttrActionModel(attrModelList.get(i), i + 1, upFirstLetter(actionNames.get
                        (i)).concat("Action")));
        }
        return attrActionModels;
    }

    public AttrModel getAttrModel() {

        return mAttrModel;
    }

    public int getTag() {

        return tag;
    }

    public String getActionName() {

        return actionName;
    }
}
