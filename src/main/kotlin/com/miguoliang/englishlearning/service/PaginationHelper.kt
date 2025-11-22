package com.miguoliang.englishlearning.service

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 * Reusable utility service that encapsulates pagination pattern.
 * Provides paginate() method that combines Flow<T> data query and suspend count query into Page<T>.
 * Used by all services that need pagination to eliminate boilerplate code.
 */
@Service
class PaginationHelper {
    /**
     * Paginates data from Flow and count into a Page.
     *
     * @param data The Flow containing the paginated data
     * @param count Suspend function returning the total count
     * @param pageable The pagination parameters
     * @return Page with the data and pagination info
     */
    suspend fun <T : Any> paginate(
        data: Flow<T>,
        count: suspend () -> Long,
        pageable: Pageable,
    ): Page<T> =
        coroutineScope {
            val itemsDeferred = async { data.toList() }
            val totalDeferred = async { count() }
            PageImpl(itemsDeferred.await(), pageable, totalDeferred.await())
        }

    /**
     * Paginates data from separate data and count queries.
     * Convenience method that combines dataQuery and countQuery.
     *
     * @param dataQuery The Flow query for paginated data
     * @param countQuery Suspend function for total count
     * @param pageable The pagination parameters
     * @return Page with the data and pagination info
     */
    suspend fun <T : Any> paginateWithQuery(
        dataQuery: Flow<T>,
        countQuery: suspend () -> Long,
        pageable: Pageable,
    ): Page<T> = paginate(dataQuery, countQuery, pageable)
}
