object Modules {

    const val APP = ":app"

    object AndroidLibrary {
        const val CORE = ":libraries:core"
        const val DATA = ":libraries:data"
        const val DOMAIN = ":libraries:domain"
        const val TEST_UTILS = ":libraries:test-utils"
    }

    /**
     * Dynamic Feature Modules
     */
    object DynamicFeature {
        const val POST_DETAIL = ":features:post_detail"
        const val SEARCH = ":features:search"
        const val DASHBOARD = ":features:dashboard"
    }
}