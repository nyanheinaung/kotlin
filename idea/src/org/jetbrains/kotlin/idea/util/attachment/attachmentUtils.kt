/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.util.attachment

import com.intellij.diagnostic.AttachmentFactory
import com.intellij.openapi.diagnostic.Attachment
import com.intellij.psi.PsiFile

fun attachmentByPsiFileAsArray(file: PsiFile?): Array<Attachment> {
    val attachment = attachmentByPsiFile(file)
    if (attachment == null) {
        return arrayOf()
    }
    return arrayOf(attachment)
}

fun attachmentByPsiFile(file: PsiFile?): Attachment? {
    if (file == null) return null

    val virtualFile = file.virtualFile
    if (virtualFile != null) return AttachmentFactory.createAttachment(virtualFile)

    val text = try { file.text
    } catch(e: Exception) { null }
    val name = try { file.name
    } catch(e: Exception) { null }

    if (text != null && name != null) return Attachment(name, text)

    return null
}

fun mergeAttachments(vararg attachments: Attachment?): Attachment {
    val builder = StringBuilder()
    attachments.forEach {
        if (it != null) {
            builder.append("----- START ${it.path} -----\n")
            builder.append(it.displayText)
            builder.append("\n----- END ${it.path} -----\n\n")
        }
    }

    return Attachment("message.txt", builder.toString())
}
