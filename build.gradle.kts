plugins {
    id("java")
}

group = "me.ricky"
version = "1.0"

tasks.jar {
    manifest {
        attributes["Main-Class"] = "me.ricky.grabber.Grabber"
    }
}