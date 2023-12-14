package com.tcoding.room

import com.tcoding.data.MessageDataSource
import com.tcoding.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import java.util.concurrent.ConcurrentHashMap


class RoomController(
    private val messageDataSource: MessageDataSource
) {

    private val members = ConcurrentHashMap<String, Member>()


    fun onJoin(
        username: String,
        sessionId: String,
        socketSession: WebSocketSession
    ){
        if(members.containsKey(username)){
            throw MemberAlreadyExistsException()
        }

        members[username] = Member(
            username = username,
            sessionId = sessionId,
            socket =  socketSession
        )

        suspend fun sendMessage(senderUsername: String, message: String){
            members.values.forEach { member ->  
                val messageEntity = Message(
                    text = message,
                    username = senderUsername,
                    timestamp = System.currentTimeMillis().toString()
                )
                messageDataSource.insertMessage(messageEntity)

                val parsedMessage = Json.encodeToString(messageEntity)
                member.socket.send(Frame.Text(parsedMessage))
            }
        }


        suspend fun  getAllMessages(): List<Message>{
            return messageDataSource.getAllMessages()
        }

        suspend fun tryDisconnect(username: String){
            members[username]?.socket?.close()
            if(members.contains(username)){
                members.remove(username)
            }
        }

    }
}