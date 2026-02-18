package de.christian2003.feature.autofill.infrastructure.android.services

import android.app.assist.AssistStructure
import android.service.autofill.FillContext
import javax.inject.Inject


/**
 * Service can fetch an assist structure from a list of fill contexts.
 */
internal class AssistStructureFetcher @Inject constructor() {

    /**
     * Fetches the focused assist structure from the list of fill contexts. Returns null if no
     * assist structure can be fetched.
     *
     * @param fillContexts  List of fill contexts from which to fetch the focused assist structure.
     * @return              Assist structure or null.
     */
    fun fetchAssistStructure(fillContexts: List<FillContext>): AssistStructure? {
        var focusedAssistStructure: AssistStructure? = null

        fillContexts.forEach { fillContext ->
            val assistStructure: AssistStructure = fillContext.structure

            for (i: Int in 0 until assistStructure.windowNodeCount) {
                val windowNode: AssistStructure.WindowNode = assistStructure.getWindowNodeAt(i)
                val rootViewNode: AssistStructure.ViewNode = windowNode.rootViewNode
                val isFocused: Boolean = isViewNodeFocused(rootViewNode)
                if (isFocused) {
                    focusedAssistStructure = assistStructure
                    return@forEach
                }
            }
        }

        return focusedAssistStructure
    }


    /**
     * Determines whether the specified view node (or one of it's children) is focused.
     *
     * @param node  Node for which to determine whether it is focused.
     * @return      Whether the node or one of it's children is focused.
     */
    private fun isViewNodeFocused(node: AssistStructure.ViewNode): Boolean {
        if (node.isFocused) {
            return true
        }

        for (i: Int in 0 until node.childCount) {
            val childNode: AssistStructure.ViewNode = node.getChildAt(i)
            val isChildFocused: Boolean = isViewNodeFocused(childNode)
            if (isChildFocused) {
                return true
            }
        }

        return false
    }

}
