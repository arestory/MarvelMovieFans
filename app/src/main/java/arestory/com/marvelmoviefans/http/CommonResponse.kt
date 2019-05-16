package com.ares.movie.http

class CommonResponse<T> {


    var code = 200
    var msg = "success"
    var data: T? = null

    constructor(code: Int, msg: String, data: T) {
        this.code = code
        this.msg = msg
        this.data = data
    }



    constructor(data: T) {
        this.data = data
    }
}
