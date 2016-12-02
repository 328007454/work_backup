/*
 * Copyright(C)2016 Chengdu TimelyHelp Network Technology Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cnksi.sjjc.inter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @version 1.0
 * @auth lyndon
 * @date 16/2/29
 * @since 1.0
 */
public interface ItemCommonClickListener<T> {
    /**
     * 列表项点击事件
     *
     * @param v 点击view
     * @param t
     * @param position
     */
    void itemClick(View v, T t, int position, int totalCount, ImageView imageView, Button btYanshou, ImageView ivOther);
    void itemClick(View v, T t, int position);

}