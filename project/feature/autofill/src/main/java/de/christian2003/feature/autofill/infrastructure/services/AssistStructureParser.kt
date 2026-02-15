package de.christian2003.feature.autofill.infrastructure.services

import android.app.assist.AssistStructure
import android.view.autofill.AutofillId
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.infrastructure.mapper.AutofillHintMapper
import javax.inject.Inject


/**
 * Parser can parse an assist structure and returns autofill types that are mapped to autofill IDs.
 *
 * @param hintMapper    Mapper that maps official android-specific autofill hints to their domain types.
 */
internal class AssistStructureParser @Inject constructor(
    private val hintMapper: AutofillHintMapper
) {

    /**
     * Parses the specified assist structure and returns all autofill hints mapped to the autofill
     * ID.
     *
     * @param assistStructure   Assist structure to parse.
     * @return                  Autofill types mapped to a corresponding autofill ID.
     */
    fun parse(assistStructure: AssistStructure): Map<AutofillId, List<AutofillType>> {
        val autofillHints: MutableMap<AutofillId, List<AutofillType>> = mutableMapOf()

        val viewNodes: List<AssistStructure.ViewNode> = flattenAssistStructure(assistStructure)
        viewNodes.forEach { node ->
            val autofillId: AutofillId? = node.autofillId
            if (autofillId != null) {
                val types: List<AutofillType> = parseViewNode(node)
                autofillHints[autofillId] = types
            }
        }

        return autofillHints
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
