package ru.teamdroid.colibripost.domain.channels

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.remote.core.TelegramException
import javax.inject.Inject

class GetChannels @Inject constructor(
    private val channelsRepository: ChannelsRepository
) : UseCase<List<ChannelEntity>, None>(){

    override suspend fun run(params: None)=
        channelsRepository.getChannels()

}