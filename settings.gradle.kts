rootProject.name = "ByteLens"
include("decompiler-api")
include("decompiler-api:vineflower-impl")
findProject(":decompiler-api:vineflower-impl")?.name = "vineflower-impl"
