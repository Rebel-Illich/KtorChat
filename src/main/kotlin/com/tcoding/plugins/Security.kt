package com.tcoding.plugins

import com.tcoding.session.ChatSession
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("CHAT_SESSION")
    }
}
