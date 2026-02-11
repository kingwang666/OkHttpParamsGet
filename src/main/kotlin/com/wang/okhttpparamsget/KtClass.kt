package com.wang.okhttpparamsget

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

fun deleteFunctionFromKtClass(ktClass: KtClass, methodName: String) {
    val body = ktClass.getBody() ?: return

    val targetFun = body.declarations
        .filterIsInstance<KtNamedFunction>()
        .firstOrNull { it.name == methodName }
        ?: return
    targetFun.delete()
}
