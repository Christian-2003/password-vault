package de.christian2003.feature.autofill.infrastructure.android.services

import android.app.assist.AssistStructure
import android.view.autofill.AutofillId
import de.christian2003.feature.autofill.domain.entities.AutofillPartition
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.infrastructure.android.dto.AssistStructureParserResult
import de.christian2003.feature.autofill.infrastructure.android.mapper.AutofillHintMapper
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
     * Autofill ID of the focused view.
     */
    private var focusedAutofillId: AutofillId? = null

    /**
     * Partition of the data from the focused view.
     */
    private var focusedAutofillPartition: AutofillPartition? = null


    /**
     * Parses the specified assist structure and returns all autofill hints mapped to the autofill
     * ID.
     *
     * @param assistStructure   Assist structure to parse.
     * @return                  Autofill types mapped to a corresponding autofill ID.
     */
    fun parse(assistStructure: AssistStructure): AssistStructureParserResult {
        focusedAutofillId = null
        focusedAutofillPartition = null

        val autofillHints: MutableMap<AutofillType, MutableList<AutofillId>> = mutableMapOf()

        val viewNodes: List<AssistStructure.ViewNode> = flattenAssistStructure(assistStructure)
        viewNodes.forEach { node ->
            val autofillId: AutofillId? = node.autofillId
            if (autofillId != null) {
                val types: List<AutofillType> = parseViewNode(node)
                types.forEach { autofillType ->
                    if (!autofillHints.containsKey(autofillType)) {
                        autofillHints[autofillType] = mutableListOf()
                    }
                    autofillHints[autofillType]!!.add(autofillId)
                }
            }
        }

        if (focusedAutofillId == null || focusedAutofillPartition == null) {
            throw UnsupportedOperationException("Focused ID or partition cannot be determined for specified assist structure")
        }

        val result = AssistStructureParserResult(
            data = autofillHints,
            focusedAutofillId = focusedAutofillId!!,
            focusedAutofillPartition = focusedAutofillPartition!!
        )
        return result
    }


    /**
     * Flattens the provided assist structure and returns all their view nodes in a list.
     *
     * @param structure Assist structure to flatten.
     * @return          Flattened list that contains all view nodes of the provided structure.
     */
    private fun flattenAssistStructure(structure: AssistStructure): List<AssistStructure.ViewNode> {
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
    private fun flattenViewNode(node: AssistStructure.ViewNode): List<AssistStructure.ViewNode> {
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
     * If the node is focused, the attributes "focusedAutofillId" and "focusedAutofillPartition"
     * are set afterwards with data that matches the contents of this node.
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

        if (node.isFocused) {
            focusedAutofillId = node.autofillId
            focusedAutofillPartition = getPartitionForAutofillTypes(types)
        }

        return types
    }


    /**
     * Returns the autofill partition which best matches the list of autofill types. If no partition
     * can be determined, null is returned.
     *
     * @param types List of types for which to determine the partition.
     * @return      Partition which best matches the provided types or null.
     */
    private fun getPartitionForAutofillTypes(types: List<AutofillType>): AutofillPartition? {
        val partitionsCount: MutableMap<AutofillPartition, Int> = mutableMapOf()

        types.forEach { type ->
            if (!partitionsCount.containsKey((type.partition))) {
                partitionsCount[type.partition] = 0
            }
            partitionsCount[type.partition] = partitionsCount[type.partition]!! + 1
        }

        var highestCount = 0
        partitionsCount.values.forEach { count ->
            if (count > highestCount) {
                highestCount = count
            }
        }
        var bestMatch: AutofillPartition? = partitionsCount.entries.find { (_, count) ->
            count == highestCount
        }?.key

        if (bestMatch == null && types.isNotEmpty()) {
            //Fallback: Use first type:
            bestMatch = types.first().partition
        }

        return bestMatch
    }

}
