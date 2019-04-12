/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.clean

import org.jetbrains.kotlin.js.backend.ast.*

internal class DeadCodeElimination(private val root: JsStatement) {
    var hasChanges = false

    fun apply(): Boolean {
        EliminationVisitor().accept(root)
        return hasChanges
    }

    inner class EliminationVisitor : RecursiveJsVisitor() {
        var breakLabels = mutableSetOf<JsName>()
        var localBreakExists = false
        var canContinue = false

        override fun visitBreak(x: JsBreak) {
            val name = x.label?.name
            if (name != null) {
                breakLabels.add(name)
            }
            else {
                localBreakExists = true
            }
            canContinue = false
        }

        override fun visitContinue(x: JsContinue) {
            canContinue = false
        }

        override fun visitLabel(x: JsLabel) {
            accept(x.statement)
            if (!canContinue && x.name in breakLabels) {
                canContinue = true
            }
            breakLabels.remove(x.name)
        }

        override fun visitBlock(x: JsBlock) {
            canContinue = true
            visitStatements(x.statements)
        }

        private fun visitStatements(statements: MutableList<JsStatement>) {
            for ((index, statement) in statements.withIndex()) {
                accept(statement)
                if (!canContinue) {
                    val removedStatements = statements.subList(index + 1, statements.size)
                    if (removedStatements.isNotEmpty()) {
                        hasChanges = true
                        removedStatements.clear()
                    }
                    break
                }
            }
        }

        override fun visitWhile(x: JsWhile) {
            EliminationVisitor().accept(x.condition)

            visitLoop(x.body) {
                val condition = x.condition
                condition !is JsBooleanLiteral || !condition.value
            }
        }

        override fun visitDoWhile(x: JsDoWhile) = visitWhile(x)

        override fun visitFor(x: JsFor) {
            EliminationVisitor().accept(x.condition)
            EliminationVisitor().accept(x.initExpression)
            EliminationVisitor().accept(x.initVars)
            EliminationVisitor().accept(x.incrementExpression)

            // TODO: We may also check if condition is `true` constant or missing, which means this loop is infinite, i.e.
            // code after this loop can be safely deleted when loop does not contain break statements.
            visitLoop(x.body) { true }
        }

        override fun visitForIn(x: JsForIn) {
            EliminationVisitor().accept(x.iterExpression)
            visitLoop(x.body) { true }
        }

        private fun visitLoop(body: JsStatement?, additionalExitCondition: () -> Boolean) {
            val localBreakExistsBackup = localBreakExists

            localBreakExists = false
            if (body != null) {
                accept(body)
            }
            if (!canContinue) {
                canContinue = additionalExitCondition() || localBreakExists
            }

            localBreakExists = localBreakExistsBackup
        }

        override fun visitIf(x: JsIf) {
            EliminationVisitor().accept(x.ifExpression)

            var result = false

            accept(x.thenStatement)
            if (canContinue) {
                result = true
            }

            val elseStatement = x.elseStatement
            if (elseStatement != null) {
                accept(x.elseStatement)
                if (canContinue) {
                    result = true
                }
            }
            else {
                result = true
            }

            canContinue = result
        }

        override fun visitTry(x: JsTry) {
            var result = false

            accept(x.tryBlock)
            if (canContinue) {
                result = true
            }

            for (catchBlock in x.catches) {
                accept(catchBlock.body)
                if (canContinue) {
                    result = true
                }
            }

            val finallyBlock = x.finallyBlock
            if (finallyBlock != null) {
                accept(finallyBlock)
                if (!canContinue) {
                    result = false
                }
            }

            canContinue = result
        }

        override fun visitExpressionStatement(x: JsExpressionStatement) {
            EliminationVisitor().accept(x.expression)
            canContinue = true
        }

        override fun visit(x: JsSwitch) {
            EliminationVisitor().accept(x.expression)
            val localBreakExistsBackup = localBreakExists

            var defaultCanContinue = true
            var allCasesCantContinue = true

            for (caseBlock in x.cases) {
                canContinue = true
                visitStatements(caseBlock.statements)

                if (!canContinue && localBreakExists) {
                    canContinue = true
                }

                if (caseBlock is JsDefault) {
                    defaultCanContinue = canContinue
                }
                else if (allCasesCantContinue && canContinue) {
                    allCasesCantContinue = false
                }
            }

            canContinue = !allCasesCantContinue || defaultCanContinue
            localBreakExists = localBreakExistsBackup
        }

        override fun visitThrow(x: JsThrow) {
            canContinue = false
        }

        override fun visitReturn(x: JsReturn) {
            canContinue = false
        }

        override fun visitVars(x: JsVars) {
            x.vars.forEach { EliminationVisitor().accept(it) }
            canContinue = true
        }
    }
}
