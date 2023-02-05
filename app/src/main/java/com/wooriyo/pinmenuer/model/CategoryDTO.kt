package com.wooriyo.pinmenuer.model

import java.io.Serializable

data class CategoryDTO (
    var idx : Int,
    var name : String,
    var subname : String
): Serializable