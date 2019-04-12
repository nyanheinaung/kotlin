/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package mobi.porquenao.poc.kotlin.core

import com.raizlabs.android.dbflow.sql.language.Select

object ItemRepository {

    fun getAll(): MutableList<Item> {
        return Select()
                .from(Item::class.java)
                .where()
                .orderBy(Item_Table.updated_at, false)
                .queryList()
    }

}
