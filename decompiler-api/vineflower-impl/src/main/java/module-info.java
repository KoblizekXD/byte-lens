module lol.koblizek.bytelens.core.decompiler.impl.vineflower {
    requires lol.koblizek.bytelens.core.decompiler.api;
    requires vineflower;
    requires org.slf4j;
    requires org.apache.commons.io;
    requires org.objectweb.asm;

    provides lol.koblizek.bytelens.core.decompiler.api.Decompiler with lol.koblizek.bytelens.core.decompiler.impl.vineflower.VineflowerDecompiler;
}