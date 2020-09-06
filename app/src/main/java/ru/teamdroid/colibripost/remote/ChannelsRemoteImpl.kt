package ru.teamdroid.colibripost.remote

import ru.teamdroid.colibripost.data.ChannelsRemote
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import javax.inject.Inject

class ChannelsRemoteImpl @Inject constructor(
    private val chats: Chats): ChannelsRemote {


    override suspend fun getChannels(chatIds:List<Long>): List<ChannelEntity> {
        return chats.getChannelsFullInfo(chatIds)
    }
}