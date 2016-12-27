package com.cnksi.core.okhttp.builder;

import com.cnksi.core.okhttp.OkHttpUtils;
import com.cnksi.core.okhttp.request.OtherRequest;
import com.cnksi.core.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder {
    @Override
    public RequestCall build() {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers).build();
    }
}
