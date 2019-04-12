/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

export interface KotlinTestRunner {
    suite(name: string, isIgnored: boolean, fn: () => void): void

    test(name: string, isIgnored: boolean, fn: () => void): void
}

export const directRunner: KotlinTestRunner = {
    suite(name: string, isIgnored: boolean, fn: () => void): void {
        if (!isIgnored) fn()
    },
    test(name: string, isIgnored: boolean, fn: () => void): void {
        if (!isIgnored) fn()
    }
};