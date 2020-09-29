package com.github.jpthiery.ed210.query

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/./")
class ed2012 {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "hello"
}