package util

class HttpRequestUtils {
    companion object {
        fun parseQueryString(queryString: String?): Map<String, String> {
            return parseValues(queryString, "&")
        }

        fun parseCookies(cookies: String?): Map<String, String> {
            return parseValues(cookies, ";")
        }

        fun parseHeader(header: String): Pair<String, String>? {
            return getKeyValue(header, ": ")
        }

        private fun parseValues(values: String?, separator: String): Map<String, String> {
            if (values.isNullOrEmpty()) {
                return HashMap()
            }

            val tokens = values.split(separator)

            return tokens.mapNotNull { getKeyValue(it, "=") }.associate { it.first to it.second }
        }

        private fun getKeyValue(keyValue: String?, regex: String): Pair<String, String>? {
            if (keyValue.isNullOrEmpty()) {
                return null
            }

            val tokens = keyValue.split(regex)
            if (tokens.size != 2) {
                return null
            }

            return tokens[0] to tokens[1]
        }
    }
}