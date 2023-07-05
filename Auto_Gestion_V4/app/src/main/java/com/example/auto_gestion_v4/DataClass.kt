package com.example.auto_gestion_v4

class DataClass {
    var dataId: String? = null
    var dataTitle: String? = null
    var dataDesc: String? = null
    var dataCode: String? = null
    var dataImage: String? = null
    var category: String? = null
    var quantity: Int? = null
    var prix:Double?=null
    constructor(
        dataId: String?, dataTitle: String?, dataDesc: String?, dataCode: String?, dataImage: String?, category: String?,
        quantity: Int, prix:Double?
    ) {
        this.dataId = dataId
        this.dataTitle = dataTitle
        this.dataDesc = dataDesc
        this.dataCode = dataCode
        this.dataImage = dataImage
        this.category = category
        this.quantity= quantity
        this.prix=prix
    }

    constructor() {}
}
