package net.lifeupapp.app.utils

import org.lighthousegames.logging.logging


val Any?.logger
    get() = logging((this?.javaClass ?: Any::class.java).toGenericString())