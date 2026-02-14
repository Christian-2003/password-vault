package de.christian2003.feature.autofill.infrastructure.services

import android.app.assist.AssistStructure
import android.service.autofill.FillContext
import android.view.autofill.AutofillId
import de.christian2003.feature.autofill.domain.entities.AutofillType
import javax.inject.Inject


/**
 * Parser can parse a list of fill contexts and returns autofill types that are mapped to autofill
 * IDs.
 *
 * @param hintMapper    Mapper that maps official android-specific autofill hints to their domain types.
 */
internal class FillContextParser @Inject constructor(
    private val hintMapper: AutofillHintMapper
) {

    /**
     * Parses the specified fill contexts by selecting the focused assist structure. From the focused
     * assist structure, all autofill hints are mapped to the autofill ID and returned.
     *
     * @param fillContexts  List of fill contexts to parse.
     * @return              Autofill types mapped to a corresponding autofill ID.
     */
    fun parse(fillContexts: List<FillContext>): Map<AutofillId, List<AutofillType>> {
        val focusedAssistStructure: AssistStructure? = getFocusedAssistStructure(fillContexts)

        val autofillHints: MutableMap<AutofillId, List<AutofillType>> = mutableMapOf()

        if (focusedAssistStructure != null) {
            val viewNodes: List<AssistStructure.ViewNode> = flattenAssistStructure(focusedAssistStructure)
            viewNodes.forEach { node ->
                val autofillId: AutofillId? = node.autofillId
                if (autofillId != null) {
                    val types: List<AutofillType> = parseViewNode(node)
                    autofillHints[autofillId] = types
                }
            }
        }

        return autofillHints
    }


    /**
     * Finds the assist structure that is focused by the user. If no structure is focused by the
     * user, null is returned.
     *
     * @param fillContexts  Fill contexts containing the assist structures.
     * @return              Focused assist structure or null.
     */
    private fun getFocusedAssistStructure(fillContexts: List<FillContext>): AssistStructure? {
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


    /**
     * Flattens the provided assist structure and returns all their view nodes in a list.
     *
     * @param structure Assist structure to flatten.
     * @return          Flattened list that contains all view nodes of the provided structure.
     */
    fun flattenAssistStructure(structure: AssistStructure): List<AssistStructure.ViewNode> {
        val flattenedNodes: MutableList<AssistStructure.ViewNode> = mutableListOf()

        for (i: Int in 0 until structure.windowNodeCount) {
            val windowNode: AssistStructure.WindowNode = structure.getWindowNodeAt(i)
            val rootViewNode: AssistStructure.ViewNode = windowNode.rootViewNode
            val flattenedViewNodes: List<AssistStructure.ViewNode> = flattenViewNode(rootViewNode)
            flattenedNodes.addAll(flattenedViewNodes)
        }

        return flattenedNodes
    }


    /**
     * Flattens the specified view node and returns it (and all their children) in a list.
     *
     * @param node  View node to flatten.
     * @return      Flattened list that contains all view nodes.
     */
    fun flattenViewNode(node: AssistStructure.ViewNode): List<AssistStructure.ViewNode> {
        val flattenedNodes: MutableList<AssistStructure.ViewNode> = mutableListOf()

        flattenedNodes.add(node)

        for (i: Int in 0 until node.childCount) {
            val childNode: AssistStructure.ViewNode = node.getChildAt(i)
            val flattenedChildNode: List<AssistStructure.ViewNode> = flattenViewNode(childNode)
            flattenedNodes.addAll(flattenedChildNode)
        }

        return flattenedNodes
    }


    /**
     * Parses the provided view node and returns a list of autofill types for the node.
     *
     * @param node  View node to parse.
     * @return      List of autofill types from the node.
     */
    private fun parseViewNode(node: AssistStructure.ViewNode): List<AutofillType> {
        val hints: List<String>? = node.autofillHints?.toList()
        val types: MutableList<AutofillType> = mutableListOf()

        hints?.forEach { hint ->
            val type: AutofillType? = hintMapper.toDomain(hint)
            if (type != null) {
                types.add(type)
            }
        }

        return types
    }

}
