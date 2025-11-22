package com.miguoliang.englishlearning.service

import com.miguoliang.englishlearning.common.Page
import com.miguoliang.englishlearning.common.Pageable
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

/**
 * Reusable utility service that encapsulates pagination pattern.
 * Provides paginate() method that combines Multi<T> data query and Uni count query into Page<T>.
 * Used by all services that need pagination to eliminate boilerplate code.
 */
@ApplicationScoped
class PaginationHelper {
    /**
     * Paginates data from Multi and count into a Page.
     *
     * @param data The Multi containing the paginated data
     * @param count Uni returning the total count
     * @param pageable The pagination parameters
     * @return Uni<Page> with the data and pagination info
     */
    fun <T : Any> paginate(
        data: Multi<T>,
        count: Uni<Long>,
        pageable: Pageable,
    ): Uni<Page<T>> {
        val itemsUni = data.collect().asList()

        return Uni.combine().all().unis(itemsUni, count).asTuple()
            .map { tuple ->
                Page(
                    content = tuple.item1,
                    number = pageable.page,
                    size = pageable.size,
                    totalElements = tuple.item2
                )
            }
    }

    /**
     * Paginates data from separate data and count queries.
     * Convenience method that combines dataQuery and countQuery.
     *
     * @param dataQuery The Multi query for paginated data
     * @param countQuery Uni for total count
     * @param pageable The pagination parameters
     * @return Uni<Page> with the data and pagination info
     */
    fun <T : Any> paginateWithQuery(
        dataQuery: Multi<T>,
        countQuery: Uni<Long>,
        pageable: Pageable,
    ): Uni<Page<T>> = paginate(dataQuery, countQuery, pageable)
}
