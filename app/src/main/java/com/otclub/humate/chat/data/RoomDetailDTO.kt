package com.otclub.humate.chat.data

data class RoomDetailDTO(
    val chatRoomId : String,
    val participateId : String,
    val memberId : String,
    val postId : String,
    val postTitle : String,
    val matchDate : String,
    val matchBranch : String,
    val isClicked : Int,
    val isMatched : Int,
    val targetNickname : String,
    val targetMemberId : String,
    val targetParticipateId : String,
    val targetProfileImgUrl : String? = null,
    val targetIsClicked : Int
)