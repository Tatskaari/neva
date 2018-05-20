package neva.routing

/**
 * Annotations to mark up endpoints
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Get(val path: String)
