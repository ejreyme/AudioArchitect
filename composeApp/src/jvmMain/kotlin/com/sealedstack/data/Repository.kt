package com.sealedstack.data

/**
 *  `Repository` is an interface that defines four functions: `create`, `read`, `update`, and `delete`
 */
interface Repository<T> {
    /**
     * `create` is a function that takes a single parameter of type `T` and returns a `Unit` (which is Kotlin's equivalent
     * of `void`)
     *
     * @param item The item to be created.
     */
    suspend fun create(item: T)
    /**
     * `read` is a function that takes a generic type `T` and returns a generic type `T`
     *
     * @param item The item to be read.
     */
    suspend fun read(item: T): T
    /**
     * `update` is a function that takes a single parameter of type `T` and returns nothing
     *
     * @param item The item to be updated.
     */
    suspend fun update(item: T)
    /**
     * It deletes the item from the database.
     *
     * @param item The item to be deleted.
     */
    suspend fun delete(item: T)
}