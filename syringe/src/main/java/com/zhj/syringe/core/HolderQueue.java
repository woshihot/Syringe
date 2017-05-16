package com.zhj.syringe.core;
import com.zhj.syringe.core.request.BaseRequestParam;
import com.zhj.syringe.core.request.ObservableFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
/**
 * Created by Fred Zhao on 2017/5/4.
 */

public class HolderQueue {

    private LinkedList<AvoidHolder> mAvoidHolders;

    public void addObservableFormat(ObservableFormat observableFormat) {

        createList();
        mAvoidHolders.getLast().mObservableFormat = observableFormat;
    }

    public void addRequestParam(BaseRequestParam baseRequestParam) {

        createList();
        mAvoidHolders.getLast().addRequestParam(baseRequestParam);
    }

    public void order(boolean isSerial) {

        createList();
        mAvoidHolders.getLast().isSerial = isSerial;
    }

    public void addAttrs(int tag, Object o) {

        createList();
        mAvoidHolders.getLast().addAttr(tag, o);
    }

    public void post() {

        createList();
        mAvoidHolders.getLast().post = true;
    }

    public boolean isDelay() {

        return mAvoidHolders != null && !mAvoidHolders.isEmpty();
    }

    public void push(BaseHttpHolder.BasePostBuilder builder) {

        if (!isDelay()) return;

        AvoidHolder avoidHolder = mAvoidHolders.getFirst();

        if (null != avoidHolder.mObservableFormat) builder.observableFormat(avoidHolder.mObservableFormat);
        if (null != avoidHolder.mBaseRequestParams)
            for (BaseRequestParam requestParam : avoidHolder.mBaseRequestParams) builder.addRequest(requestParam);

        if (avoidHolder.isSerial) builder.serial();
        else builder.parallel();

        if (null != avoidHolder.attrs)
            for (AvoidHolder.Attr attr : avoidHolder.attrs) builder.reBindAttrs(attr.tag, attr.o);

        if (avoidHolder.post) builder.post();
        mAvoidHolders.removeFirst();
    }

    private void createList() {

        if (null == mAvoidHolders) mAvoidHolders = new LinkedList<>();
        if (mAvoidHolders.isEmpty()) mAvoidHolders.add(new AvoidHolder());
        if (mAvoidHolders.getLast().post) mAvoidHolders.add(new AvoidHolder());
    }

    class AvoidHolder {

        private boolean post;

        private ObservableFormat mObservableFormat;

        private HashSet<BaseRequestParam> mBaseRequestParams;

        private boolean isSerial;

        private ArrayList<Attr> attrs;

        public void addRequestParam(BaseRequestParam baseRequestParam) {

            if (null == mBaseRequestParams) mBaseRequestParams = new LinkedHashSet<>();
            mBaseRequestParams.add(baseRequestParam);
        }

        public void addAttr(int tag, Object o) {

            if (null == attrs) attrs = new ArrayList<>();
            attrs.add(new Attr(tag, o));
        }

        class Attr {

            int tag;

            Object o;

            public Attr(int tag, Object o) {

                this.tag = tag;
                this.o = o;
            }

        }
    }
}
