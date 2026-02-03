package io.github.archipelagominecraft.core


internal interface Logger{
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
}
internal object LogManager {
    @JvmStatic
    fun getLogger(clazz: Class<*>): Logger {
        return getLogger(clazz.name)
    }

    @JvmStatic
    fun getLogger(name: String): Logger {
        //? if <=1.12.2 {
        /*val logger = org.apache.logging.log4j.LogManager.getLogger(name)
        return object : Logger {
            override fun info(message: String) {
                logger.info(message)
            }

            override fun warn(message: String) {
                logger.warn(message)
            }

            override fun error(message: String) {
                logger.error(message)
            }
        }
        *///?} else {
            val logger = org.slf4j.LoggerFactory.getLogger(name)
            return object : Logger {
                override fun info(message: String) {
                    logger.info(message)
                }

                override fun warn(message: String) {
                    logger.warn(message)
                }

                override fun error(message: String) {
                    logger.error(message)
                }
            }
        //?}
    }
}

