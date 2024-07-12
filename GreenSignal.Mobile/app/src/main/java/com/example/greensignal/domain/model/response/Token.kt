package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.TokenDto

data class Token (
    val token: String
)

fun TokenDto.toToken(): Token {
    return Token(
        token = token
    )
}