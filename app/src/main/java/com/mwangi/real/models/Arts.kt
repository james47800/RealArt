package com.mwangi.real.models

class Arts {
    var imageUrl:String = ""
    var userDescription:String = ""
    var userId:String = ""
    var imageId:String = ""

    constructor(imageUrl: String, userName: String, userId: String, imageId: String) {
        this.imageUrl = imageUrl
        this.userDescription = userName
        this.userId = userId
        this.imageId = imageId
    }
    constructor()

}