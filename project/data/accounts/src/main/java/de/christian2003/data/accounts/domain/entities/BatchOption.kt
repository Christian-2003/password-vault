package de.christian2003.data.accounts.domain.entities


/**
 * Batch options describe what should happen with existing data during batch operations.
 *
 * ===================
 *    E X A M P L E
 * ===================
 * Before performing any batch operation, the following items exist:
 * [{id: 0, name: item1}, {id: 1, name: item2}]
 *
 * Each batch operation happens with the following items:
 * [{id: 1, name: new_item}, {id: 4, name: item4}]
 *
 * After the batch operation, the following items exist:
 * - DeleteAllExisting:   [                      {id: 1, name: new_item}, {id: 4, name: item4}]
 * - ReplaceDuplicates:   [{id: 0, name: item1}, {id: 1, name: new_item}, {id: 4, name: item4}]
 * - SkipDuplicates:      [{id: 0, name: item1}, {id: 1, name: item1},    {id: 4, name: item4}]
 *
 *
 * @property DeleteAllExisting  Before creating a batch of items (e.g. accounts), all existing items
 *                              are deleted.
 * @property ReplaceDuplicates  When creating a batch of items (e.g. accounts), existing items are
 *                              replaced if the item is included in the batch.
 * @property SkipDuplicates     When creating a batch of items (e.g. accounts), all items are skipped
 *                              (i.e. not created) when the item already exists.
 */
enum class BatchOption {

    DeleteAllExisting,
    ReplaceDuplicates,
    SkipDuplicates

}
